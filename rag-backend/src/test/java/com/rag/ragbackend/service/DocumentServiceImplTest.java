package com.rag.ragbackend.service;

import com.rag.ragbackend.entity.Document;
import com.rag.ragbackend.processing.ProcessingService;
import com.rag.ragbackend.repository.DocumentRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DocumentServiceImplTest {

    @Test
    void getsDocumentsUsingProjectRepositoryFilter() {
        DocumentRepository documentRepository = mock(DocumentRepository.class);
        Document document = new Document("1", "notes.txt", "text/plain", 128L,
                "C:/uploads/notes.txt", null);
        when(documentRepository.findByProjectId(1)).thenReturn(List.of(document));
        DocumentService documentService = new DocumentServiceImpl(documentRepository);

        List<Document> result = documentService.getDocumentsByProjectId("1");

        assertEquals(List.of(document), result);
        verify(documentRepository).findByProjectId(1);
    }

    @Test
    void processesUploadedDocumentAutomatically() throws Exception {
        DocumentRepository documentRepository = mock(DocumentRepository.class);
        ProcessingService processingService = mock(ProcessingService.class);
        Document savedDocument = new Document("1", "notes.txt", "text/plain", 128L,
                "C:/uploads/notes.txt", null);
        savedDocument.setId(12L);
        when(documentRepository.save(org.mockito.ArgumentMatchers.any(Document.class))).thenReturn(savedDocument);
        DocumentService documentService = new DocumentServiceImpl(documentRepository, processingService);
        org.springframework.mock.web.MockMultipartFile file = new org.springframework.mock.web.MockMultipartFile(
                "file", "notes.txt", "text/plain", "document text".getBytes());

        documentService.uploadDocument("1", file);

        verify(processingService).processDocument(12L);
    }
}