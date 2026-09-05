package com.rag.ragbackend.service;

import com.rag.ragbackend.entity.Document;
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
        Document document = new Document("project-1", "notes.txt", "text/plain", 128L,
                "C:/uploads/notes.txt", null);
        when(documentRepository.findByProjectId("project-1")).thenReturn(List.of(document));
        DocumentService documentService = new DocumentServiceImpl(documentRepository);

        List<Document> result = documentService.getDocumentsByProjectId("project-1");

        assertEquals(List.of(document), result);
        verify(documentRepository).findByProjectId("project-1");
    }
}