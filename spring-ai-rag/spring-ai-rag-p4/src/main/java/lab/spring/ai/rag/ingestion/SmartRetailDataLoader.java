package lab.spring.ai.rag.ingestion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmartRetailDataLoader implements ApplicationRunner {

    private final VectorStore vectorStore;

    @Override
    public void run(ApplicationArguments args) {
        try {
            List<Document> existingDocs = vectorStore.similaritySearch(
                    SearchRequest.builder().query("商品ID").topK(1).build()
            );
            if (!existingDocs.isEmpty()) {
                return;
            }
        } catch (Exception ignored) {
        }

        List<Document> documents = List.of(
                new Document(
                        "商品ID: P001, 商品名: 北海道産牛乳1L, カテゴリ: 冷蔵食品, ステータス: 販売中, 在庫回転率が低下傾向、賞味期限切れ廃棄が月間12件に増加",
                        Map.of("type", "product", "productId", "P001")
                ),
                new Document(
                        "店舗ID: S003, 期間: 2025年10月, カテゴリ: 冷凍食品, 売上金額: ¥1,250,000, 前月比: -20%, 客数: 3,200人, 客単価: ¥390",
                        Map.of("type", "sales", "storeId", "S003")
                ),
                new Document(
                        "デバイスID: D012, 種別: 温度センサー, 設置場所: 冷蔵棚3番, アラート件数: 10件/月, ステータス: 要点検, 最終メンテナンス: 2025-08-15",
                        Map.of("type", "device", "deviceId", "D012")
                ),
                new Document(
                        "商品カテゴリ: 調味料, 在庫数量: 2,450個, 在庫金額: ¥735,000, 回転日数: 45日（業界平均15日）, 判定: 過剰在庫",
                        Map.of("type", "inventory", "category", "調味料")
                )
        );

        vectorStore.add(documents);
        log.info("Loaded {} documents into VectorStore", documents.size());
    }
}

