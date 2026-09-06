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

        Document projectDocument = document(1L, "1");
        DocumentChunk projectChunk = chunk(11L, "Project content");

        when(documentRepository.findByProjectId(1)).thenReturn(List.of(projectDocument));
        when(chunkRepository.findByDocumentId(1L)).thenReturn(List.of(projectChunk));
        when(embeddingService.embed(List.of("What is relevant?"))).thenReturn(List.of(List.of(1.0, 0.0)));
        when(similaritySearchService.search(List.of(1.0, 0.0), 3, List.of(projectChunk)))
                .thenReturn(List.of(projectChunk));

        List<DocumentChunk> results = ragService.retrieveRelevantChunks("1", "What is relevant?", 3);

        assertEquals(List.of(projectChunk), results);
        assertEquals("project-document.txt", results.get(0).getDocumentFileName());
        verify(documentRepository).findByProjectId(1);
        verify(chunkRepository).findByDocumentId(1L);
    }

    @Test
    void retrievesChunksAcrossAllProjectsForGlobalScope() {
        DocumentRepository documentRepository = mock(DocumentRepository.class);
        DocumentChunkRepository chunkRepository = mock(DocumentChunkRepository.class);
        EmbeddingService embeddingService = mock(EmbeddingService.class);
        SimilaritySearchService similaritySearchService = mock(SimilaritySearchService.class);
        RagService ragService = new RagServiceImpl(
                documentRepository, chunkRepository, embeddingService, similaritySearchService);

        Document firstDocument = document(1L, "1");
        firstDocument.setFileName("first-project.txt");
        Document secondDocument = document(2L, "2");
        secondDocument.setFileName("second-project.txt");
        DocumentChunk firstChunk = chunk(11L, "First project content");
        DocumentChunk secondChunk = chunk(12L, "Second project content");

        when(documentRepository.findAll()).thenReturn(List.of(firstDocument, secondDocument));
        when(chunkRepository.findByDocumentId(1L)).thenReturn(List.of(firstChunk));
        when(chunkRepository.findByDocumentId(2L)).thenReturn(List.of(secondChunk));
        when(embeddingService.embed(List.of("What is shared?"))).thenReturn(List.of(List.of(1.0, 0.0)));
        when(similaritySearchService.search(
                List.of(1.0, 0.0), 3, List.of(firstChunk, secondChunk)))
                .thenReturn(List.of(firstChunk, secondChunk));

        List<DocumentChunk> results = ragService.retrieveRelevantChunks("global", "What is shared?", 3);

        assertEquals(List.of(firstChunk, secondChunk), results);
        assertEquals("first-project.txt", results.get(0).getDocumentFileName());
        assertEquals("second-project.txt", results.get(1).getDocumentFileName());
        verify(documentRepository).findAll();
        verify(chunkRepository).findByDocumentId(1L);
        verify(chunkRepository).findByDocumentId(2L);
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