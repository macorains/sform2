<?php
/**
 * WordPress テストライブラリ (wp-phpunit) 用の設定
 *
 * 注意: DB_NAME のデータベースはテスト実行のたびに初期化される（wptests_ テーブルを全削除）。
 *       動作確認用の wp スキーマとは必ず別のデータベースを指定すること。
 */

// devcontainer の setup-wordpress.sh が用意する WordPress コア（/workspace/wordpress）
define('ABSPATH', rtrim(getenv('WP_CORE_DIR') ?: dirname(__DIR__, 3) . '/wordpress', '/') . '/');

define('WP_DEBUG', true);

define('DB_NAME', getenv('WP_TESTS_DB_NAME') ?: 'wp_test');
define('DB_USER', getenv('WP_TESTS_DB_USER') ?: 'sform');
define('DB_PASSWORD', getenv('WP_TESTS_DB_PASSWORD') ?: 'sform');
define('DB_HOST', getenv('WP_TESTS_DB_HOST') ?: 'mysql');
define('DB_CHARSET', 'utf8mb4');
define('DB_COLLATE', '');

define('AUTH_KEY',         'msform-test-auth-key');
define('SECURE_AUTH_KEY',  'msform-test-secure-auth-key');
define('LOGGED_IN_KEY',    'msform-test-logged-in-key');
define('NONCE_KEY',        'msform-test-nonce-key');
define('AUTH_SALT',        'msform-test-auth-salt');
define('SECURE_AUTH_SALT', 'msform-test-secure-auth-salt');
define('LOGGED_IN_SALT',   'msform-test-logged-in-salt');
define('NONCE_SALT',       'msform-test-nonce-salt');

$table_prefix = 'wptests_';

define('WP_TESTS_DOMAIN', 'example.org');
define('WP_TESTS_EMAIL', 'admin@example.org');
define('WP_TESTS_TITLE', 'Test Blog');
define('WP_PHP_BINARY', 'php');
define('WPLANG', '');
