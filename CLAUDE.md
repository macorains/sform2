# CLAUDE.md

このファイルは、このリポジトリでコードを扱う際に Claude Code (claude.ai/code) に向けたガイダンスを提供します。

## プロジェクト概要

SForm2 は Web フォームの作成・ホスティングシステムです。サイト運営者は管理画面でフォームを設定し、
外部サイトに埋め込まれた小さな「レシーバ」スクリプトがフォームを表示し、送信データを送り返します。
また、送信されたフォームデータを外部システム（現状は Salesforce）へ転送する機能や、自動返信メール
の送信機能もサポートしています。

## リポジトリ構成

リポジトリルート（`/workspace`）は sbt/Scala（Play Framework）によるマルチモジュール構成であり、
それとは別に Vue の SPA と WordPress プラグインが存在します。

- `domain/` — 共有ライブラリ。エンティティ、DAO（ScalikeJDBC）、サービスを含む。両 API モジュールから依存される。
- `adminApi/` — 管理画面バックエンド（フォーム/転送/ユーザー管理、認証）を提供する Play アプリ。
- `formApi/` — レシーバスクリプトから呼び出される、公開向けフォームエンドポイントを提供する Play アプリ。
- `adminUi/` — 管理画面フロントエンド（Vue 3 の SPA）。Vite でビルド。
- `receiver/wp-plugin/` — 外部の WordPress サイトにフォームを埋め込み、`formApi` と通信する WordPress プラグイン（PHP）。
- `wordpress/` — `receiver/wp-plugin` のテスト用ホスト環境として使われる、vendored かつ gitignore 対象の
  WordPress コア一式。このプロジェクトのソースコードではないため、アプリケーションコードとして編集・
  ドキュメント化の対象にしないこと。
- `docker/`, `.devcontainer/` — ローカル/開発用および本番用イメージのコンテナ定義。
- `testdata/` — `domain/src/test/data` とは別に存在する、独立した SQL フィクスチャ群。

## コマンド

### Scala/Play バックエンド（domain, adminApi, formApi）

この環境には sbt がインストールされており、Scala モジュールのコンパイル・テストを検証に使えます。
コードを読んで判断するだけで済ませず、変更後は必ず該当モジュールの sbt コマンドを実行してください。

**注意**: sbt のプロジェクト ID はディレクトリ名（`adminApi`/`formApi`）とは異なります。`build.sbt`
上の ID は `domain`（一致）、`admin`（`adminApi/` に対応）、`form`（`formApi/` に対応）です。不明な
場合は `sbt projects` で確認してください。

```
sbt "domain/test"                 # domain モジュールの ScalaTest スイートを実行
sbt "admin/run"                   # 管理 API（Play の開発サーバー）を起動
sbt "form/run"                    # フォーム API（Play の開発サーバー）を起動
sbt "domain/testOnly *ClassName*" # 単一のテストクラスを実行（他モジュールも同様に <id>/testOnly）
```

なお `formApi/build.gradle`（この モジュール向けの、Gradle/Play を使った別のビルド経路）も存在します
が、`build.sbt`（`domain` + `adminApi` + `formApi` を aggregate する）が主となる sbt ビルドです。

### adminUi（Vue SPA）

```
cd adminUi
npm run dev        # Vite の開発サーバー
npm run build       # 本番ビルド
npm run lint         # eslint --fix
npm run format      # prettier --write src/
```

## アーキテクチャ

### domain モジュール

- `models/entity/**` — ScalikeJDBC の `SQLSyntaxSupport` を介して DB テーブルにマッピングされる
  case class 群。アグリゲート（`form`, `transfer`, `formtransfertask`, `user`, `api_token` など）
  ごとにグループ化されている。
- `models/daos/**` — テーブルごとに `XxxDAO` トレイトと `XxxDAOImpl` クラスが1組ずつ存在する。
  コントローラ/サービスはトレイトに依存し、DB アクセスは常に暗黙の `DBSession` を通して行う。
- `services/**` — アグリゲートごと、さらに動詞のサブパッケージごとに整理されている（例:
  `services/Form/{insert,update,delete,get,list,load,post,sendtestmail,validate}`）。各サブパッ
  ケージには、その1つの操作用の Request/Response DTO と、それを実装するサービスクラスが入っている。
  既存アグリゲートに新しい操作を追加する場合は、単一の巨大なサービスにメソッドを追加するのではなく、
  この動詞サブパッケージの規約に従うこと。
- JSON のシリアライズ/デシリアライズには、コンパニオンオブジェクト内で Play JSON の
  `Json.format[T]` マクロを使用する。カスタムの `Reads`/`Writes` は、フィールド名が実際に食い違う
  場合（例: リクエストの camelCase フィールドと DB/JSON の snake_case キーの違い、`_type`/
  `auto_number` のようにリマップが必要なフィールドなど）にのみ書かれている。新しいフィールドを
  追加する前に、同じアグリゲート内の既存 DTO で期待されるキー名を確認すること。この不一致がこれ
  までにも繰り返しバグの原因となっている。

### adminApi / formApi（Play アプリ）

- どちらも独立した Play アプリケーション（それぞれ独自の `conf/routes`、`conf/application.conf`
  を持つ）であり、いずれも `.dependsOn(domain)` する。同一プロセスは共有しない。
- `adminApi` はフォーム/転送/ユーザーの CRUD と管理者認証を担当し、`formApi` は外部のレシーバ
  スクリプトが呼び出す最小限のインターフェース（`/signin`, `/validate`, `/confirm`, `/save`,
  `/load`, `/forminputjs/*`）を提供する。
- 認証には `pac4j`（`play-pac4j`）を使用し、`modules/SecurityModule.scala` で配線されている。複数
  のクライアントが同時に登録されている点に注意: `FormClient`/`DirectFormClient`（`UserDAO` に対して
  `SqlAuthencator` によるユーザー名/パスワード認証）、`HeaderClient`（`X-Auth-Token` による JWT）、
  `OidcClient`（Google OIDC）。セッションは暗号化された `PlayCookieSessionStore` に保存される。
- ルーティングは通常の Play のルートファイル（`conf/routes`）でコントローラのアクションに直接
  マッピングされており、別途 API バージョニング層は存在しない。

### adminUi

- `@vue/compat` の MODE 2（Vue 2 互換モード）で動作する Vue 3 アプリ。コンポーネント追加時は
  Vue 2/3 の API の差異に注意すること。UI キットは Bootstrap 5 + `bootstrap-vue-3`。
- ルーティングは `src/router/index.js` にフラットな `vue-router` テーブルとして定義され、状態管理
  は Pinia（`src/stores`）を使用する。
- `adminApi` とは `axios` 経由の HTTP 通信で連携する。

### receiver/wp-plugin

- ビルドステップを持たない素の PHP 製 WordPress プラグイン。`macolabo-sform.php` がエントリー
  ポイントで、WP のフック/ショートコードを `macolabo-sform-loader.php`（表示/読み込みロジック）
  と `macolabo-sform-setting.php`（プラグイン設定）に接続する。`macolabo-sform.js` は確認/キャン
  セルといったフォームのクライアント側フローを、`formApi` への AJAX 通信で処理する。
- このリポジトリ内に vendored されている `wordpress/` をホスト WP 環境として、そこに対してプラグ
  インの動作確認を行う。ただし WP コア自体はプロジェクトのコードではない。

## 既知の注意点（Known gotchas）

- `domain` 内のカスタム Play JSON `Reads`/`Writes` は、これまで実際の DB カラム名からズレることが
  たびたびあった（`type_Code`/`uce_bcc` のようなタイプミス、`form_transfer_task_condition` と
  `form_transfer_task_conditions` のような単数/複数の不一致、同じアグリゲートの似た Insert/Update
  リクエスト DTO 間で誤ったキー参照を使い回してしまう問題 — 例: Transfer config のリクエストで
  誤った id フィールド名を再利用していたケースなど）。シリアライズ関連のコードを触る際は、兄弟
  DTO との比較だけでなく、DAO/エンティティ側の実際の DB カラム名と突き合わせて確認すること。
- `SalesforceSObjectsDescribeResponseField` と `UserSaveRequest` は、Salesforce の Describe API
  とのフィールド名の食い違いや camelCase と snake_case の違いにより、意図的にカスタムの
  Reads/Writes を持つ DTO の例である。これらを安易に `Json.format[T]` に「簡略化」しないこと。
