-- WordPress (receiver/wp-plugin 動作確認用) のスキーマ
CREATE DATABASE IF NOT EXISTS wp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

GRANT ALL PRIVILEGES ON wp.* TO 'sform'@'%';

-- receiver/wp-plugin の PHPUnit 用（テスト実行のたびに初期化される）
CREATE DATABASE IF NOT EXISTS wp_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON wp_test.* TO 'sform'@'%';
FLUSH PRIVILEGES;
