package lab.spring.ai.rag.controller;

import jakarta.validation.Valid;
import lab.spring.ai.rag.dto.RagRequest;
import lab.spring.ai.rag.dto.RagResponse;
import lab.spring.ai.rag.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;

    @PostMapping("/ask")
    public RagResponse ask(@Valid @RequestBody RagRequest request) {
        return ragService.ask(request);
    }
}

