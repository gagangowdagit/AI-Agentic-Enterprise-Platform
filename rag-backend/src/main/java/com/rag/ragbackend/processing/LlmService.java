package com.rag.ragbackend.processing;

import com.rag.ragbackend.entity.DocumentChunk;

import java.util.List;

public interface LlmService {

    LlmResponse generateAnswer(String query, List<DocumentChunk> contextChunks);
}