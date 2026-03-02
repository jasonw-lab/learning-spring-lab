package lab.spring.ai.rag.controller;

import jakarta.validation.Valid;
import lab.spring.ai.rag.dto.AgentRequest;
import lab.spring.ai.rag.dto.AgentResponse;
import lab.spring.ai.rag.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @PostMapping("/ask")
    public AgentResponse ask(@Valid @RequestBody AgentRequest request) {
        return agentService.ask(request);
    }
}

