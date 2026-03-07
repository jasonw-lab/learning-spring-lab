package lab.spring.ai.rag.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 在庫照会ツール（セキュリティ強化版）
 *
 * Phase5 セキュリティ対策:
 * - 引数の必須チェック
 * - 引数長の上限チェック（DoS対策）
 * - ToolAuditAspect による実行ログ
 */
@Slf4j
@Component
public class InventoryTool {

    public static final String TOOL_INVENTORY_STATUS = "inventory_status";

    @Tool(
            name = TOOL_INVENTORY_STATUS,
            description = "在庫状況と過剰在庫の簡易判定を返す。引数: category(商品カテゴリ)"
    )
    public String inventoryStatus(
            @ToolParam(description = "商品カテゴリ（例: 調味料、冷蔵食品）") String category
    ) {
        // 引数バリデーション（セキュリティ対策）
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("category must not be blank");
        }
        if (category.length() > 64) {
            throw new IllegalArgumentException("category too long");  // DoS対策
        }
        log.info("[TOOL] inventory_status 呼び出し: category={}", category);

        // ダミーデータ（本番では mall-retail API を呼び出す）
        if (category.contains("調味料")) {
            return "商品カテゴリ: 調味料, 在庫数量: 2,450個, 在庫金額: ¥735,000, 回転日数: 45日, 判定: 過剰在庫";
        }
        return "商品カテゴリ: " + category + ", 在庫情報: 情報がありません";
    }
}

