package lab.spring.ai.rag.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * KPIダッシュボードツール
 *
 * 本番環境では mall-retail の DashboardController API を呼び出す想定
 */
@Slf4j
@Component
public class DashboardTool {

    public static final String TOOL_KPI_SUMMARY = "kpi_summary";

    @Tool(
            name = TOOL_KPI_SUMMARY,
            description = "店舗KPIの簡易サマリを返す。引数: storeId(店舗ID)"
    )
    public String kpiSummary(
            @ToolParam(description = "店舗ID（例: S001, S003）") String storeId
    ) {
        if (storeId == null || storeId.isBlank()) {
            throw new IllegalArgumentException("storeId must not be blank");
        }
        log.info("[TOOL] kpi_summary 呼び出し: storeId={}", storeId);

        // ダミーデータ
        if ("S003".equalsIgnoreCase(storeId)) {
            return "店舗ID: S003, KPI概要: 冷凍食品の売上が前月比-20%, 温度センサー(D012)に要点検フラグ, 調味料カテゴリは過剰在庫";
        }
        return "店舗ID: " + storeId + ", KPI概要: 情報がありません";
    }
}

