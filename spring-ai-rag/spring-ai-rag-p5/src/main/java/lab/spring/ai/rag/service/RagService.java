package lab.spring.ai.rag.service;

import lab.spring.ai.rag.dto.RagRequest;
import lab.spring.ai.rag.dto.RagResponse;
import lab.spring.ai.rag.tool.DashboardTool;
import lab.spring.ai.rag.tool.DeviceTool;
import lab.spring.ai.rag.tool.InventoryTool;
import lab.spring.ai.rag.tool.SalesTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RAG + Tool Calling サービス - Phase5: セキュリティ実装版
 *
 * フロー図: docs/diagrams/flow_p5_security.drawio
 *
 * Phase5 セキュリティ対策:
 * 1. API Key認証 (ApiKeyAuthFilter) - X-API-KEY ヘッダで認証
 * 2. 同時実行制限 (ConcurrentLimitFilter) - Semaphore で制限
 * 3. Tool ホワイトリスト - defaultToolNames() で許可するToolを明示
 * 4. 引数バリデーション - 各Tool内で不正な引数を拒否
 * 5. 監査ログ (RagAuditAspect, ToolAuditAspect) - AOP で実行時間を記録
 */
@Slf4j
@Service
public class RagService {

    private static final int TOP_K = 3;

    private static final String SYSTEM_PROMPT = """
            あなたは店舗運営データを分析するAIアシスタントです。
            まずは以下のコンテキスト情報を参照し、足りない場合のみツールを呼び出して確認してください。
            ツールにない情報は推測せずに「情報がありません」と回答してください。

            コンテキスト:
            %s
            """;

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    /**
     * セキュアなChatClient構築
     *
     * セキュリティ対策:
     * - defaultToolNames(): ホワイトリスト方式でToolを制限
     * - 未登録のToolは呼び出し不可
     */
    public RagService(
            VectorStore vectorStore,
            ChatClient.Builder chatClientBuilder,
            InventoryTool inventoryTool,
            SalesTool salesTool,
            DeviceTool deviceTool,
            DashboardTool dashboardTool
    ) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder
                .defaultTools(inventoryTool, salesTool, deviceTool, dashboardTool)
                .defaultToolNames(
                        InventoryTool.TOOL_INVENTORY_STATUS,
                        SalesTool.TOOL_SALES_ANALYSIS,
                        DeviceTool.TOOL_DEVICE_ALERTS,
                        DashboardTool.TOOL_KPI_SUMMARY
                )
                .build();
    }

    /**
     * RAG処理（監査ログ対象: RagAuditAspect）
     */
    public RagResponse ask(RagRequest request) {
        log.info("[STEP 1] 質問受付: {}", request.question());

        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder().query(request.question()).topK(TOP_K).build()
        );
        log.info("[STEP 2] 類似ドキュメント: {} 件", relevantDocs.size());

        String context = relevantDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        String answer = chatClient.prompt()
                .system(String.format(SYSTEM_PROMPT, context))
                .user(request.question())
                .call()
                .content();
        log.info("[STEP 3] LLM回答生成完了");

        List<String> sources = relevantDocs.stream().map(Document::getText).toList();
        return new RagResponse(answer, sources);
    }
}

