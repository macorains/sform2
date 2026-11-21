<?php
/**
 * プラグイン本体（macolabo-sform.php）のフック登録・出力のテスト
 */
class PluginTest extends WP_UnitTestCase {

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
     * @dataProvider public_action_provider
     */
    public function test_public_ajax_actions_are_registered_for_guests_and_users($action) {
        $this->assertSame(10, has_action('wp_ajax_' . $action, $action));
        $this->assertSame(10, has_action('wp_ajax_nopriv_' . $action, $action));
    }

    public static function public_action_provider() {
        return [
            ['msform_load_form'],
            ['msform_validate_form'],
            ['msform_confirm_form'],
            ['msform_save_form'],
        ];
    }

    public function test_connection_check_is_not_available_to_guests() {
        $this->assertSame(10, has_action('wp_ajax_msform_connection_check', 'msform_connection_check'));
        $this->assertFalse(has_action('wp_ajax_nopriv_msform_connection_check'));
    }

    public function test_content_and_footer_hooks_are_registered() {
        $this->assertNotFalse(has_filter('the_content'));
        $this->assertSame(10, has_action('wp_footer', 'msform_js_footer'));
    }

    public function test_footer_script() {
        ob_start();
        msform_js_footer();
        $output = ob_get_clean();

        $this->assertStringContainsString("var ajaxurl = '" . admin_url('admin-ajax.php') . "'", $output);
        foreach (['msform_validate_form', 'msform_confirm_form', 'msform_save_form', 'msform_load_form'] as $action) {
            $this->assertStringContainsString("'action' : '{$action}'", $output);
        }
        // 429 時のメッセージ表示と二重送信防止
        $this->assertStringContainsString('xhr.status === 429', $output);
        $this->assertStringContainsString("prop('disabled', true)", $output);
    }
}
