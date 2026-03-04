package lab.spring.ai.rag.dto;

import jakarta.validation.constraints.NotBlank;

public record AgentRequest(@NotBlank String question) {
}

