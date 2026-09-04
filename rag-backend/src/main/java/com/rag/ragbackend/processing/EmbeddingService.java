package com.rag.ragbackend.processing;

import java.util.List;

public interface EmbeddingService {

    List<List<Double>> embed(List<String> textChunks);
}