package lab.spring.ai.rag.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import lab.spring.ai.rag.agent.InventoryAgent;
import lab.spring.ai.rag.agent.SalesAgent;
import lab.spring.ai.rag.agent.SensorAgent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LangChain4j 設定 - Phase4
 *
 * フロー図: docs/diagrams/flow_p4_multi_agent.drawio
 *
 * LangChain4j の Agent 構築パターン:
 * 1. Interface 定義（@SystemMessage でペルソナ設定）
 * 2. AiServices.builder() で実装生成（Proxyパターン）
 * 3. 使用側は通常のJavaインターフェースとして呼び出し
 *
 * Spring AI との使い分け:
 * - Spring AI: Embedding, VectorStore, Tool Calling（Springエコシステム統合）
 * - LangChain4j: Agent定義, Memory, RetrievalAugmentor（Agent設計が強い）
 */
@Configuration
public class LangChain4jConfig {

    /**
     * LangChain4j ChatModel (Ollama)
     *
     * Spring AI の設定値を再利用してOllamaに接続
     * - temperature: 0.2（決定論的な回答を優先）
     */
    @Bean
    public ChatModel chatModel(
            @Value("${spring.ai.ollama.base-url}") String baseUrl,
            @Value("${spring.ai.ollama.chat.options.model}") String modelName
    ) {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .temperature(0.2)  // 低めに設定（事実ベースの回答）
                .build();
    }

    /**
     * 売上分析エージェント
     *
     * AiServices.builder() パターン:
     * - Interface をProxyで実装
     * - @SystemMessage が自動でシステムプロンプトとして設定される
     */
    @Bean
    public SalesAgent salesAgent(ChatModel model) {
        return AiServices.builder(SalesAgent.class)
                .chatModel(model)
                .build();
    }

    /**
     * 在庫管理エージェント
     */
    @Bean
    public InventoryAgent inventoryAgent(ChatModel model) {
        return AiServices.builder(InventoryAgent.class)
                .chatModel(model)
                .build();
    }

    /**
     * IoT/センサー監視エージェント
     */
    @Bean
    public SensorAgent sensorAgent(ChatModel model) {
        return AiServices.builder(SensorAgent.class)
                .chatModel(model)
                .build();
    }
}
