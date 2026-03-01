package lab.spring.ai.rag.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        // Phase1: In-memory only. Rebuild on each restart via SmartRetailDataLoader.
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
