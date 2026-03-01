package lab.spring.ai.rag.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 売上分析ツール
 *
 * 本番環境では mall-retail の SalesController API を呼び出す想定
 */
@Slf4j
@Component
public class SalesTool {

    public static final String TOOL_SALES_ANALYSIS = "sales_analysis";

    /**
     * 売上分析を実行
     *
     * 複数引数のTool例:
     * - LLMは質問から適切な引数を抽出して呼び出す
     * - 省略可能な引数はnullで渡される場合がある
     */
    @Tool(
            name = TOOL_SALES_ANALYSIS,
            description = "売上分析の簡易結果を返す。引数: storeId(店舗ID), period(期間), category(カテゴリ)"
    )
    public String salesAnalysis(
            @ToolParam(description = "店舗ID（例: S001, S003）") String storeId,
            @ToolParam(description = "分析期間（例: 2025年10月）") String period,
            @ToolParam(description = "商品カテゴリ（例: 冷凍食品）") String category
    ) {
        if (storeId == null || storeId.isBlank()) {
            throw new IllegalArgumentException("storeId must not be blank");
        }
        log.info("[TOOL] sales_analysis 呼び出し: storeId={}, period={}, category={}", storeId, period, category);

        // ダミーデータ
        if ("S003".equalsIgnoreCase(storeId) && (category == null || category.contains("冷凍食品"))) {
            return "店舗ID: S003, 期間: 2025年10月, カテゴリ: 冷凍食品, 売上金額: ¥1,250,000, 前月比: -20%, 客数: 3,200人, 客単価: ¥390";
        }
        return "店舗ID: " + storeId + ", 売上分析: 情報がありません";
    }
}

