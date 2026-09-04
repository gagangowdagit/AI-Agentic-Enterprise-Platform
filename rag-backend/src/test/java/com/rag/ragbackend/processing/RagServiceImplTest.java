package com.rag.ragbackend.processing;

import com.rag.ragbackend.entity.Document;
import com.rag.ragbackend.entity.DocumentChunk;
import com.rag.ragbackend.repository.DocumentChunkRepository;
import com.rag.ragbackend.repository.DocumentRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RagServiceImplTest {

    @Test
    void retrievesOnlyProjectChunksUsingQueryEmbeddingAndTopK() {
        DocumentRepository documentRepository = mock(DocumentRepository.class);
        DocumentChunkRepository chunkRepository = mock(DocumentChunkRepository.class);
        EmbeddingService embeddingService = mock(EmbeddingService.class);
        SimilaritySearchService similaritySearchService = mock(SimilaritySearchService.class);
        RagService ragService = new RagServiceImpl(
                documentRepository, chunkRepository, embeddingService, similaritySearchService);

        Document projectDocument = document(1L, "project-1");
        DocumentChunk projectChunk = chunk(11L, "Project content");

        when(documentRepository.findByProjectId("project-1")).thenReturn(List.of(projectDocument));
        when(chunkRepository.findByDocumentId(1L)).thenReturn(List.of(projectChunk));
        when(embeddingService.embed(List.of("What is relevant?"))).thenReturn(List.of(List.of(1.0, 0.0)));
        when(similaritySearchService.search(List.of(1.0, 0.0), 3, List.of(projectChunk)))
                .thenReturn(List.of(projectChunk));

        List<DocumentChunk> results = ragService.retrieveRelevantChunks("project-1", "What is relevant?", 3);

        assertEquals(List.of(projectChunk), results);
        assertEquals("project-document.txt", results.get(0).getDocumentFileName());
        verify(documentRepository).findByProjectId("project-1");
        verify(chunkRepository).findByDocumentId(1L);
    }

    private Document document(Long id, String projectId) {
        Document document = new Document();
        document.setId(id);
        document.setProjectId(projectId);
        document.setFileName("project-document.txt");
        return document;
    }

    private DocumentChunk chunk(Long id, String content) {
        DocumentChunk chunk = new DocumentChunk(1L, 0, content);
        chunk.setId(id);
        return chunk;
    }
}