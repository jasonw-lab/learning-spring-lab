#!/usr/bin/env bash
# Elasticsearch 初期設定スクリプト
# シングルノードクラスタ用のディスクしきい値設定

ES_URL="${ES_URL:-http://localhost:9200}"

echo "Waiting for Elasticsearch to be ready..."
until curl -s "$ES_URL" >/dev/null 2>&1; do
  echo "  Elasticsearch not ready yet..."
  sleep 2
done
echo "Elasticsearch is ready!"

echo "Setting disk watermarks for single-node cluster..."
curl -s -X PUT "$ES_URL/_cluster/settings" \
  -H "Content-Type: application/json" \
  -d '{
    "persistent": {
      "cluster.routing.allocation.disk.watermark.low": "95%",
      "cluster.routing.allocation.disk.watermark.high": "97%",
      "cluster.routing.allocation.disk.watermark.flood_stage": "98%"
    }
  }'

echo ""
echo "Elasticsearch initialized successfully!"
