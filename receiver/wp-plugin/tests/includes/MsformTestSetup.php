<?php
/**
 * テスト共通の初期化処理
 *
 * WP_UnitTestCase / WP_Ajax_UnitTestCase の両方で使うため trait にしている。
 */
trait MsformTestSetup {

    private $msform_server_backup;

    /**
     * set_up() から呼ぶ。モック API の記録を消し、プラグイン設定をモック API 向けにする。
     */
    protected function set_up_msform() {
        MsformMockApi::reset();
        update_option('msform_setting', [
            'api_url'  => MsformMockApi::url(),
            'group'    => 'test-group',
            'password' => 'test-password',
        ]);
        $this->msform_server_backup = $_SERVER;
        $this->set_client('192.0.2.10', 'MsformTest/1.0');
    }

    /**
     * tear_down() から呼ぶ。
     */
    protected function tear_down_msform() {
        $_SERVER = $this->msform_server_backup;
    }

    protected function set_client($ip, $ua) {
        $_SERVER['REMOTE_ADDR'] = $ip;
        $_SERVER['HTTP_USER_AGENT'] = $ua;
    }

    /**
     * テストで使う制限ルールに差し替える。
     * ウィンドウ境界をまたいでカウンタがリセットされるとテストが不安定になるため、
     * 既定のテストでは十分長いウィンドウを使う。
     */
    protected function use_rate_limit_rules(array $rules) {
        add_filter('msform_rate_limit_rules', function () use ($rules) {
            return $rules;
        });
    }
}
