# spring-ai-rag-p3 (Phase3: Tool Calling)

Phase1 の最小RAGに、Spring AI の Tool Calling を追加したサンプルです。

## Prerequisites

- Java 21
- Maven 3.9+
- Ollama (`http://localhost:11434`)
- Models:
  - `ollama pull llama3.2`
  - `ollama pull nomic-embed-text`

## Run

```bash
cd spring-ai-rag-p3
mvn clean package
mvn spring-boot:run
```

## Ask

```bash
curl -X POST http://localhost:8080/rag/ask \
  -H "Content-Type: application/json" \
  -d '{"question":"在庫が過剰なカテゴリは？必要ならツールで確認して"}'
```

