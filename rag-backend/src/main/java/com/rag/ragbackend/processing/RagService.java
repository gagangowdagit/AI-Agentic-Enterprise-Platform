package com.rag.ragbackend.processing;

import com.rag.ragbackend.entity.DocumentChunk;

import java.util.List;

public interface RagService {

    List<DocumentChunk> retrieveRelevantChunks(String projectId, String query, int topK);
}