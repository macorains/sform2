<?php
/**
 * formApi のモックサーバー（php -S のルータースクリプト）
 *
 * tests/bootstrap.php が起動する。受け取ったリクエストは MSFORM_MOCK_API_LOG に JSON Lines で記録し、
 * テストから MsformMockApi::requests() で参照する。
 *
 * 認証情報: group = test-group / password = test-password で X-Auth-Token: test-token を返す。
 */

$path = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
$body = file_get_contents('php://input');
$headers = array_change_key_case(getallheaders(), CASE_LOWER);

file_put_contents(
    getenv('MSFORM_MOCK_API_LOG'),
    json_encode(['path' => $path, 'headers' => $headers, 'body' => $body]) . "\n",
    FILE_APPEND | LOCK_EX
);

const MOCK_TOKEN = 'test-token';

if ($path === '/signin') {
    parse_str($body, $params);
    if (($params['username'] ?? '') === 'test-group' && ($params['password'] ?? '') === 'test-password') {
        header('X-Auth-Token: ' . MOCK_TOKEN);
        return;
    }
    http_response_code(401);
    echo 'invalid credentials';
    return;
}

if (($headers['x-auth-token'] ?? '') !== MOCK_TOKEN) {
    http_response_code(401);
    echo json_encode('unauthorized');
    return;
}

$request = json_decode($body, true);
$form_id = $request['hashed_form_id'] ?? '';
$cache_id = $request['cache_id'] ?? '';

switch ($path) {
    case '/load':
        echo json_encode('<div class="sform-form-wrapper">LOAD:' . $form_id . ':' . $cache_id . '</div>');
        break;
    case '/validate':
        $postdata = $request['postdata'] ?? [];
        $errors = (isset($postdata['email']) && $postdata['email'] === '') ? ['email' => '必須項目です'] : new stdClass();
        echo json_encode(['validate_result' => $errors, 'cache_id' => 'cache-' . $form_id]);
        break;
    case '/confirm':
        echo json_encode('<div class="sform-form-wrapper">CONFIRM:' . $form_id . ':' . $cache_id . '</div>');
        break;
    case '/save':
        echo json_encode('<div class="sform-form-wrapper">SAVED:' . $form_id . ':' . $cache_id . '</div>');
        break;
    default:
        http_response_code(404);
        echo json_encode('not found');
}
