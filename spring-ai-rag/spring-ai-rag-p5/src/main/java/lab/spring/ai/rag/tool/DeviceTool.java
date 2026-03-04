package lab.spring.ai.rag.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * デバイス監視ツール（セキュリティ強化版）
 *
 * Phase5 セキュリティ対策:
 * - 引数の必須チェック
 * - 引数長の上限チェック（DoS対策）
 * - ToolAuditAspect による実行ログ
 */
@Slf4j
@Component
public class DeviceTool {

    public static final String TOOL_DEVICE_ALERTS = "device_alerts";

    @Tool(
            name = TOOL_DEVICE_ALERTS,
            description = "デバイスのアラート概要を返す。引数: deviceId(デバイスID)"
    )
    public String deviceAlerts(
            @ToolParam(description = "デバイスID（例: D012）") String deviceId
    ) {
        // 引数バリデーション（セキュリティ対策）
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("deviceId must not be blank");
        }
        if (deviceId.length() > 32) {
            throw new IllegalArgumentException("deviceId too long");  // DoS対策
        }
        log.info("[TOOL] device_alerts 呼び出し: deviceId={}", deviceId);

        // ダミーデータ
        if ("D012".equalsIgnoreCase(deviceId)) {
            return "デバイスID: D012, 種別: 温度センサー, 設置場所: 冷蔵棚3番, アラート件数: 10件/月, ステータス: 要点検, 最終メンテナンス: 2025-08-15";
        }
        return "デバイスID: " + deviceId + ", アラート: 情報がありません";
    }
}

