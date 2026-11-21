<?php
/**
 * PHPUnit bootstrap
 *
 * - formApi のモックサーバー（tests/mock-api/router.php）を起動する
 * - WordPress テストライブラリ (wp-phpunit) でテスト用 WordPress を初期化し、プラグインを読み込む
 */

$plugin_dir = dirname(__DIR__);

if (!file_exists($plugin_dir . '/vendor/autoload.php')) {
    fwrite(STDERR, "vendor/ がありません。receiver/wp-plugin で composer install を実行してください。\n");
    exit(1);
}

$tmp_dir = __DIR__ . '/tmp';
if (!is_dir($tmp_dir)) {
    mkdir($tmp_dir, 0777, true);
}
// プラグインの _log() 出力でテスト結果が見づらくならないよう、PHP のエラーログはファイルへ
ini_set('error_log', $tmp_dir . '/php-error.log');

putenv('WP_PHPUNIT__TESTS_CONFIG=' . __DIR__ . '/wp-tests-config.php');
require $plugin_dir . '/vendor/autoload.php';

require __DIR__ . '/includes/MsformMockApi.php';
require __DIR__ . '/includes/MsformTestSetup.php';

MsformMockApi::start(
    (int) (getenv('MSFORM_MOCK_API_PORT') ?: 18089),
    __DIR__ . '/mock-api/router.php',
    $tmp_dir . '/mock-api-requests.log'
);

$_tests_dir = getenv('WP_PHPUNIT__DIR');
require_once $_tests_dir . '/includes/functions.php';

tests_add_filter('muplugins_loaded', function () use ($plugin_dir) {
    require $plugin_dir . '/macolabo-sform.php';
});

require $_tests_dir . '/includes/bootstrap.php';
