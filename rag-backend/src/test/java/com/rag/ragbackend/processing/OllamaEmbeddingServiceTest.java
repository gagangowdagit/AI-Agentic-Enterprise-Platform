package com.rag.ragbackend.processing;

import com.fasterxml.jackson.databind.ObjectMapper;
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

class OllamaEmbeddingServiceTest {

    @Test
    void generatesNormalizedEmbeddingsWithoutAnApiKeyOrRealOllamaCall() {
        RestClient.Builder restClientBuilder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        server.expect(requestTo("http://localhost:11434/api/embed"))
                .andExpect(content().string(containsString("nomic-embed-text")))
                .andExpect(content().string(containsString("Document text")))
                .andRespond(withSuccess("{\"embeddings\":[[3.0,4.0]]}", MediaType.APPLICATION_JSON));

        OllamaEmbeddingService embeddingService = new OllamaEmbeddingService(
                restClientBuilder.baseUrl("http://localhost:11434").build(),
                "nomic-embed-text", new ObjectMapper());

        List<List<Double>> result = embeddingService.embed(List.of("Document text"));

        assertEquals(List.of(0.6, 0.8), result.get(0));
        server.verify();
    }
}