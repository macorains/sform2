<?php
/**
 * formApi モックサーバーの起動と、受信リクエストの参照
 */
class MsformMockApi {

    private static $process;
    private static $url;
    private static $log_file;

    public static function start($port, $router, $log_file) {
        self::$url = 'http://127.0.0.1:' . $port;
        self::$log_file = $log_file;
        self::reset();

        $env = getenv();
        $env['MSFORM_MOCK_API_LOG'] = $log_file;
        self::$process = proc_open(
            [PHP_BINARY, '-S', '127.0.0.1:' . $port, $router],
            [0 => ['pipe', 'r'], 1 => ['file', '/dev/null', 'w'], 2 => ['file', '/dev/null', 'w']],
            $pipes,
            dirname($router),
            $env
        );
        if (!is_resource(self::$process)) {
            self::abort('モック API サーバーを起動できませんでした');
        }
        register_shutdown_function([__CLASS__, 'stop']);

        // 起動待ち（プラグインの curl にはタイムアウトが無いため、確実に起動してからテストを始める）
        $deadline = microtime(true) + 5;
        while (microtime(true) < $deadline) {
            $status = proc_get_status(self::$process);
            if (!$status['running']) {
                self::abort("モック API サーバーが終了しました（ポート {$port} が使用中の可能性。MSFORM_MOCK_API_PORT で変更可）");
            }
            $socket = @fsockopen('127.0.0.1', $port, $errno, $errstr, 0.2);
            if ($socket) {
                fclose($socket);
                return;
            }
            usleep(100000);
        }
        self::abort('モック API サーバーの起動がタイムアウトしました');
    }

    public static function stop() {
        if (is_resource(self::$process)) {
            proc_terminate(self::$process);
            proc_close(self::$process);
            self::$process = null;
        }
    }

    public static function url() {
        return self::$url;
    }

    /**
     * 受信したリクエストの記録を消去する
     */
    public static function reset() {
        file_put_contents(self::$log_file, '');
    }

    /**
     * 受信したリクエスト一覧  [['path' => ..., 'headers' => [...], 'body' => ...], ...]
     */
    public static function requests() {
        $lines = array_filter(explode("\n", (string) file_get_contents(self::$log_file)));
        return array_map(function ($line) {
            return json_decode($line, true);
        }, array_values($lines));
    }

    /**
     * 指定パスへのリクエストのみ
     */
    public static function requests_to($path) {
        return array_values(array_filter(self::requests(), function ($r) use ($path) {
            return $r['path'] === $path;
        }));
    }

    private static function abort($message) {
        fwrite(STDERR, $message . "\n");
        self::stop();
        exit(1);
    }
}
