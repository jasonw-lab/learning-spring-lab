package lab.spring.ai.rag.agent;

import dev.langchain4j.service.SystemMessage;

/**
 * 売上分析エージェント - LangChain4j AiServices パターン
 *
 * フロー図: docs/diagrams/flow_p4_multi_agent.drawio
 *
 * 本番では以下を追加検討:
 * - @UserMessage: ユーザーメッセージのテンプレート
 * - ChatMemory: 会話履歴の保持
 * - Tools: Function Calling 連携
 */
public interface SalesAgent {

    @SystemMessage("""
            あなたは売上分析の専門家です。
            与えられたCONTEXTだけを根拠に、日本語で簡潔に回答してください。
            CONTEXTにないことは推測せず「情報がありません」と答えてください。
            """)
    String chat(String message);
}

