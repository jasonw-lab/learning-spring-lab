package lab.spring.ai.rag.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * API Key 認証フィルター
 *
 * <pre>
 * 認証フロー:
 * 1. X-API-KEY ヘッダを取得
 * 2. 期待値と一致するか検証
 * 3. 一致: SecurityContext に認証情報をセット → 次のFilterへ
 * 4. 不一致: 401 Unauthorized を返却
 *
 * 使用例:
 * curl -H "X-API-KEY: change-me" http://localhost:8080/rag/ask
 * </pre>
 *
 * 本番環境での改善案:
 * - JWT認証への移行
 * - APIキーのローテーション機能
 * - 複数キー対応（ユーザー/サービス別）
 */
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-API-KEY";

    private final boolean enabled;
    private final String expectedApiKey;

    public ApiKeyAuthFilter(boolean enabled, String expectedApiKey) {
        this.enabled = enabled;
        this.expectedApiKey = expectedApiKey;
    }

    /** Actuator エンドポイントは認証スキップ */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 認証無効時はスキップ
        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        // API Key 検証
        String apiKey = request.getHeader(HEADER);
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 認証成功: SecurityContext に認証情報をセット
        var auth = new UsernamePasswordAuthenticationToken(
                "api-key",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}

