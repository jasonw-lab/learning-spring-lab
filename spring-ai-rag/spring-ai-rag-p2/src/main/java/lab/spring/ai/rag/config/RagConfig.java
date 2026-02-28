package lab.spring.ai.rag.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStoreOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Phase2: Elasticsearch Vector Store 設定
 *
 * フロー図: docs/diagrams/flow_p2_elasticsearch_vectorstore.drawio
 *
 * Elasticsearchインデックス構造:
 * - Index: smart-retail-rag
 * - Mapping: id(keyword), content(text), embedding(dense_vector 768次元), metadata(object)
 *
 * SimpleVectorStore (Phase1) との違い:
 * - データ永続化: ESはディスク保存、SimpleVectorStoreはメモリのみ
 * - スケーラビリティ: ESはクラスタ構成可能
 * - 検索性能: ESは大規模データでも高速（転置インデックス + HNSW）
 */
@Configuration
public class RagConfig {

    @Value("${spring.elasticsearch.uris:http://localhost:9200}")
    private String elasticsearchUri;

    /** インデックス名: ドキュメントの格納先 */
    @Value("${spring.ai.vectorstore.elasticsearch.index-name:smart-retail-rag}")
    private String indexName;

    /** ベクトル次元数: nomic-embed-text は 768次元 */
    @Value("${spring.ai.vectorstore.elasticsearch.dimensions:768}")
    private int dimensions;

    /**
     * Elasticsearch Low-Level REST Client
     * - Spring AI は内部で RestClient を使用してESと通信
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder(HttpHost.create(elasticsearchUri)).build();
    }

    /**
     * Elasticsearch Vector Store Bean
     *
     * - initializeSchema(true): 起動時に自動でインデックス作成
     * - dense_vector フィールドで cosine 類似度検索
     */
    @Bean
    public VectorStore vectorStore(RestClient restClient, EmbeddingModel embeddingModel) {
        ElasticsearchVectorStoreOptions options = new ElasticsearchVectorStoreOptions();
        options.setIndexName(indexName);
        options.setDimensions(dimensions);

        return ElasticsearchVectorStore.builder(restClient, embeddingModel)
                .options(options)
                .initializeSchema(true)  // 起動時にインデックス自動作成
                .build();
    }
}
