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

@Slf4j
@Service
public class RagService {

    private static final int TOP_K = 3;

    private static final String SYSTEM_PROMPT = """
            あなたは店舗運営データを分析するAIアシスタントです。
            以下のコンテキスト情報のみを根拠に、ユーザーの質問に日本語で回答してください。
            コンテキストに含まれない情報については、推測せずに「情報がありません」と回答してください。

            コンテキスト:
            %s
            """;

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public RagService(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
    }

    public RagResponse ask(RagRequest request) {
        log.info("Processing RAG request: {}", request.question());

        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder().query(request.question()).topK(TOP_K).build()
        );

        String context = relevantDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        String answer = chatClient.prompt()
                .system(String.format(SYSTEM_PROMPT, context))
                .user(request.question())
                .call()
                .content();

        List<String> sources = relevantDocs.stream().map(Document::getText).toList();
        return new RagResponse(answer, sources);
    }
}

