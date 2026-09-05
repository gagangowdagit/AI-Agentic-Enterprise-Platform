package com.rag.ragbackend.controller;

import com.rag.ragbackend.entity.Document;
import com.rag.ragbackend.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectDocumentsControllerTest {

    @Test
    void returnsDocumentsForRequestedProjectWithMetadata() throws Exception {
        DocumentService documentService = mock(DocumentService.class);
        Document document = new Document("project-1", "requirements.pdf", "application/pdf",
                2048L, "C:/uploads/requirements.pdf", LocalDateTime.of(2026, 9, 5, 12, 30));
        document.setId(10L);
        when(documentService.getDocumentsByProjectId("project-1")).thenReturn(List.of(document));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ProjectDocumentsController(documentService))
                .build();

        mockMvc.perform(get("/api/v1/projects/project-1/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].projectId").value("project-1"))
                .andExpect(jsonPath("$[0].fileName").value("requirements.pdf"))
                .andExpect(jsonPath("$[0].fileType").value("application/pdf"))
                .andExpect(jsonPath("$[0].fileSize").value(2048))
                .andExpect(jsonPath("$[0].filePath").value("C:/uploads/requirements.pdf"))
                .andExpect(jsonPath("$[0].uploadedAt").value("2026-09-05T12:30:00"));

        verify(documentService).getDocumentsByProjectId("project-1");
    }
}