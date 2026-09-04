package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.ApiResponse;
import com.rag.ragbackend.dto.RagQueryRequest;
import com.rag.ragbackend.entity.DocumentChunk;
import com.rag.ragbackend.processing.LlmResponse;
import com.rag.ragbackend.processing.LlmService;
import com.rag.ragbackend.processing.RagService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rag")
public class RagController {

    private final RagService ragService;
    private final LlmService llmService;

    public RagController(RagService ragService, LlmService llmService) {
        this.ragService = ragService;
        this.llmService = llmService;
    }

    @PostMapping("/query")
    public ResponseEntity<ApiResponse<LlmResponse>> query(@Valid @RequestBody RagQueryRequest request) {
        List<DocumentChunk> relevantChunks = ragService.retrieveRelevantChunks(
                request.getProjectId(), request.getQuery(), request.getTopK());
        LlmResponse response = llmService.generateAnswer(request.getQuery(), relevantChunks);
        return ResponseEntity.ok(ApiResponse.success(response, "RAG answer generated"));
    }
}