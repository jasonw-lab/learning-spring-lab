package lab.spring.ai.rag.dto;

import java.util.List;

public record RagResponse(String answer, List<String> sources) {
}

