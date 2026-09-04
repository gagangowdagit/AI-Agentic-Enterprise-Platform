package com.rag.ragbackend.processing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.ragbackend.entity.DocumentChunk;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class OllamaLlmServiceTest {

    @Test
    void generatesAnswerAndReturnsTheOriginalQueryAndContextWithoutAnApiKey() {
        RestClient.Builder restClientBuilder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        server.expect(requestTo("http://localhost:11434/api/generate"))
                .andExpect(content().string(containsString("llama3.2:3b")))
            .andExpect(content().string(containsString("Use only the retrieved context below")))
            .andExpect(content().string(containsString("Do not use outside knowledge, guess, or invent facts")))
                .andExpect(content().string(containsString("For an either/or question")))
            .andExpect(content().string(containsString("I do not know based on the provided documents")))
            .andExpect(content().string(containsString("Retrieved context:")))
                .andExpect(content().string(containsString("User question")))
                .andExpect(content().string(containsString("Relevant context")))
                .andRespond(withSuccess("{\"response\":\"The answer\"}", MediaType.APPLICATION_JSON));

        OllamaLlmService llmService = new OllamaLlmService(
                restClientBuilder.baseUrl("http://localhost:11434").build(),
                "llama3.2:3b", new ObjectMapper());
        DocumentChunk contextChunk = new DocumentChunk(1L, 0, "Relevant context");

        LlmResponse response = llmService.generateAnswer("User question", List.of(contextChunk));

        assertEquals("User question", response.query());
        assertEquals("The answer", response.answer());
        assertEquals(List.of(contextChunk), response.contextChunks());
        server.verify();
    }
}