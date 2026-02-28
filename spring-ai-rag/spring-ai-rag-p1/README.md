# spring-ai-rag-p1 (Phase1: SimpleVectorStore)

Spring AI + Ollama を使った最小の RAG サンプルです。Vector Store は `SimpleVectorStore`（インメモリ）を使います。

## Prerequisites

- Java 21
- Maven 3.9+
- Ollama (`http://localhost:11434`)
- Models:
  - `ollama pull llama3.2`
  - `ollama pull nomic-embed-text`

## Run

```bash
cd spring-ai-rag-p1
mvn clean package
mvn spring-boot:run
```

### Ask

```bash
curl -X POST http://localhost:8080/rag/ask \
  -H "Content-Type: application/json" \
  -d '{"question":"売上が低下している原因は？"}'
```

