<?php
/**
 * 同一クライアントからの連続リクエストを制限するクラス
 *
 * 2段構えで制限する。
 *   - ip_ua : IPアドレス + UserAgent 単位（厳しめ）。NAT配下の別ユーザーを巻き込まないための主な制限。
 *   - ip    : IPアドレス単位（緩め）。UserAgentを変えながらの連続リクエストへの歯止め。
 *
 * カウンタは Transient API に固定ウィンドウ方式で保存する（object cache があればそちらが使われる）。
 * キーは IP/UA を HMAC でハッシュ化したもので、生の値は保存しない。
 */
class MacolaboSformRateLimiter {

    /**
     * action ごとの制限ルール  [ウィンドウ秒数, 上限回数]
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
     * 制限を超えていれば 429 を返して処理を終了する。超えていなければカウントして戻る。
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
        $buckets = [];
        foreach ($rules[$action] as $tier => $rule) {
            list($window, $limit) = $rule;
            $key = self::transient_key($action, $tier, $identities[$tier]);
            $bucket = get_transient($key);
            if (!is_array($bucket) || $bucket['reset_at'] <= $now) {
                $bucket = ['count' => 0, 'reset_at' => $now + $window];
            }
            if ($bucket['count'] >= $limit) {
                self::reject($action, $tier, max(1, $bucket['reset_at'] - $now));
            }
            $buckets[$key] = $bucket;
        }

        // 全段で上限内の場合のみカウントする（拒否したリクエストはカウントしない）
        foreach ($buckets as $key => $bucket) {
            $bucket['count']++;
            set_transient($key, $bucket, max(1, $bucket['reset_at'] - $now));
        }
    }

    /**
     * クライアントIPを取得する。
     * X-Forwarded-For は偽装可能なため使わない。リバースプロキシ配下の場合は
     * msform_client_ip フィルタで信頼できる値に差し替えること。
     * IPv6 はアドレスを変えながらの回避を防ぐため /64 単位にまとめる。
     */
    public static function client_ip() {
        $ip = isset($_SERVER['REMOTE_ADDR']) ? (string) $_SERVER['REMOTE_ADDR'] : '';
        $ip = (string) apply_filters('msform_client_ip', $ip);

        if (filter_var($ip, FILTER_VALIDATE_IP, FILTER_FLAG_IPV6)) {
            $packed = inet_pton($ip);
            return inet_ntop(substr($packed, 0, 8) . str_repeat("\0", 8)) . '/64';
        }
        if (filter_var($ip, FILTER_VALIDATE_IP)) {
            return $ip;
        }
        return 'unknown';
    }

    private static function transient_key($action, $tier, $identity) {
        return 'msform_rl_' . hash_hmac('sha256', $action . '|' . $tier . '|' . $identity, wp_salt('nonce'));
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
