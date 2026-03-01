# spring-ai-rag-p2 (Phase2: Elasticsearch Vector Store)

Spring AI + Ollama を使用した店舗運営データRAGアシスタントのプロトタイプ。

## 概要

店舗運営データ（商品マスタ、売上レポート、デバイスログ、在庫状況）を自然言語で横断検索できるAIアシスタント。
RAG (Retrieval-Augmented Generation) パターンにより、LLMが店舗データを参照しながら質問に回答する。

### RAGフロー

```
ユーザー質問 → Embedding生成 → Vector検索(Top-K) → Context結合 → Prompt注入 → LLM回答生成
```

### 技術スタック

| レイヤ | 技術 |
|--------|------|
| Java | 21 (LTS) |
| Backend | Spring Boot 3.4.x |
| LLM Framework | Spring AI 1.x |
| LLM | Ollama (llama3.2) |
| Embedding | Ollama (nomic-embed-text) |
| Vector Store | Elasticsearch (Vector Store) |

## 前提条件

- Java 21
- Maven 3.9+
- Ollama
- Elasticsearch 8.x (ローカル)

## セットアップ

### 1. Ollama インストール

```bash
# macOS
brew install ollama

# または公式サイトからダウンロード
# https://ollama.ai/download
```

### 2. モデル導入

```bash
ollama pull llama3.2
ollama pull nomic-embed-text
```

### 3. Ollama 起動

```bash
ollama serve
```

### 4. 起動確認

```bash
curl http://localhost:11434/api/tags
```

## ビルド・起動

```bash
# Elasticsearch 起動（Docker）
docker-compose up -d

# ビルド
mvn clean package

# 起動
mvn spring-boot:run
```

Elasticsearch が起動していない場合はデータ投入が失敗します（ログに警告が出ます）。

## 動作確認

### RAG質問

```bash
curl -X POST http://localhost:8080/rag/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "売上が低下している原因は？"}'
```

### レスポンス例

```json
{
  "answer": "売上が低下している原因として、店舗S003の冷凍食品カテゴリで前月比-20%の売上減少が確認されています...",
  "sources": [
    "店舗ID: S003, 期間: 2025年10月, カテゴリ: 冷凍食品, 売上金額: ¥1,250,000, 前月比: -20%...",
    "..."
  ]
}
```

### 質問例

- 「売上が低下している原因は？」
- 「在庫が過剰な商品カテゴリは？」
- 「点検が必要なデバイスは？」
- 「北海道産牛乳の状況を教えて」

## サンプルデータ

起動時に以下のデータがVectorStoreに投入される:

| タイプ | 内容 |
|--------|------|
| 商品マスタ | 北海道産牛乳1L - 在庫回転率低下、賞味期限切れ廃棄増加 |
| 売上レポート | 店舗S003 冷凍食品 - 前月比-20% |
| デバイスログ | 温度センサーD012 - アラート10件/月、要点検 |
| 在庫状況 | 調味料カテゴリ - 過剰在庫（回転日数45日） |

## プロジェクト構成

```
src/main/java/lab/spring/ai/rag/
├── RagDemoApplication.java      # メインクラス
├── config/
│   └── RagConfig.java           # VectorStore Bean定義
├── dto/
│   ├── RagRequest.java          # リクエストDTO
│   └── RagResponse.java         # レスポンスDTO
├── ingestion/
│   └── SmartRetailDataLoader.java  # サンプルデータ投入
├── service/
│   └── RagService.java          # RAGフロー実装
└── controller/
    └── RagController.java       # REST API
```

## API

### POST /rag/ask

RAG質問エンドポイント

**Request:**
```json
{
  "question": "質問文"
}
```

**Response:**
```json
{
  "answer": "LLMの回答",
  "sources": ["参照したドキュメント1", "参照したドキュメント2", ...]
}
```
