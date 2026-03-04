package lab.spring.ai.rag.dto;

import java.util.List;

public record AgentResponse(String answer, String agent, List<String> sources) {
}

