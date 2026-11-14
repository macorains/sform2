<?php
/*
Plugin Name: Sform Plugin
Plugin URI:
Description: Sformによるフォーム表示
Version: 0.0.3
Author: Macorains
Author URI:
License: GPL2
*/
?>
<?php
/*  Copyright 2019 Macorains (email : mac.rainshrine@gmail.com)

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License, version 2, as
     published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
?>
<?php
require(dirname(__FILE__) . "/macolabo-sform-setting.php");
require(dirname(__FILE__) . "/macolabo-sform-loader.php");
require(dirname(__FILE__) . "/macolabo-sform-ratelimit.php");

MacolaboSformRateLimiter::init();
register_activation_hook(__FILE__, array('MacolaboSformRateLimiter', 'maybe_install'));
register_deactivation_hook(__FILE__, array('MacolaboSformRateLimiter', 'deactivate'));

function msform_hello() {
    $loader = new MacolaboSformLoader();
    return $loader->hello();
}

// Sform JS
function msform_js_footer(){
    wp_enqueue_script('macolabo-sform', plugins_url( 'macolabo-sform' ) . '/macolabo-sform.js');
    wp_print_scripts( array( 'jquery' ));
    ?>
    <script type="text/javascript">
    //<![CDATA[
        var that = this;
        var ajaxurl = '<?php echo admin_url( 'admin-ajax.php' ); ?>';

        // AJAXエラー時のメッセージ表示（429: 連続リクエスト制限）
        function msformShowError(xhr) {
            var message = 'エラーが発生しました。時間をおいて再度お試しください。';
            if (xhr && xhr.status === 429) {
                var retryAfter = xhr.responseJSON && xhr.responseJSON.data ? xhr.responseJSON.data.retry_after : null;
                var wait = 'しばらく待ってから';
                if (retryAfter >= 60) {
                    wait = '約' + Math.ceil(retryAfter / 60) + '分後に';
                } else if (retryAfter > 0) {
                    wait = '約' + retryAfter + '秒後に';
                }
                message = '短時間に連続して操作されたため、処理を受け付けられませんでした。' + wait + '再度お試しください。';
            }
            var box = jQuery('div.sform_wrapper .sform-error-message');
            if (box.length === 0) {
                box = jQuery('<div class="sform-error-message" role="alert"></div>');
                jQuery('div.sform_wrapper').prepend(box);
            }
            box.text(message);
        }
        function msformClearError() {
            jQuery('div.sform_wrapper .sform-error-message').remove();
        }
        // エラー表示してボタンを再度押せるようにする
        function msformFail(button, xhr) {
            msformShowError(xhr);
            button.prop('disabled', false);
        }
        // 二重エンコードされたレスポンスを解析する。失敗時は null を返す
        function msformParse(response, key) {
            try {
                return JSON.parse(JSON.parse(response)[key]);
            } catch (e) {
                console.error(e);
                return null;
            }
        }

        // 入力フォームの「次へ」ボタンクリック時
        jQuery('#sform_button_confirm').on('click', function(){
            onClickConfirm(that);
        });
        // 入力フォームの「キャンセル」ボタンクリック時
        jQuery('#sform_button_cancel').on('click', function(){
            onClickCancel(that);
        });

        function onClickConfirm(that) {
            // 二重送信防止
            var confirmButton = jQuery('#sform_button_confirm');
            if (confirmButton.prop('disabled')) return;
            confirmButton.prop('disabled', true);
            msformClearError();

            var tmpData = {};
            // フォーム入力内容でjson作る
            jQuery(".sform-col-form-text").each(function(){
                tmpData[this.id] = this.value;
            });
            jQuery(".sform-col-form-checkbox, .sform-col-form-radio").each(function(){
                var tmpChecked = [];
                jQuery("[name=sel_" + this.id + "]").each(function(){
                    if(this.checked) tmpChecked.push(this.value);
                })
                tmpData[this.id] = tmpChecked.join();
            });
            console.log(tmpData);
            jQuery.ajax({
                type: 'POST',
                url: ajaxurl,
                data: {
                    'action' : 'msform_validate_form',
                    'contentType' : 'application/json',
                    'data' : tmpData,
                    'form_id' : jQuery("#sform_form_id").val()
                },
                success: function( response ){
                    var response_data = msformParse(response, 'data');
                    if (response_data === null) return msformFail(confirmButton);
                    console.log('*** validate result***')
                    console.log(response_data)
                    if(Object.keys(response_data.validate_result).length === 0) {
                        // validate ok なら確認フォームを表示
                        jQuery.ajax({
                            type: 'POST',
                            url: ajaxurl,
                            data: {
                                'action' : 'msform_confirm_form',
                                'contentType' : 'application/json',
                                'data' :  tmpData,
                                'form_id' : jQuery("#sform_form_id").val(),
                                'cache_id' : response_data.cache_id
                            },
                            success: function( response ) {
                                var response_data = msformParse(response, 'data');
                                if (response_data === null) return msformFail(confirmButton);
                                console.log('*** confirm result***')
                                console.log(response)
                                jQuery("div.sform_wrapper .sform-form-wrapper").replaceWith(response_data);
                                // 「送信」クリック時
                                jQuery('#sform_button_submit').on('click', function(){
                                    var submitButton = jQuery(this);
                                    if (submitButton.prop('disabled')) return;
                                    submitButton.prop('disabled', true);
                                    msformClearError();
                                    jQuery.ajax({
                                        type: 'POST',
                                        url: ajaxurl,
                                        data: {
                                            'action' : 'msform_save_form',
                                            'contentType' : 'application/json',
                                            'data' : tmpData,
                                            'form_id' : jQuery("#sform_form_id").val(),
                                            'cache_id' : jQuery("#sform_cache_id").val()
                                        },
                                        success: function(response) {
                                            console.log('*** save result ***')
                                            console.log(response)
                                            var response_data = msformParse(response, 'html');
                                            if (response_data === null) return msformFail(submitButton);
                                            jQuery("div.sform_wrapper .sform-form-wrapper").replaceWith(response_data);
                                                // 「完了」ボタンクリック時
                                                jQuery('#sform_button_finish').on('click', function(){
                                                location.href = jQuery("#complete_url").val();
                                            });

                                        },
                                        error: function(xhr) {
                                            msformFail(submitButton, xhr);
                                        }
                                    })
                                });
                                // 「戻る」クリック時
                                jQuery('#sform_button_back').on('click', function(){
                                    var backButton = jQuery(this);
                                    if (backButton.prop('disabled')) return;
                                    backButton.prop('disabled', true);
                                    msformClearError();
                                    console.log(jQuery("#sform_form_id").val())
                                    console.log(jQuery("#sform_cache_id").val())
                                    jQuery.ajax({
                                        type: 'POST',
                                        url: ajaxurl,
                                        data: {
                                            'action' : 'msform_load_form',
                                            'contentType' : 'application/json',
                                            'form_id' : jQuery("#sform_form_id").val(),
                                            'cache_id' : jQuery("#sform_cache_id").val()
                                        },
                                        success: function(response) {
                                            console.log(response)
                                            jQuery("div.sform_wrapper .sform-form-wrapper").replaceWith(response);
                                            // 入力フォームの「次へ」ボタンクリック時
                                            jQuery('#sform_button_confirm').on('click', function(){
                                                that.onClickConfirm(that);
                                            });
                                            // 入力フォームの「キャンセル」ボタンクリック時
                                            jQuery('#sform_button_cancel').on('click', function(){
                                                that.onClickCancel(that);
                                            });
                                        },
                                        error: function(xhr) {
                                            console.error(xhr);
                                            msformFail(backButton, xhr);
                                        }
                                    })
                                });
                            },
                            error: function(xhr){
                                console.log(xhr);
                                msformFail(confirmButton, xhr);
                            }
                        });
                    } else {
                        // validate ng なら入力フォームにエラーメッセージを追加
                        confirmButton.prop('disabled', false);
                        Object.keys(response_data.validate_result).forEach(function(k){
                            var errorElement = jQuery('#sform-col-error-' + k)[0];
                            if (errorElement) {
                                errorElement.textContent = response_data.validate_result[k];
                            } else {
                                console.error('error element not found: ' + k, response_data.validate_result[k]);
                            }
                        })
                    }
                },
                error: function(xhr){
                    msformFail(confirmButton, xhr);
                }
            });
        }

        function onClickCancel() {
            location.href = jQuery("#cancel_url").val();
        }
    //]]>
    </script>
<?php
}
add_action( 'wp_footer', 'msform_js_footer' );

/**
 * フォーム読み込み（主にリロード）
 */
function msform_load_form(){
    MacolaboSformRateLimiter::guard('msform_load_form');
    $loader = new MacolaboSformLoader();
    $response = $loader->form_load($_POST['form_id'], $_POST['cache_id']);
    die($response);
}

/**
 * フォームバリデーション
 */
function msform_validate_form(){
    MacolaboSformRateLimiter::guard('msform_validate_form');
    $loader = new MacolaboSformLoader();
    $response = $loader->form_validate($_POST['form_id'], $_POST['data']);
    die($response);
}

/**
 * 確認ページ取得
 */
function msform_confirm_form(){
    MacolaboSformRateLimiter::guard('msform_confirm_form');
    $loader = new MacolaboSformLoader();
    $response = $loader->form_confirm($_POST['form_id'], $_POST['data'], $_POST['cache_id']);
    die($response);
}

/**
 * フォーム保存
 */
function msform_save_form(){
    MacolaboSformRateLimiter::guard('msform_save_form');
    $loader = new MacolaboSformLoader();
    $response = $loader->form_save($_POST['form_id'], $_POST['cache_id']);
    die($response);

}

/**
 * 疎通確認（ログインできるか）
 */
function msform_connection_check(){
  $loader = new MacolaboSformLoader();
  $data = $_POST['data'];
  $info = [
    'api_url' => $data['api_url'],
    //'user_id' => $data['user_id'],
    'password' => $data['password'],
    'group' => $data['group']
  ];
  $response = json_encode($loader->_login($info));
  die($response);
}

add_action('wp_ajax_msform_load_form', 'msform_load_form');
add_action('wp_ajax_nopriv_msform_load_form', 'msform_load_form');
add_action('wp_ajax_msform_validate_form', 'msform_validate_form');
add_action('wp_ajax_nopriv_msform_validate_form', 'msform_validate_form');
add_action('wp_ajax_msform_confirm_form', 'msform_confirm_form');
add_action('wp_ajax_nopriv_msform_confirm_form', 'msform_confirm_form');
add_action('wp_ajax_msform_save_form', 'msform_save_form');
add_action('wp_ajax_nopriv_msform_save_form', 'msform_save_form');
add_action('wp_ajax_msform_connection_check', 'msform_connection_check');

add_filter( 'the_content', function($content) {
    $loader = new MacolaboSformLoader();
    return $loader->form_initial_load($content, null);
} );

if(is_admin()){
    $msform_setting_page = new MacolaboSformSettingPage();
}

if(!function_exists('_log')){
    function _log($message) {
      if (WP_DEBUG === true) {
        if (is_array($message) || is_object($message)) {
          error_log(print_r($message, true));
        } else {
          error_log($message);
        }
      }
    }
}
?>
