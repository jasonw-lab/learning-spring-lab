package lab.spring.ai.rag.audit;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Tool実行の監査ログ（AOP）
 *
 * <pre>
 * 目的:
 * - LLMからのTool呼び出しを全て記録
 * - 不正なTool使用の検知
 * - パフォーマンス分析
 *
 * 出力例:
 * AUDIT tool.call method=InventoryTool.inventoryStatus(..) elapsedMs=5
 *
 * セキュリティ観点:
 * - どのToolが何回呼ばれたか追跡可能
 * - 異常なTool呼び出しパターンの検知に活用
 * </pre>
 */
@Slf4j
@Aspect
@Component
public class ToolAuditAspect {

    /**
     * @Tool アノテーション付きメソッドを監視
     *
     * Spring AI が自動でToolを呼び出す際にも適用される
     */
    @Around("@annotation(org.springframework.ai.tool.annotation.Tool)")
    public Object aroundTool(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return pjp.proceed();
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            log.info("AUDIT tool.call method={} elapsedMs={}", pjp.getSignature().toShortString(), elapsed);
        }
    }
}

