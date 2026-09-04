package com.rag.ragbackend.processing;

import com.rag.ragbackend.entity.DocumentChunk;

import java.util.List;

public interface SimilaritySearchService {

    List<DocumentChunk> search(List<Double> queryEmbedding, int topK);

    List<DocumentChunk> search(List<Double> queryEmbedding, int topK, List<DocumentChunk> candidateChunks);
}