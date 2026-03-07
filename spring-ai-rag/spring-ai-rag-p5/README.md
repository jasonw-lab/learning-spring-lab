# spring-ai-rag-p5 (Phase5: Security)

Phase3 の Tool Calling に、最低限のセキュリティ実装を追加したサンプルです。

## Security

- API Key 認証: `X-API-KEY` ヘッダ
- Tool のホワイトリスト: `ChatClient.Builder.defaultToolNames(...)`
- 引数バリデーション: Tool 内で拒否
- 監査ログ: Rag / Tool の実行ログ
- 同時実行制限: `POST /rag/ask` をセマフォで制限

## Run

```bash
cd spring-ai-rag-p5
mvn clean package
mvn spring-boot:run
```

## Ask

```bash
curl -X POST http://localhost:8080/rag/ask \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: change-me" \
  -d '{"question":"在庫が過剰なカテゴリは？"}'
```

