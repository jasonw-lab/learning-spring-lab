package lab.spring.ai.rag.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 在庫照会ツール
 *
 * フロー図: docs/diagrams/flow_p3_tool_calling.drawio
 *
 * 本番環境では mall-retail の InventoryController API を呼び出す想定
 */
@Slf4j
@Component
public class InventoryTool {

    /** Tool名（ホワイトリスト登録用） */
    public static final String TOOL_INVENTORY_STATUS = "inventory_status";

    /**
     * 在庫状況を取得
     *
     * @Tool アノテーション:
     * - name: LLMが認識するTool名
     * - description: LLMがToolを選択する際の判断材料（重要）
     *
     * @ToolParam アノテーション:
     * - description: 引数の説明（LLMが適切な値を渡すために必要）
     */
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
        log.info("[TOOL] inventory_status 呼び出し: category={}", category);

        // ダミーデータ（本番では mall-retail API を呼び出す）
        if (category.contains("調味料")) {
            return "商品カテゴリ: 調味料, 在庫数量: 2,450個, 在庫金額: ¥735,000, 回転日数: 45日, 判定: 過剰在庫";
        }
        return "商品カテゴリ: " + category + ", 在庫情報: 情報がありません";
    }
}

