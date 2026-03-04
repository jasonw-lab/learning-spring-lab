package lab.spring.ai.rag.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 設定 - Phase5
 *
 * フロー図: docs/diagrams/flow_p5_security.drawio
 *
 * Filter Chain: ApiKeyAuthFilter → ConcurrentLimitFilter → Spring Security → RagController
 *
 * 設定プロパティ:
 * - app.security.enabled: 認証有効/無効
 * - app.security.api-key: 認証キー
 * - app.security.max-concurrent-requests: 同時リクエスト上限
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Value("${app.security.enabled:true}") boolean enabled,
            @Value("${app.security.api-key:change-me}") String apiKey,
            @Value("${app.security.max-concurrent-requests:4}") int maxConcurrent
    ) throws Exception {
        // CSRF無効（REST API、ステートレス前提）
        http.csrf(csrf -> csrf.disable());
        http.httpBasic(Customizer.withDefaults());
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 認可設定
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()  // ヘルスチェック等は許可
                .anyRequest().authenticated()
        );

        // カスタムFilter追加（順序重要: 先にAPI Key、次に同時実行制限）
        http.addFilterBefore(new ConcurrentLimitFilter(maxConcurrent), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new ApiKeyAuthFilter(enabled, apiKey), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

