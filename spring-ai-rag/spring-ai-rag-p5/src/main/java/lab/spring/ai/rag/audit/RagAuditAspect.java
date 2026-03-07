package lab.spring.ai.rag.audit;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * RAG実行の監査ログ（AOP）
 *
 * <pre>
 * 目的:
 * - 全RAGリクエストの実行時間を記録
 * - インシデント対応時のトレーサビリティ確保
 * - パフォーマンス分析・ボトルネック特定
 *
 * 出力例:
 * AUDIT rag.ask elapsedMs=1234
 *
 * 本番環境での改善案:
 * - 構造化ログ（JSON形式）
 * - リクエストID、ユーザーID の付与
 * - 外部監査ログサービスへの送信
 * </pre>
 */
@Slf4j
@Aspect
@Component
public class RagAuditAspect {

    /**
     * RagService.ask() の実行を監視
     *
     * @Around: メソッド実行前後に処理を挟む
     */
    @Around("execution(* lab.spring.ai.rag.service.RagService.ask(..))")
    public Object aroundAsk(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return pjp.proceed();  // 実際のメソッド実行
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            log.info("AUDIT rag.ask elapsedMs={}", elapsed);
        }
    }
}

