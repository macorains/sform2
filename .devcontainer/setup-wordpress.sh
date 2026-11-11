#!/bin/bash
# receiver/wp-plugin 動作確認用の WordPress をセットアップする（冪等）。
# devcontainer の postCreateCommand から実行されるほか、手動で再実行してもよい。
#   起動: wp server --host=0.0.0.0 --port=8081 --path=wordpress
set -euo pipefail

WORKSPACE="${WORKSPACE:-/workspace}"
WP_PATH="$WORKSPACE/wordpress"
WP_URL="${WP_URL:-http://localhost:8081}"
PLUGIN_SLUG="macolabo-sform"
PLUGIN_LINK="$WP_PATH/wp-content/plugins/$PLUGIN_SLUG"
PLUGIN_TARGET="../../../receiver/wp-plugin"
FORM_API_URL="${FORM_API_URL:-http://localhost:9002}"

DB_HOST="${DB_HOST:-mysql}"
DB_NAME="${DB_NAME:-wp}"
DB_USER="${DB_USER:-sform}"
DB_PASS="${DB_PASS:-sform}"
DB_ROOT_PASS="${DB_ROOT_PASS:-root}"   # docker-compose.yml の MYSQL_ROOT_PASSWORD

wp() { command wp --path="$WP_PATH" "$@"; }

if ! command -v wp >/dev/null 2>&1; then
  echo "ERROR: wp (WP-CLI) が見つかりません。devcontainer を再ビルドしてください。" >&2
  exit 1
fi

# 0. DB スキーマの用意
# init-db/02-create-wordpress-db.sql は mysql-data ボリュームが空の初回起動時にしか実行されないため、
# 既存ボリュームを使い回している環境向けにここでも作成する（mysql クライアントは無いので PHP の mysqli を使う）。
echo "Ensuring database '$DB_NAME' exists..."
DB_HOST="$DB_HOST" DB_NAME="$DB_NAME" DB_USER="$DB_USER" DB_ROOT_PASS="$DB_ROOT_PASS" php -r '
  mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);
  $db = getenv("DB_NAME"); $user = getenv("DB_USER");
  if (!preg_match("/^\w+$/", $db) || !preg_match("/^\w+$/", $user)) {
    fwrite(STDERR, "ERROR: invalid DB_NAME/DB_USER\n"); exit(1);
  }
  try {
    $m = new mysqli(getenv("DB_HOST"), "root", getenv("DB_ROOT_PASS"));
    $m->query("CREATE DATABASE IF NOT EXISTS `$db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
    $m->query("GRANT ALL PRIVILEGES ON `$db`.* TO \x27$user\x27@\x27%\x27");
  } catch (mysqli_sql_exception $e) {
    fwrite(STDERR, "ERROR: failed to prepare database: " . $e->getMessage() . "\n"); exit(1);
  }
'

# 1. WordPress コアの取得
if [ ! -f "$WP_PATH/wp-load.php" ]; then
  echo "Downloading WordPress core..."
  mkdir -p "$WP_PATH"
  wp core download --locale=ja
fi

# 2. wp-config.php の作成
if [ ! -f "$WP_PATH/wp-config.php" ]; then
  echo "Creating wp-config.php..."
  wp config create \
    --dbname="$DB_NAME" --dbuser="$DB_USER" --dbpass="$DB_PASS" --dbhost="$DB_HOST" \
    --dbcharset=utf8mb4 --skip-check
fi
wp config set WP_HOME "$WP_URL" --type=constant
wp config set WP_SITEURL "$WP_URL" --type=constant
wp config set WP_DEBUG true --raw --type=constant
wp config set WP_DEBUG_LOG true --raw --type=constant
wp config set WP_DEBUG_DISPLAY false --raw --type=constant
# 組み込みサーバーはシングルスレッドのため、loopback リクエストを伴う WP-Cron は無効化する
wp config set DISABLE_WP_CRON true --raw --type=constant

# 3. インストール
if ! wp core is-installed 2>/dev/null; then
  echo "Installing WordPress..."
  wp core install \
    --url="$WP_URL" --title="SForm Receiver Test" \
    --admin_user=admin --admin_password=admin --admin_email=admin@example.com \
    --skip-email
fi

# 4. プラグインを receiver/wp-plugin へのシンボリックリンクにする
if [ -e "$PLUGIN_LINK" ] && [ ! -L "$PLUGIN_LINK" ]; then
  echo "Replacing copied plugin directory with symlink..."
  rm -rf "$PLUGIN_LINK"
fi
if [ ! -L "$PLUGIN_LINK" ]; then
  ln -s "$PLUGIN_TARGET" "$PLUGIN_LINK"
fi

# 5. プラグイン有効化と初期設定（未設定時のみ。group/password は管理画面から入力する）
wp plugin activate "$PLUGIN_SLUG"
if ! wp option get msform_setting >/dev/null 2>&1; then
  wp option add msform_setting \
    "{\"api_url\":\"$FORM_API_URL\",\"group\":\"\",\"password\":\"\"}" --format=json
fi

echo "WordPress setup complete: $WP_URL (admin/admin)"
