package com.rag.ragbackend.processing;

import com.rag.ragbackend.entity.DocumentChunk;
import com.rag.ragbackend.memory.Memory;

import java.util.List;

public interface LlmService {

    LlmResponse generateAnswer(String query, List<DocumentChunk> contextChunks);

    default LlmResponse generateAnswer(String query, List<DocumentChunk> contextChunks, Object toolResult) {
        return generateAnswer(query, contextChunks);
    }

    default LlmResponse generateAnswer(
            String query,
            List<DocumentChunk> contextChunks,
            Object toolResult,
            List<Memory> memories) {
        return generateAnswer(query, contextChunks, toolResult);
    }
}