<?php
/**
 * admin-ajax.php 経由の各アクションのテスト（formApi はモックサーバー）
 */
class AjaxTest extends WP_Ajax_UnitTestCase {

    use MsformTestSetup;

    public function set_up() {
        parent::set_up();
        $this->set_up_msform();
    }

    public function tear_down() {
        $this->tear_down_msform();
        parent::tear_down();
    }

    /**
     * アクションを実行し、wp_die() に渡されたレスポンスを返す
     */
    private function call_ajax($action, array $post) {
        $_POST = $post;
        $this->_last_response = '';
        try {
            $this->_handleAjax($action);
        } catch (WPAjaxDieStopException $e) {
            return $e->getMessage();
        } catch (WPAjaxDieContinueException $e) {
            return $this->_last_response;
        }
        $this->fail("{$action} が wp_die() で終了しませんでした");
    }

    public function test_validate_form() {
        $response = $this->call_ajax('msform_validate_form', [
            'form_id' => 'form-1',
            'data'    => ['name' => '山田', 'email' => 'a@example.com'],
        ]);

        // JS 側は JSON.parse(JSON.parse(response).data) で読む
        $data = json_decode(json_decode($response, true)['data'], true);
        $this->assertSame([], $data['validate_result']);
        $this->assertSame('cache-form-1', $data['cache_id']);
    }

    public function test_confirm_form() {
        $response = $this->call_ajax('msform_confirm_form', [
            'form_id'  => 'form-1',
            'data'     => ['name' => '山田'],
            'cache_id' => 'cache-1',
        ]);

        $html = json_decode(json_decode($response, true)['data']);
        $this->assertSame('<div class="sform-form-wrapper">CONFIRM:form-1:cache-1</div>', $html);
    }

    public function test_save_form() {
        $response = $this->call_ajax('msform_save_form', ['form_id' => 'form-1', 'cache_id' => 'cache-1']);

        // JS 側は JSON.parse(JSON.parse(response).html) で読む
        $html = json_decode(json_decode($response, true)['html']);
        $this->assertSame('<div class="sform-form-wrapper">SAVED:form-1:cache-1</div>', $html);
    }

    public function test_load_form() {
        $response = $this->call_ajax('msform_load_form', ['form_id' => 'form-1', 'cache_id' => 'cache-1']);

        $this->assertSame(
            '<div class="sform-form-wrapper">LOAD:form-1:cache-1</div><input type="hidden" id="sform_form_id" value="form-1"/>',
            $response
        );
    }

    public function test_connection_check_as_admin() {
        wp_set_current_user(self::factory()->user->create(['role' => 'administrator']));

        $response = json_decode($this->call_ajax('msform_connection_check', ['data' => [
            'api_url'  => MsformMockApi::url(),
            'group'    => 'test-group',
            'password' => 'test-password',
        ]]), true);

        $this->assertSame('test-token', trim($response['token']));
    }

    public function test_connection_check_reports_login_failure() {
        wp_set_current_user(self::factory()->user->create(['role' => 'administrator']));

        $response = json_decode($this->call_ajax('msform_connection_check', ['data' => [
            'api_url'  => MsformMockApi::url(),
            'group'    => 'test-group',
            'password' => 'wrong',
        ]]), true);

        $this->assertSame('', $response['token']);
        $this->assertSame('invalid credentials', $response['message']);
    }

    // ---- 連続リクエスト制限 ----

    public function test_save_form_is_rate_limited() {
        $this->use_rate_limit_rules([
            'msform_save_form' => ['ip_ua' => [31536000, 2], 'ip' => [31536000, 20]],
        ]);
        $post = ['form_id' => 'form-1', 'cache_id' => 'cache-1'];

        $this->call_ajax('msform_save_form', $post);
        $this->call_ajax('msform_save_form', $post);
        $response = json_decode($this->call_ajax('msform_save_form', $post), true);

        $this->assertFalse($response['success']);
        $this->assertSame('too_many_requests', $response['data']['code']);
        $this->assertCount(2, MsformMockApi::requests_to('/save'), '拒否したリクエストは formApi に送らないこと');
        $this->assertCount(2, MsformMockApi::requests_to('/signin'));
    }

    /**
     * @dataProvider limited_action_provider
     */
    public function test_all_public_actions_are_rate_limited($action) {
        $this->use_rate_limit_rules([$action => ['ip_ua' => [31536000, 1]]]);
        $post = ['form_id' => 'form-1', 'cache_id' => 'cache-1', 'data' => ['name' => 'x']];

        $this->call_ajax($action, $post);
        $response = json_decode($this->call_ajax($action, $post), true);

        $this->assertSame('too_many_requests', $response['data']['code']);
    }

    public static function limited_action_provider() {
        return [
            ['msform_load_form'],
            ['msform_validate_form'],
            ['msform_confirm_form'],
            ['msform_save_form'],
        ];
    }

    // ---- 既知の不具合（phpunit.xml.dist で除外。修正時に @group を外す） ----

    /**
     * msform_connection_check に権限チェックが無く、ログイン済みなら購読者でも任意の URL へ
     * POST させられる（SSRF）。
     *
     * @group known-bug
     */
    public function test_connection_check_requires_manage_options() {
        wp_set_current_user(self::factory()->user->create(['role' => 'subscriber']));

        $this->call_ajax('msform_connection_check', ['data' => [
            'api_url'  => MsformMockApi::url(),
            'group'    => 'test-group',
            'password' => 'test-password',
        ]]);

        $this->assertSame([], MsformMockApi::requests(), '権限の無いユーザーの指定 URL へリクエストしないこと');
    }
}
