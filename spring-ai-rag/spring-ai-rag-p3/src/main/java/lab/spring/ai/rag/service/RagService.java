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
 * RAG + Tool Calling サービス - Phase3
 *
 * フロー図: docs/diagrams/flow_p3_tool_calling.drawio
 *
 * 利用可能なTools:
 * - inventory_status: 在庫状況・過剰在庫判定
 * - sales_analysis: 売上分析
 * - device_alerts: デバイスアラート
 * - kpi_summary: 店舗KPIサマリ
 *
 * Tool Calling の仕組み:
 * 1. LLMが質問を分析し、Toolが必要か判断
 * 2. 必要な場合、LLMがTool名と引数をJSON形式で出力
 * 3. Spring AI が自動でToolメソッドを呼び出し
 * 4. Tool結果をLLMに返却し、最終回答を生成
 */
@Slf4j
@Service
public class RagService {

    private static final int TOP_K = 3;

    /**
     * Tool Calling 用システムプロンプト
     * - コンテキストを優先参照
     * - 不足時のみToolを呼び出す
     */
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
     * Tool Calling の設定
     *
     * - defaultTools(): Toolインスタンスを登録
     * - defaultToolNames(): ホワイトリスト（セキュリティ対策）
     *
     * @Tool アノテーションのついたメソッドがLLMから呼び出し可能になる
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

        // ホワイトリストでTool名を明示的に指定（セキュリティ対策）
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

    public RagResponse ask(RagRequest request) {
        log.info("[STEP 1] 質問受付: {}", request.question());

        // STEP 2: ベクトル検索でコンテキスト取得
        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder().query(request.question()).topK(TOP_K).build()
        );
        log.info("[STEP 2] 類似ドキュメント: {} 件", relevantDocs.size());

        String context = relevantDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        // STEP 3: LLM呼び出し（Tool Calling 有効）
        // - LLMが必要と判断した場合、自動でToolメソッドが呼ばれる
        // - Spring AI がTool呼び出し→結果取得→LLM再呼び出しを自動処理
        String answer = chatClient.prompt()
                .system(String.format(SYSTEM_PROMPT, context))
                .user(request.question())
                .call()
                .content();
        log.info("[STEP 3] LLM回答生成完了（Tool呼び出し含む可能性あり）");

        List<String> sources = relevantDocs.stream().map(Document::getText).toList();
        return new RagResponse(answer, sources);
    }
}

