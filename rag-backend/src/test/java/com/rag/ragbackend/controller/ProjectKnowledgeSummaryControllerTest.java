package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.ProjectKnowledgeSummary;
import com.rag.ragbackend.service.ProjectKnowledgeSummaryService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectKnowledgeSummaryControllerTest {

    @Test
    void returnsKnowledgeSummaryForProject() throws Exception {
        ProjectKnowledgeSummaryService service = mock(ProjectKnowledgeSummaryService.class);
        when(service.getSummary("project-1")).thenReturn(new ProjectKnowledgeSummary(3, 2, 8, "READY"));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ProjectKnowledgeSummaryController(service)).build();

        mockMvc.perform(get("/api/v1/projects/project-1/knowledge-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalDocuments").value(3))
                .andExpect(jsonPath("$.data.processedDocuments").value(2))
                .andExpect(jsonPath("$.data.totalDocumentChunks").value(8))
                .andExpect(jsonPath("$.data.status").value("READY"));

        verify(service).getSummary("project-1");
    }
}