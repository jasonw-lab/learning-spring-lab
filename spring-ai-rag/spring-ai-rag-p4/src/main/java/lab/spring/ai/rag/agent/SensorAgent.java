package lab.spring.ai.rag.agent;

import dev.langchain4j.service.SystemMessage;

/**
 * IoT/センサー監視エージェント - LangChain4j AiServices パターン
 *
 * 温度センサー、デバイスアラート、メンテナンス状況などの分析を担当
 */
public interface SensorAgent {

    @SystemMessage("""
            あなたは店舗IoT/センサー監視の専門家です。
            与えられたCONTEXTだけを根拠に、日本語で簡潔に回答してください。
            CONTEXTにないことは推測せず「情報がありません」と答えてください。
            """)
    String chat(String message);
}

