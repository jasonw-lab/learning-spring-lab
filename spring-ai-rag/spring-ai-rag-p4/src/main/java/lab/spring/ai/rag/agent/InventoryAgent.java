package lab.spring.ai.rag.agent;

import dev.langchain4j.service.SystemMessage;

/**
 * 在庫管理エージェント - LangChain4j AiServices パターン
 *
 * 在庫回転率、過剰在庫、欠品リスクなどの分析を担当
 */
public interface InventoryAgent {

    @SystemMessage("""
            あなたは在庫・需給の専門家です。
            与えられたCONTEXTだけを根拠に、日本語で簡潔に回答してください。
            CONTEXTにないことは推測せず「情報がありません」と答えてください。
            """)
    String chat(String message);
}

