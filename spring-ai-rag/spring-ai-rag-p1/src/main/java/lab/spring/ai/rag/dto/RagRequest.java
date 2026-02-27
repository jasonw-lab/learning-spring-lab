package lab.spring.ai.rag.dto;

import jakarta.validation.constraints.NotBlank;

public record RagRequest(
        @NotBlank(message = "question is required")
        String question
) {
}
