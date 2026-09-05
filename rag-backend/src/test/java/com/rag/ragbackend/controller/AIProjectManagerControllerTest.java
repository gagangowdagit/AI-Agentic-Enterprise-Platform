package com.rag.ragbackend.controller;

import com.rag.ragbackend.agent.AIProjectManager;
import com.rag.ragbackend.exception.GlobalExceptionHandler;
import com.rag.ragbackend.processing.LlmResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AIProjectManagerControllerTest {

    @Test
    void delegatesProjectQueryAndReturnsAiResponse() throws Exception {
        AIProjectManager projectManager = mock(AIProjectManager.class);
        AIProjectManagerController controller = new AIProjectManagerController(projectManager);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        LlmResponse response = new LlmResponse("Summarize progress", "Project is on track.", List.of());
        when(projectManager.manageProject("project-1", "Summarize progress")).thenReturn(response);

        mockMvc.perform(post("/api/v1/ai/projects/project-1/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"request\":\"Summarize progress\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.answer").value("Project is on track."));

        verify(projectManager).manageProject("project-1", "Summarize progress");
    }

    @Test
    void rejectsBlankProjectRequest() throws Exception {
        AIProjectManagerController controller = new AIProjectManagerController(mock(AIProjectManager.class));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(post("/api/v1/ai/projects/project-1/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"request\":\" \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }
}