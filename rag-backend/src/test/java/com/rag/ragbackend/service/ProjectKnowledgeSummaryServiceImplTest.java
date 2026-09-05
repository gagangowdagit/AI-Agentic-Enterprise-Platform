package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.ProjectKnowledgeSummary;
import com.rag.ragbackend.entity.Document;
import com.rag.ragbackend.entity.DocumentChunk;
import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.repository.DocumentChunkRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProjectKnowledgeSummaryServiceImplTest {

    @Test
    void summarizesStoredDocumentsAndChunksForProject() {
        ProjectService projectService = mock(ProjectService.class);
        DocumentService documentService = mock(DocumentService.class);
        DocumentChunkRepository chunkRepository = mock(DocumentChunkRepository.class);
        when(projectService.getProject("project-1")).thenReturn(new Project(1L, "Alpha", "active"));

        Document processedDocument = new Document();
        processedDocument.setId(10L);
        processedDocument.setProjectId("project-1");
        processedDocument.setExtractedText("Processed content");
        Document pendingDocument = new Document();
        pendingDocument.setId(11L);
        pendingDocument.setProjectId("project-1");
        when(documentService.getDocumentsByProjectId("project-1"))
                .thenReturn(List.of(processedDocument, pendingDocument));
        when(chunkRepository.findByDocumentId(10L)).thenReturn(List.of(
                new DocumentChunk(10L, 0, "Part one"),
                new DocumentChunk(10L, 1, "Part two")));
        when(chunkRepository.findByDocumentId(11L)).thenReturn(List.of());
        ProjectKnowledgeSummaryService service = new ProjectKnowledgeSummaryServiceImpl(
                projectService, documentService, chunkRepository);

        ProjectKnowledgeSummary summary = service.getSummary("project-1");

        assertEquals(2, summary.totalDocuments());
        assertEquals(1, summary.processedDocuments());
        assertEquals(2, summary.totalDocumentChunks());
        assertEquals("PROCESSING", summary.status());
        verify(projectService).getProject("project-1");
        verify(documentService).getDocumentsByProjectId("project-1");
        verify(chunkRepository).findByDocumentId(10L);
        verify(chunkRepository).findByDocumentId(11L);
    }

    @Test
    void reportsEmptyAndReadyStatuses() {
        ProjectService projectService = mock(ProjectService.class);
        DocumentService documentService = mock(DocumentService.class);
        DocumentChunkRepository chunkRepository = mock(DocumentChunkRepository.class);
        when(projectService.getProject("project-1")).thenReturn(new Project(1L, "Alpha", "active"));
        ProjectKnowledgeSummaryService service = new ProjectKnowledgeSummaryServiceImpl(
                projectService, documentService, chunkRepository);

        when(documentService.getDocumentsByProjectId("project-1")).thenReturn(List.of());
        assertEquals("EMPTY", service.getSummary("project-1").status());

        Document document = new Document();
        document.setId(10L);
        document.setExtractedText("Processed content");
        when(documentService.getDocumentsByProjectId("project-1")).thenReturn(List.of(document));
        when(chunkRepository.findByDocumentId(10L)).thenReturn(List.of());
        assertEquals("READY", service.getSummary("project-1").status());
    }
}