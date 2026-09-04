package com.rag.ragbackend.processing;

import com.rag.ragbackend.entity.ChunkEmbedding;
import com.rag.ragbackend.entity.DocumentChunk;
import com.rag.ragbackend.repository.ChunkEmbeddingRepository;
import com.rag.ragbackend.repository.DocumentChunkRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JavaSimilaritySearchServiceTest {

    @Test
    void searchReturnsChunksRankedByCosineSimilarityAndLimitedToTopK() {
        ChunkEmbeddingRepository embeddingRepository = mock(ChunkEmbeddingRepository.class);
        DocumentChunkRepository chunkRepository = mock(DocumentChunkRepository.class);
        JavaSimilaritySearchService searchService = new JavaSimilaritySearchService(
            embeddingRepository, chunkRepository);

        ChunkEmbedding best = new ChunkEmbedding(10L, "[1.0, 0.0]");
        ChunkEmbedding second = new ChunkEmbedding(20L, "[0.0, 1.0]");
        ChunkEmbedding third = new ChunkEmbedding(30L, "[-1.0, 0.0]");
        when(embeddingRepository.findAll()).thenReturn(List.of(second, third, best));

        DocumentChunk bestChunk = chunk(10L, "Best match");
        DocumentChunk secondChunk = chunk(20L, "Second match");
        when(chunkRepository.findAllById(List.of(10L, 20L))).thenReturn(List.of(bestChunk, secondChunk));

        List<DocumentChunk> results = searchService.search(List.of(1.0, 0.0), 2);

        assertEquals(List.of(bestChunk, secondChunk), results);
    }

    @Test
    void searchReturnsNoResultsForInvalidTopKOrZeroQuery() {
        JavaSimilaritySearchService searchService = new JavaSimilaritySearchService(
            mock(ChunkEmbeddingRepository.class), mock(DocumentChunkRepository.class));

        assertEquals(List.of(), searchService.search(List.of(1.0, 0.0), 0));
        assertEquals(List.of(), searchService.search(List.of(0.0, 0.0), 3));
    }

    private DocumentChunk chunk(Long id, String content) {
        DocumentChunk chunk = new DocumentChunk(1L, 0, content);
        chunk.setId(id);
        return chunk;
    }
}