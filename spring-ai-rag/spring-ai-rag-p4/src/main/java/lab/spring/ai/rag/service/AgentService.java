package lab.spring.ai.rag.service;

import lab.spring.ai.rag.agent.InventoryAgent;
import lab.spring.ai.rag.agent.SalesAgent;
import lab.spring.ai.rag.agent.SensorAgent;
import lab.spring.ai.rag.dto.AgentRequest;
import lab.spring.ai.rag.dto.AgentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Multi-Agent サービス - Phase4: Spring AI + LangChain4j ハイブリッド
 *
 * フロー図: docs/diagrams/flow_p4_multi_agent.drawio
 *
 * ハイブリッド構成の利点:
 * - Spring AI: Embedding + VectorStore（エコシステム統合）
 * - LangChain4j: Agent定義（@SystemMessage で専門家ペルソナ設定）
 *
 * 本番環境では Router を LLM ベースにすることで、より柔軟な振り分けが可能
 */
@Slf4j
@Service
public class AgentService {

    private static final int TOP_K = 3;

    /** Spring AI: VectorStore（Embedding + 類似度検索） */
    private final VectorStore vectorStore;

    /** LangChain4j: 専門家Agent群 */
    private final SalesAgent salesAgent;
    private final InventoryAgent inventoryAgent;
    private final SensorAgent sensorAgent;

    public AgentService(
            VectorStore vectorStore,
            SalesAgent salesAgent,
            InventoryAgent inventoryAgent,
            SensorAgent sensorAgent
    ) {
        this.vectorStore = vectorStore;
        this.salesAgent = salesAgent;
        this.inventoryAgent = inventoryAgent;
        this.sensorAgent = sensorAgent;
    }

    public AgentResponse ask(AgentRequest request) {
        String question = request.question();
        log.info("[STEP 1] 質問受付: {}", question);

        // ============================================================
        // STEP 2: Spring AI でベクトル検索（RAG部分）
        // ============================================================
        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder().query(question).topK(TOP_K).build()
        );
        String context = relevantDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));
        log.info("[STEP 2] 類似ドキュメント: {} 件", relevantDocs.size());

        // ============================================================
        // STEP 3: Router（質問の振り分け）
        // - 本サンプルはキーワードベースの簡易実装
        // - 本番では LLM を使った意図分類が推奨
        // ============================================================
        AgentType agentType = route(question);
        log.info("[STEP 3] ルーティング結果: {} Agent", agentType);

        // ============================================================
        // STEP 4: LangChain4j Agent に委譲
        // - 各AgentはSystemMessageで専門家ペルソナを持つ
        // - Context + Question を渡して回答を生成
        // ============================================================
        String message = """
                CONTEXT:
                %s

                QUESTION:
                %s
                """.formatted(context, question);

        String answer = switch (agentType) {
            case SALES -> salesAgent.chat(message);
            case INVENTORY -> inventoryAgent.chat(message);
            case SENSOR -> sensorAgent.chat(message);
        };
        log.info("[STEP 4] {} Agent 回答生成完了", agentType);

        List<String> sources = relevantDocs.stream().map(Document::getText).toList();
        return new AgentResponse(answer, agentType.name(), sources);
    }

    /**
     * 簡易ルーター（キーワードベース）
     *
     * 本番環境での改善案:
     * - LLM を使った意図分類（"売上分析"/"在庫照会"/"デバイス監視" のいずれか判定）
     * - 複数Agentへの並列クエリ + 結果統合
     */
    private AgentType route(String question) {
        String q = question.toLowerCase(Locale.ROOT);
        if (q.contains("売上") || q.contains("sales") || q.contains("前月比")) {
            return AgentType.SALES;
        }
        if (q.contains("在庫") || q.contains("inventory") || q.contains("過剰")) {
            return AgentType.INVENTORY;
        }
        if (q.contains("デバイス") || q.contains("センサー") || q.contains("温度") || q.contains("alert")) {
            return AgentType.SENSOR;
        }
        return AgentType.SALES; // デフォルト
    }

    /** Agent種別 */
    private enum AgentType {
        SALES,      // 売上分析
        INVENTORY,  // 在庫管理
        SENSOR      // IoT/センサー監視
    }
}

