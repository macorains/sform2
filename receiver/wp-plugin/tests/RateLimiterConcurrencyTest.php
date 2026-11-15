<?php
/**
 * 並列リクエストで制限をすり抜けないことのテスト
 *
 * 別プロセス（tests/bin/concurrent-guard.php）から同時に guard() を呼ぶ。
 * 子プロセスは autocommit で書き込むため、このテストのトランザクションのロールバックでは消えない。
 * 他のテストに干渉しないよう、tear_down で削除してコミットする。
 *
 * @group slow
 */
class RateLimiterConcurrencyTest extends WP_UnitTestCase {

    const PROCESSES = 10;
    const LIMIT = 2;
    const WINDOW = 600;

    public function tear_down() {
        global $wpdb;
        $wpdb->query('DELETE FROM ' . MacolaboSformRateLimiter::table_name());
        self::commit_transaction();
        parent::tear_down();
    }

    public function test_parallel_requests_cannot_exceed_limit() {
        $ip = '198.51.100.' . random_int(1, 254);
        $ua = 'Concurrency/' . bin2hex(random_bytes(8));

        // 子プロセスの WordPress 読み込みが終わるのを待ってから一斉に実行する。
        // 固定ウィンドウの境界をまたぐとカウンタがリセットされるため、境界付近は避ける。
        $start_at = microtime(true) + 3;
        if (((int) $start_at % self::WINDOW) > self::WINDOW - 10) {
            $start_at += 15;
        }

        $processes = [];
        for ($i = 0; $i < self::PROCESSES; $i++) {
            $process = proc_open(
                [PHP_BINARY, __DIR__ . '/bin/concurrent-guard.php', 'msform_save_form', $ip, $ua, (string) self::WINDOW, (string) self::LIMIT, sprintf('%.6F', $start_at)],
                [1 => ['pipe', 'w'], 2 => ['pipe', 'w']],
                $pipes
            );
            $this->assertIsResource($process);
            $processes[] = [$process, $pipes];
        }

        $results = [];
        foreach ($processes as list($process, $pipes)) {
            $stdout = stream_get_contents($pipes[1]);
            $stderr = stream_get_contents($pipes[2]);
            fclose($pipes[1]);
            fclose($pipes[2]);
            $exit = proc_close($process);
            $this->assertSame(0, $exit, "子プロセスが異常終了しました: {$stdout} {$stderr}");
            $this->assertContains($stdout, ['ok', 'rejected'], "子プロセスの出力が不正です: {$stdout} {$stderr}");
            $results[] = $stdout;
        }

        $counts = array_count_values($results);
        $this->assertSame(self::LIMIT, $counts['ok'] ?? 0, '許可されたリクエスト数が上限と一致しません: ' . implode(',', $results));
        $this->assertSame(self::PROCESSES - self::LIMIT, $counts['rejected'] ?? 0);
    }
}
