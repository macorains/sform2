<?php
/**
 * RateLimiterConcurrencyTest から並列に起動される子プロセス
 *
 * テスト用 WordPress（wp-tests-config.php の DB）をプラグイン付きで読み込み、
 * 指定時刻まで待ってから guard() を1回呼び、結果（ok / rejected）を標準出力に書く。
 *
 * usage: php concurrent-guard.php <action> <ip> <ua> <window> <limit> <start_at(float)>
 */

list(, $action, $ip, $ua, $window, $limit, $start_at) = $argv;

require dirname(__DIR__) . '/wp-tests-config.php';

$_SERVER['HTTP_HOST'] = WP_TESTS_DOMAIN;
$_SERVER['SERVER_NAME'] = WP_TESTS_DOMAIN;
$_SERVER['REQUEST_METHOD'] = 'POST';
$_SERVER['REQUEST_URI'] = '/wp-admin/admin-ajax.php';
ini_set('error_log', dirname(__DIR__) . '/tmp/php-error.log');

// wp-settings.php 読み込み前にフックを登録する（tests_add_filter と同じ方式）
$GLOBALS['wp_filter']['muplugins_loaded'][10]['msform_test_load_plugin'] = [
    'function' => function () {
        require dirname(__DIR__, 2) . '/macolabo-sform.php';
    },
    'accepted_args' => 1,
];

define('WP_USE_THEMES', false);
define('DISABLE_WP_CRON', true);
define('WP_DEBUG_DISPLAY', false);   // 標準出力は結果の受け渡しに使うため、エラーはログへ
require ABSPATH . 'wp-settings.php';

add_filter('msform_rate_limit_rules', function () use ($action, $window, $limit) {
    return [$action => ['ip_ua' => [(int) $window, (int) $limit]]];
});
add_filter('wp_doing_ajax', '__return_true');
add_filter('wp_die_ajax_handler', function () {
    return function () {
        throw new RuntimeException('rejected');
    };
}, 99);

$_SERVER['REMOTE_ADDR'] = $ip;
$_SERVER['HTTP_USER_AGENT'] = $ua;

// 全プロセスがほぼ同時に guard() を呼ぶよう、開始時刻まで待つ
while (microtime(true) < (float) $start_at) {
    usleep(1000);
}

ob_start();
try {
    MacolaboSformRateLimiter::guard($action);
    $result = 'ok';
} catch (RuntimeException $e) {
    $result = 'rejected';
}
ob_end_clean();

echo $result;
