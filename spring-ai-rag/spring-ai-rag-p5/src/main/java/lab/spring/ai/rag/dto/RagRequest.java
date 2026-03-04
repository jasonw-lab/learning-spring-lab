package lab.spring.ai.rag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RagRequest(
        @NotBlank
        @Size(max = 500)
        String question
) {
}

