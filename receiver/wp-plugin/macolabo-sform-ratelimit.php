<?php
/**
 * 同一クライアントからの連続リクエストを制限するクラス
 *
 * 2段構えで制限する。
 *   - ip_ua : IPアドレス + UserAgent 単位（厳しめ）。NAT配下の別ユーザーを巻き込まないための主な制限。
 *   - ip    : IPアドレス単位（緩め）。UserAgentを変えながらの連続リクエストへの歯止め。
 *
 * カウンタは専用テーブルに固定ウィンドウ方式で保存し、
 * INSERT ... ON DUPLICATE KEY UPDATE で原子的にインクリメントする（並列リクエストでもすり抜けない）。
 * キーは IP/UA を HMAC でハッシュ化したもので、生の値は保存しない。
 * 期限切れの行は WP-Cron で1時間ごとに削除する。
 */
class MacolaboSformRateLimiter {

    const DB_VERSION = '1';
    const DB_VERSION_OPTION = 'msform_rate_limit_db_version';
    const CLEANUP_HOOK = 'msform_rate_limit_cleanup';

    /**
     * action ごとの制限ルール  [ウィンドウ秒数, 上限回数]
     * 段は ip_ua → ip の順に評価する（ip_ua で拒否されたリクエストは ip の回数に数えない）。
     */
    const RULES = [
        'msform_save_form' => [
            'ip_ua' => [60, 2],
            'ip'    => [3600, 20],
        ],
        'msform_confirm_form' => [
            'ip_ua' => [60, 10],
            'ip'    => [60, 30],
        ],
        'msform_validate_form' => [
            'ip_ua' => [60, 20],
            'ip'    => [60, 60],
        ],
        'msform_load_form' => [
            'ip_ua' => [60, 20],
            'ip'    => [60, 60],
        ],
    ];

    /**
     * フック登録（プラグイン読み込み時に1回呼ぶ）
     */
    public static function init() {
        add_action('plugins_loaded', [__CLASS__, 'maybe_install']);
        add_action(self::CLEANUP_HOOK, [__CLASS__, 'cleanup']);
        add_action('init', function() {
            if (!wp_next_scheduled(self::CLEANUP_HOOK)) {
                wp_schedule_event(time(), 'hourly', self::CLEANUP_HOOK);
            }
        });
    }

    public static function table_name() {
        global $wpdb;
        return $wpdb->prefix . 'msform_rate_limit';
    }

    /**
     * テーブルが未作成・旧バージョンの場合に作成/更新する
     */
    public static function maybe_install() {
        if (get_option(self::DB_VERSION_OPTION) === self::DB_VERSION) {
            return;
        }
        global $wpdb;
        require_once ABSPATH . 'wp-admin/includes/upgrade.php';
        $table = self::table_name();
        $charset_collate = $wpdb->get_charset_collate();
        dbDelta("CREATE TABLE {$table} (
  rl_key char(64) NOT NULL,
  hit_count int unsigned NOT NULL DEFAULT 0,
  expires_at int unsigned NOT NULL,
  PRIMARY KEY  (rl_key),
  KEY expires_at (expires_at)
) {$charset_collate};");
        update_option(self::DB_VERSION_OPTION, self::DB_VERSION);
    }

    /**
     * プラグイン無効化時の後始末
     */
    public static function deactivate() {
        wp_clear_scheduled_hook(self::CLEANUP_HOOK);
    }

    /**
     * 期限切れのカウンタを削除する
     */
    public static function cleanup() {
        global $wpdb;
        $table = self::table_name();
        $wpdb->query($wpdb->prepare("DELETE FROM {$table} WHERE expires_at <= %d", time()));
    }

    /**
     * 制限を超えていれば 429 を返して処理を終了する。超えていなければ戻る。
     */
    public static function guard($action) {
        $rules = apply_filters('msform_rate_limit_rules', self::RULES);
        if (!isset($rules[$action])) {
            return;
        }

        $ip = self::client_ip();
        $ua = isset($_SERVER['HTTP_USER_AGENT']) ? (string) $_SERVER['HTTP_USER_AGENT'] : '';
        $identities = [
            'ip_ua' => $ip . '|' . $ua,
            'ip'    => $ip,
        ];

        $now = time();
        foreach ($rules[$action] as $tier => $rule) {
            if (!isset($identities[$tier])) {
                _log("msform rate limit: unknown tier '{$tier}' for action {$action} ignored");
                continue;
            }
            list($window, $limit) = $rule;
            $window_start = $now - ($now % $window);
            $expires_at = $window_start + $window;
            $key = hash_hmac('sha256', $action . '|' . $tier . '|' . $identities[$tier] . '|' . $window_start, wp_salt('nonce'));

            $count = self::increment($key, $expires_at);
            if ($count !== null && $count > $limit) {
                self::reject($action, $tier, max(1, $expires_at - $now));
            }
        }
    }

    /**
     * カウンタを原子的に +1 し、加算後の値を返す。DBエラー時は null（制限せず通す）。
     */
    private static function increment($key, $expires_at) {
        global $wpdb;
        $table = self::table_name();
        // 更新時は LAST_INSERT_ID(expr) で加算後の値を同じクエリ内で取得する
        $affected = $wpdb->query($wpdb->prepare(
            "INSERT INTO {$table} (rl_key, hit_count, expires_at) VALUES (%s, 1, %d)
             ON DUPLICATE KEY UPDATE hit_count = LAST_INSERT_ID(hit_count + 1)",
            $key, $expires_at
        ));
        if ($affected === false) {
            _log('msform rate limit: DB error: ' . $wpdb->last_error);
            return null;
        }
        // 1: 新規挿入, 2: 既存行の更新
        return $affected === 1 ? 1 : (int) $wpdb->insert_id;
    }

    /**
     * クライアントIPを取得する。
     * X-Forwarded-For は偽装可能なため使わない。リバースプロキシ配下の場合は
     * msform_client_ip フィルタで信頼できる値に差し替えること。
     * IPv4-mapped IPv6 (::ffff:a.b.c.d) は IPv4 に戻す。
     * IPv6 はアドレスを変えながらの回避を防ぐため /64 単位にまとめる。
     */
    public static function client_ip() {
        $ip = isset($_SERVER['REMOTE_ADDR']) ? (string) $_SERVER['REMOTE_ADDR'] : '';
        $ip = (string) apply_filters('msform_client_ip', $ip);

        if (filter_var($ip, FILTER_VALIDATE_IP, FILTER_FLAG_IPV6)) {
            $packed = inet_pton($ip);
            if (substr($packed, 0, 12) === str_repeat("\0", 10) . "\xff\xff") {
                return inet_ntop(substr($packed, 12));
            }
            return inet_ntop(substr($packed, 0, 8) . str_repeat("\0", 8)) . '/64';
        }
        if (filter_var($ip, FILTER_VALIDATE_IP)) {
            return $ip;
        }
        return 'unknown';
    }

    private static function reject($action, $tier, $retry_after) {
        _log("msform rate limit exceeded: action={$action} tier={$tier}");
        header('Retry-After: ' . $retry_after);
        wp_send_json_error([
            'code' => 'too_many_requests',
            'retry_after' => $retry_after,
        ], 429);
    }
}
