package com.rag.ragbackend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.rag.ragbackend.exception.GlobalExceptionHandler;

class ApiContractTest {

    @Test
    void healthEndpointReturnsStandardApiResponse() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new HealthController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value("RAG Backend is running"));
    }

    @Test
    void validationErrorsReturnStandardErrorResponse() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ExampleValidationController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        mockMvc.perform(post("/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }
}
