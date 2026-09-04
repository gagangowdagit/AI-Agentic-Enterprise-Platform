package com.rag.ragbackend.agent;

import com.rag.ragbackend.processing.LlmResponse;
import com.rag.ragbackend.processing.LlmService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentService implements Agent {

    private final LlmService llmService;

    public AgentService(LlmService llmService) {
        this.llmService = llmService;
    }

    @Override
    public LlmResponse execute(String userRequest) {
        if (userRequest == null || userRequest.isBlank()) {
            throw new IllegalArgumentException("Agent request is required.");
        }

        return llmService.generateAnswer(userRequest, List.of());
    }
}