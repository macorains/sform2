<?php
/**
 * MacolaboSformLoader のテスト（formApi はモックサーバー）
 */
class LoaderTest extends WP_UnitTestCase {

    use MsformTestSetup;

    /** @var MacolaboSformLoader */
    private $loader;

    public function set_up() {
        parent::set_up();
        $this->set_up_msform();
        $this->loader = new MacolaboSformLoader();
    }

    public function tear_down() {
        $this->tear_down_msform();
        parent::tear_down();
    }

    private function last_json_request($path) {
        $requests = MsformMockApi::requests_to($path);
        $this->assertCount(1, $requests, "{$path} へのリクエスト数");
        return [$requests[0]['headers'], json_decode($requests[0]['body'], true)];
    }

    // ---- ログイン ----

    public function test_login_success_returns_token() {
        $login = $this->loader->_login();

        $this->assertSame('test-token', trim($login['token']));
        $this->assertSame('', $login['message']);
    }

    public function test_login_sends_group_and_password_as_form_params() {
        $this->loader->_login();

        $requests = MsformMockApi::requests_to('/signin');
        $this->assertCount(1, $requests);
        $this->assertStringStartsWith('application/x-www-form-urlencoded', $requests[0]['headers']['content-type']);
        parse_str($requests[0]['body'], $params);
        $this->assertSame(['password' => 'test-password', 'username' => 'test-group'], $params);
    }

    public function test_login_failure_returns_empty_token_and_message() {
        update_option('msform_setting', [
            'api_url'  => MsformMockApi::url(),
            'group'    => 'test-group',
            'password' => 'wrong',
        ]);
        $login = (new MacolaboSformLoader())->_login();

        $this->assertSame('', $login['token']);
        $this->assertSame('invalid credentials', $login['message']);
    }

    public function test_login_with_explicit_info_ignores_saved_settings() {
        update_option('msform_setting', ['api_url' => 'http://127.0.0.1:1', 'group' => 'x', 'password' => 'y']);
        $login = (new MacolaboSformLoader())->_login([
            'api_url'  => MsformMockApi::url(),
            'group'    => 'test-group',
            'password' => 'test-password',
        ]);

        $this->assertSame('test-token', trim($login['token']));
    }

    // ---- 各 API 呼び出し ----

    public function test_form_load() {
        $html = $this->loader->form_load('form-1', 'cache-9');

        $this->assertSame(
            '<div class="sform-form-wrapper">LOAD:form-1:cache-9</div><input type="hidden" id="sform_form_id" value="form-1"/>',
            $html
        );
        list($headers, $body) = $this->last_json_request('/load');
        $this->assertSame('test-token', $headers['x-auth-token']);
        $this->assertStringStartsWith('application/json', $headers['content-type']);
        $this->assertSame(['hashed_form_id' => 'form-1', 'receiver_path' => '', 'cache_id' => 'cache-9'], $body);
    }

    public function test_form_validate_ok() {
        $response = json_decode($this->loader->form_validate('form-1', ['name' => '山田', 'email' => 'a@example.com']), true);
        $data = json_decode($response['data'], true);

        $this->assertSame([], $data['validate_result']);
        $this->assertSame('cache-form-1', $data['cache_id']);

        list($headers, $body) = $this->last_json_request('/validate');
        $this->assertSame('test-token', $headers['x-auth-token']);
        $this->assertSame([
            'hashed_form_id' => 'form-1',
            'receiver_path'  => '',
            'postdata'       => ['name' => '山田', 'email' => 'a@example.com'],
        ], $body);
    }

    public function test_form_validate_ng() {
        $response = json_decode($this->loader->form_validate('form-1', ['email' => '']), true);
        $data = json_decode($response['data'], true);

        $this->assertSame(['email' => '必須項目です'], $data['validate_result']);
    }

    public function test_form_confirm() {
        $response = json_decode($this->loader->form_confirm('form-1', ['name' => '山田'], 'cache-1'), true);

        $this->assertSame('<div class="sform-form-wrapper">CONFIRM:form-1:cache-1</div>', json_decode($response['data']));
        list(, $body) = $this->last_json_request('/confirm');
        $this->assertSame([
            'hashed_form_id' => 'form-1',
            'cache_id'       => 'cache-1',
            'receiver_path'  => '',
            'postdata'       => ['name' => '山田'],
        ], $body);
    }

    public function test_form_save() {
        $response = json_decode($this->loader->form_save('form-1', 'cache-1'), true);

        $this->assertSame(['html'], array_keys($response));
        $this->assertSame('<div class="sform-form-wrapper">SAVED:form-1:cache-1</div>', json_decode($response['html']));
        list(, $body) = $this->last_json_request('/save');
        $this->assertSame(['hashed_form_id' => 'form-1', 'cache_id' => 'cache-1', 'receiver_path' => ''], $body);
    }

    public function test_each_call_logs_in_first() {
        $this->loader->form_validate('form-1', []);
        $this->loader->form_save('form-1', 'cache-1');

        $this->assertSame(['/signin', '/validate', '/signin', '/save'], array_column(MsformMockApi::requests(), 'path'));
    }

    // ---- 本文中のフォーム展開 ----

    public function test_form_initial_load_replaces_msform_tag() {
        $content = "<p>before</p>\n<msform><form_id>form-1</form_id></msform>\n<p>after</p>";

        $this->assertSame(
            '<p>before</p><div class="sform_wrapper"><div class="sform-form-wrapper">LOAD:form-1:</div>'
            . '<input type="hidden" id="sform_form_id" value="form-1"/></div><p>after</p>',
            $this->loader->form_initial_load($content, null)
        );
    }

    public function test_get_form_param() {
        $this->assertSame(
            ['form_id' => 'abc'],
            $this->loader->get_form_param("<p>x</p><msform>\n<form_id>abc</form_id>\n</msform>")
        );
    }

    public function test_get_hidden_param() {
        $this->assertSame('<input type="hidden" id="sform_form_id" value="abc"/>', $this->loader->get_hidden_param('abc'));
    }

    // ---- 既知の不具合（phpunit.xml.dist で除外。修正時に @group を外す） ----

    /**
     * the_content フィルタは全ページで呼ばれるが、<msform> の無い本文でも formApi を呼び、
     * get_form_param() で未定義インデックスの警告が出る。
     *
     * @group known-bug
     */
    public function test_form_initial_load_without_msform_tag_returns_content_unchanged() {
        $content = '<p>no form here</p>';

        $this->assertSame($content, $this->loader->form_initial_load($content, null));
        $this->assertSame([], MsformMockApi::requests(), '<msform> の無いページで formApi を呼ばないこと');
    }

    /**
     * ログイン失敗時の分岐で未定義変数 $res / $form_id を参照している。
     *
     * @group known-bug
     */
    public function test_form_initial_load_shows_error_when_login_fails() {
        update_option('msform_setting', ['api_url' => MsformMockApi::url(), 'group' => 'test-group', 'password' => 'wrong']);
        $html = (new MacolaboSformLoader())->form_initial_load('<msform><form_id>form-1</form_id></msform>', null);

        $this->assertStringContainsString("Can't connect to SformAPI.", $html);
    }

    /**
     * form_id がエスケープされずに HTML へ出力される。msform_load_form では $_POST['form_id'] がそのまま
     * 入るため、nonce の無い admin-ajax への POST で反射型 XSS になりうる。
     *
     * @group known-bug
     */
    public function test_get_hidden_param_escapes_form_id() {
        $html = $this->loader->get_hidden_param('"><script>alert(1)</script>');

        $this->assertStringNotContainsString('<script>', $html);
    }
}
