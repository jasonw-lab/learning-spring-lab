package lab.spring.ai.rag.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.Semaphore;

/**
 * 同時実行制限フィルター（DoS対策）
 *
 * <pre>
 * 目的:
 * - LLM呼び出しは高コスト（CPU/GPU/メモリ）
 * - 同時リクエスト数を制限してリソース枯渇を防止
 *
 * 仕組み:
 * - Semaphore で同時実行数を管理
 * - 上限到達時は 429 Too Many Requests を返却
 * - 処理完了後に permits を解放
 *
 * 設定:
 * - app.security.max-concurrent-requests: 同時リクエスト上限（デフォルト: 4）
 * </pre>
 *
 * 本番環境での改善案:
 * - ユーザー/APIキー単位のレート制限
 * - Redis 等を使った分散レート制限
 * - トークン消費量ベースのクォータ管理
 */
public class ConcurrentLimitFilter extends OncePerRequestFilter {

    private final Semaphore semaphore;

    public ConcurrentLimitFilter(int maxConcurrent) {
        this.semaphore = new Semaphore(Math.max(1, maxConcurrent));
    }

    /** POST /rag/ask のみ制限対象 */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("POST".equalsIgnoreCase(request.getMethod()) && "/rag/ask".equals(request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // ノンブロッキングで permits 取得を試行
        boolean acquired = semaphore.tryAcquire();
        if (!acquired) {
            // 上限到達: 429 Too Many Requests
            response.setStatus(429);
            return;
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 必ず permits を解放
            semaphore.release();
        }
    }
}

