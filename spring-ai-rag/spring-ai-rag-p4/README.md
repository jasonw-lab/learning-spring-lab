# spring-ai-rag-p4 (Phase4: LangChain4j Multi-Agent)

Spring AI を RAG(Embedding + VectorStore) に使い、LangChain4j を Agent(回答生成/分岐) に使うハイブリッド例です。

## Prerequisites

- Java 21
- Maven 3.9+
- Ollama (`http://localhost:11434`)
- Models:
  - `ollama pull llama3.2`
  - `ollama pull nomic-embed-text`

## Run

```bash
cd spring-ai-rag-p4
mvn clean package
mvn spring-boot:run
```

## Ask

```bash
curl -X POST http://localhost:8080/agent/ask \
  -H "Content-Type: application/json" \
  -d '{"question":"売上が低下している原因は？"}'
```

