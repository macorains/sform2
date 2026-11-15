<?php
/**
 * MacolaboSformSettingPage のテスト
 */
class SettingPageTest extends WP_UnitTestCase {

    /** @var MacolaboSformSettingPage */
    private $page;

    public static function set_up_before_class() {
        parent::set_up_before_class();
        require_once ABSPATH . 'wp-admin/includes/template.php';   // add_settings_error() など
    }

    public function set_up() {
        parent::set_up();
        global $wp_settings_errors;
        $wp_settings_errors = [];
        update_option('msform_setting', [
            'api_url'  => 'http://old.example.com',
            'group'    => 'old-group',
            'password' => 'old-password',
        ]);
        $this->page = new MacolaboSformSettingPage();
    }

    private function error_codes() {
        return array_column(get_settings_errors('msform_setting'), 'code');
    }

    public function test_registers_admin_hooks() {
        $this->assertSame(10, has_action('admin_menu', [$this->page, 'add_plugin_page']));
        $this->assertSame(10, has_action('admin_init', [$this->page, 'page_init']));
    }

    public function test_sanitize_accepts_valid_input() {
        $result = $this->page->sanitize([
            'api_url'  => ' http://api.example.com ',
            'group'    => 'group-1',
            'password' => 'token-1',
        ]);

        $this->assertSame([
            'api_url'  => 'http://api.example.com',
            'group'    => 'group-1',
            'password' => 'token-1',
        ], $result);
        $this->assertSame([], $this->error_codes());
    }

    public function test_sanitize_strips_tags() {
        $result = $this->page->sanitize([
            'api_url'  => 'http://api.example.com',
            'group'    => '<b>group</b>',
            'password' => 'token',
        ]);

        $this->assertSame('group', $result['group']);
    }

    public function test_sanitize_keeps_old_values_and_reports_errors_for_empty_input() {
        $result = $this->page->sanitize(['api_url' => '', 'group' => '   ', 'password' => '']);

        $this->assertSame([
            'api_url'  => 'http://old.example.com',
            'group'    => 'old-group',
            'password' => 'old-password',
        ], $result);
        $this->assertSame(['api_url', 'group', 'password'], $this->error_codes());
    }

    public function test_sanitize_handles_missing_keys_without_previous_settings() {
        delete_option('msform_setting');

        $result = $this->page->sanitize([]);

        $this->assertSame(['api_url' => '', 'group' => '', 'password' => ''], $result);
        $this->assertSame(['api_url', 'group', 'password'], $this->error_codes());
    }
}
