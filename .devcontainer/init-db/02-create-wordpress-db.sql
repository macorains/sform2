-- WordPress (receiver/wp-plugin 動作確認用) のスキーマ
CREATE DATABASE IF NOT EXISTS wp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

GRANT ALL PRIVILEGES ON wp.* TO 'sform'@'%';
FLUSH PRIVILEGES;
