package com.rag.ragbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.ragbackend.dto.RagQueryRequest;
import com.rag.ragbackend.entity.DocumentChunk;
import com.rag.ragbackend.processing.LlmResponse;
import com.rag.ragbackend.processing.LlmService;
import com.rag.ragbackend.processing.RagService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RagControllerTest {

    private final RagService ragService = org.mockito.Mockito.mock(RagService.class);
    private final LlmService llmService = org.mockito.Mockito.mock(LlmService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc;

    RagControllerTest() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new RagController(ragService, llmService))
                .setControllerAdvice(new com.rag.ragbackend.exception.GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void returnsGeneratedAnswerAndRetrievedContext() throws Exception {
        DocumentChunk chunk = new DocumentChunk(1L, 0, "Relevant context");
        chunk.setDocumentFileName("knowledge.txt");
        DocumentChunk duplicateChunk = new DocumentChunk(1L, 0, "Repeated relevant context");
        duplicateChunk.setDocumentFileName("knowledge.txt");
        List<DocumentChunk> retrievedChunks = List.of(chunk, duplicateChunk);
        LlmResponse llmResponse = new LlmResponse("What is relevant?", "The answer", retrievedChunks);
        when(ragService.retrieveRelevantChunks("project-1", "What is relevant?", 2)).thenReturn(retrievedChunks);
        when(llmService.generateAnswer("What is relevant?", retrievedChunks)).thenReturn(llmResponse);

        mockMvc.perform(post("/api/v1/rag/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRequest("project-1", "What is relevant?", 2))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.query").value("What is relevant?"))
                .andExpect(jsonPath("$.data.answer").value("The answer"))
            .andExpect(jsonPath("$.data.contextChunks[0].content").value("Relevant context"))
                .andExpect(jsonPath("$.data.sources[0].documentId").value(1))
                .andExpect(jsonPath("$.data.sources[0].fileName").value("knowledge.txt"))
            .andExpect(jsonPath("$.data.sources[0].chunkIndex").value(0))
            .andExpect(jsonPath("$.data.sources", hasSize(1)));

        verify(ragService).retrieveRelevantChunks(eq("project-1"), eq("What is relevant?"), eq(2));
        verify(llmService).generateAnswer(eq("What is relevant?"), eq(retrievedChunks));
    }

    @Test
    void rejectsMissingQuery() throws Exception {
        mockMvc.perform(post("/api/v1/rag/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"projectId\":\"project-1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    @Test
    void rejectsNullTopK() throws Exception {
        mockMvc.perform(post("/api/v1/rag/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"projectId\":\"project-1\",\"query\":\"What is relevant?\",\"topK\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    private RagQueryRequest newRequest(String projectId, String query, Integer topK) {
        RagQueryRequest request = new RagQueryRequest();
        request.setProjectId(projectId);
        request.setQuery(query);
        request.setTopK(topK);
        return request;
    }
}