package lab.spring.ai.rag.service;

import lab.spring.ai.rag.dto.RagRequest;
import lab.spring.ai.rag.dto.RagResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RAG (Retrieval-Augmented Generation) サービス - Phase2: Elasticsearch Vector Store版
 *
 * フロー図: docs/diagrams/flow_p2_elasticsearch_vectorstore.drawio
 *
 * Phase2の特徴:
 * - Elasticsearch をベクトルストアとして使用（永続化）
 * - dense_vector フィールドで類似度検索
 * - 再起動してもデータが保持される
 */
@Slf4j
@Service
public class RagService {

    /** 類似度検索で取得するドキュメント数 */
    private static final int TOP_K = 3;

    /** システムプロンプト: LLMに役割とコンテキストを与える */
    private static final String SYSTEM_PROMPT = """
            あなたは店舗運営データを分析するAIアシスタントです。
            以下のコンテキスト情報を参考に、ユーザーの質問に日本語で回答してください。
            コンテキストに含まれない情報については、推測せずに「情報がありません」と回答してください。

            コンテキスト:
            %s
            """;

    private final VectorStore vectorStore;  // Elasticsearch Vector Store
    private final ChatClient chatClient;    // Ollama Chat Client

    /**
     * Spring AI 1.x 推奨パターン: ChatClient.Builder を注入して build()
     */
    public RagService(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * RAG処理のメインフロー
     *
     * @param request ユーザーからの質問
     * @return LLMの回答と参照ソース
     */
    public RagResponse ask(RagRequest request) {
        log.info("[STEP 1] 質問受付: {}", request.question());

        // ============================================================
        // STEP 2: ベクトル類似度検索 (Retrieval)
        // - 質問文をEmbedding化し、Elasticsearchで類似ドキュメントを検索
        // ============================================================
        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(request.question())
                        .topK(TOP_K)
                        .build()
        );
        log.info("[STEP 2] 類似ドキュメント検索完了: {} 件", relevantDocs.size());

        // ============================================================
        // STEP 3: コンテキスト結合
        // - 検索結果のドキュメントをテキストとして結合
        // ============================================================
        String context = relevantDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));
        log.debug("[STEP 3] コンテキスト構築完了");

        // ============================================================
        // STEP 4: プロンプト構築 & LLM呼び出し (Generation)
        // - システムプロンプト + コンテキスト + ユーザー質問
        // ============================================================
        String systemPrompt = String.format(SYSTEM_PROMPT, context);
        String answer = chatClient.prompt()
                .system(systemPrompt)
                .user(request.question())
                .call()
                .content();
        log.info("[STEP 4] LLM回答生成完了");

        // ============================================================
        // STEP 5: レスポンス構築
        // - 回答と参照ソース（トレーサビリティ用）を返却
        // ============================================================
        List<String> sources = relevantDocs.stream()
                .map(Document::getText)
                .toList();

        return new RagResponse(answer, sources);
    }
}
