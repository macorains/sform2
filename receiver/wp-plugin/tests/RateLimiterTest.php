<?php
/**
 * MacolaboSformRateLimiter のテスト
 */
class RateLimiterTest extends WP_UnitTestCase {

    use MsformTestSetup;

    /** ウィンドウ境界をまたがないよう十分長くする（1年） */
    const LONG_WINDOW = 31536000;

    public function set_up() {
        parent::set_up();
        $this->set_up_msform();
        // guard() の拒否は wp_send_json_error() → wp_die() で終了するため、例外に置き換えて捕捉する
        add_filter('wp_doing_ajax', '__return_true');
        add_filter('wp_die_ajax_handler', function () {
            return function () {
                throw new MsformRateLimitRejected();
            };
        }, 99);
        $this->use_rate_limit_rules([
            'msform_save_form' => [
                'ip_ua' => [self::LONG_WINDOW, 2],
                'ip'    => [self::LONG_WINDOW, 5],
            ],
        ]);
    }

    public function tear_down() {
        $this->tear_down_msform();
        parent::tear_down();
    }

    /**
     * guard() を呼び、許可なら null、拒否ならレスポンス JSON（配列）を返す
     */
    private function guard($action = 'msform_save_form', $ip = null, $ua = null) {
        if ($ip !== null) {
            $this->set_client($ip, $ua);
        }
        ob_start();
        try {
            MacolaboSformRateLimiter::guard($action);
            ob_end_clean();
            return null;
        } catch (MsformRateLimitRejected $e) {
            return json_decode(ob_get_clean(), true);
        }
    }

    private function results($n, $action = 'msform_save_form') {
        $results = [];
        for ($i = 0; $i < $n; $i++) {
            $results[] = $this->guard($action) === null ? 'ok' : 'rejected';
        }
        return $results;
    }

    // ---- テーブル / Cron ----

    public function test_table_is_installed() {
        global $wpdb;
        $table = MacolaboSformRateLimiter::table_name();
        $this->assertSame($table, $wpdb->get_var($wpdb->prepare('SHOW TABLES LIKE %s', $table)));
        $this->assertSame(MacolaboSformRateLimiter::DB_VERSION, get_option(MacolaboSformRateLimiter::DB_VERSION_OPTION));
    }

    public function test_cleanup_is_scheduled_hourly() {
        $this->assertNotFalse(wp_next_scheduled(MacolaboSformRateLimiter::CLEANUP_HOOK));
        $this->assertSame('hourly', wp_get_schedule(MacolaboSformRateLimiter::CLEANUP_HOOK));
    }

    public function test_deactivate_unschedules_cleanup() {
        MacolaboSformRateLimiter::deactivate();
        $this->assertFalse(wp_next_scheduled(MacolaboSformRateLimiter::CLEANUP_HOOK));
    }

    public function test_cleanup_deletes_only_expired_rows() {
        global $wpdb;
        $table = MacolaboSformRateLimiter::table_name();
        $wpdb->insert($table, ['rl_key' => str_repeat('a', 64), 'hit_count' => 1, 'expires_at' => time() - 10]);
        $wpdb->insert($table, ['rl_key' => str_repeat('b', 64), 'hit_count' => 1, 'expires_at' => time() + 600]);

        MacolaboSformRateLimiter::cleanup();

        $this->assertSame([str_repeat('b', 64)], $wpdb->get_col("SELECT rl_key FROM {$table}"));
    }

    // ---- 制限ルール ----

    public function test_default_rules() {
        $rules = MacolaboSformRateLimiter::RULES;
        $this->assertSame(['ip_ua' => [60, 2], 'ip' => [3600, 20]], $rules['msform_save_form']);
        $this->assertSame(['ip_ua' => [60, 10], 'ip' => [60, 30]], $rules['msform_confirm_form']);
        $this->assertSame(['ip_ua' => [60, 20], 'ip' => [60, 60]], $rules['msform_validate_form']);
        $this->assertSame(['ip_ua' => [60, 20], 'ip' => [60, 60]], $rules['msform_load_form']);
    }

    public function test_same_ip_and_ua_is_rejected_after_limit() {
        $this->assertSame(['ok', 'ok', 'rejected', 'rejected'], $this->results(4));
    }

    public function test_rejection_response() {
        $this->results(2);
        $response = $this->guard();

        $this->assertFalse($response['success']);
        $this->assertSame('too_many_requests', $response['data']['code']);
        $this->assertGreaterThanOrEqual(1, $response['data']['retry_after']);
        $this->assertLessThanOrEqual(self::LONG_WINDOW, $response['data']['retry_after']);
    }

    public function test_other_ua_on_same_ip_is_allowed() {
        $this->results(3);
        $this->assertNull($this->guard('msform_save_form', '192.0.2.10', 'OtherBrowser/1.0'));
    }

    public function test_other_ip_with_same_ua_is_allowed() {
        $this->results(3);
        $this->assertNull($this->guard('msform_save_form', '192.0.2.11', 'MsformTest/1.0'));
    }

    public function test_rotating_ua_is_rejected_by_ip_tier() {
        $results = [];
        for ($i = 0; $i < 7; $i++) {
            $results[] = $this->guard('msform_save_form', '198.51.100.1', "Rotating/{$i}") === null ? 'ok' : 'rejected';
        }
        $this->assertSame(['ok', 'ok', 'ok', 'ok', 'ok', 'rejected', 'rejected'], $results);
    }

    public function test_requests_rejected_by_ip_ua_tier_are_not_counted_in_ip_tier() {
        // 同一 IP+UA で 10 回（2 回許可、8 回拒否）しても、IP 段のカウントは 2 のまま
        $this->results(10);
        $results = [];
        for ($i = 0; $i < 4; $i++) {
            $results[] = $this->guard('msform_save_form', '192.0.2.10', "Other/{$i}") === null ? 'ok' : 'rejected';
        }
        $this->assertSame(['ok', 'ok', 'ok', 'rejected'], $results);
    }

    public function test_limits_are_per_action() {
        $this->use_rate_limit_rules([
            'msform_save_form'    => ['ip_ua' => [self::LONG_WINDOW, 1]],
            'msform_confirm_form' => ['ip_ua' => [self::LONG_WINDOW, 1]],
        ]);
        $this->assertNull($this->guard('msform_save_form'));
        $this->assertNull($this->guard('msform_confirm_form'));
        $this->assertNotNull($this->guard('msform_save_form'));
    }

    public function test_action_without_rule_is_not_limited() {
        $this->assertSame(array_fill(0, 5, 'ok'), $this->results(5, 'msform_unknown_action'));
    }

    public function test_unknown_tier_is_ignored() {
        $this->use_rate_limit_rules([
            'msform_save_form' => ['session' => [self::LONG_WINDOW, 1]],
        ]);
        $this->assertSame(['ok', 'ok', 'ok'], $this->results(3));
    }

    public function test_keys_do_not_contain_raw_ip_or_ua() {
        global $wpdb;
        $this->guard('msform_save_form', '203.0.113.50', 'SecretAgent/9.9');
        $keys = $wpdb->get_col('SELECT rl_key FROM ' . MacolaboSformRateLimiter::table_name());

        $this->assertCount(2, $keys);
        foreach ($keys as $key) {
            $this->assertMatchesRegularExpression('/^[0-9a-f]{64}$/', $key);
        }
    }

    public function test_db_error_fails_open() {
        global $wpdb;
        $table = MacolaboSformRateLimiter::table_name();
        add_filter('query', function ($query) use ($table) {
            return strpos($query, "INSERT INTO {$table}") === 0 ? 'SELECT * FROM msform_table_does_not_exist' : $query;
        });
        $suppress = $wpdb->suppress_errors(true);

        $results = $this->results(5);

        $wpdb->suppress_errors($suppress);
        $this->assertSame(array_fill(0, 5, 'ok'), $results);
    }

    // ---- クライアント IP ----

    /**
     * @dataProvider client_ip_provider
     */
    public function test_client_ip($remote_addr, $expected) {
        $_SERVER['REMOTE_ADDR'] = $remote_addr;
        $this->assertSame($expected, MacolaboSformRateLimiter::client_ip());
    }

    public static function client_ip_provider() {
        return [
            'IPv4'                     => ['203.0.113.5', '203.0.113.5'],
            'IPv6 は /64 にまとめる'     => ['2001:db8:1:2:3:4:5:6', '2001:db8:1:2::/64'],
            'IPv6 同一 /64 は同じ値'     => ['2001:db8:1:2:ffff::1', '2001:db8:1:2::/64'],
            'IPv4-mapped IPv6'         => ['::ffff:203.0.113.5', '203.0.113.5'],
            'IPv6 ループバック'          => ['::1', '::/64'],
            '不正な値'                  => ['not-an-ip', 'unknown'],
            '空'                       => ['', 'unknown'],
        ];
    }

    public function test_client_ip_without_remote_addr() {
        unset($_SERVER['REMOTE_ADDR']);
        $this->assertSame('unknown', MacolaboSformRateLimiter::client_ip());
    }

    public function test_client_ip_filter() {
        $_SERVER['REMOTE_ADDR'] = '10.0.0.1';
        add_filter('msform_client_ip', function () {
            return '203.0.113.99';
        });
        $this->assertSame('203.0.113.99', MacolaboSformRateLimiter::client_ip());
    }

    public function test_ip_ignores_x_forwarded_for() {
        $_SERVER['REMOTE_ADDR'] = '10.0.0.1';
        $_SERVER['HTTP_X_FORWARDED_FOR'] = '203.0.113.1';
        $this->assertSame('10.0.0.1', MacolaboSformRateLimiter::client_ip());
    }
}

class MsformRateLimitRejected extends Exception {}
