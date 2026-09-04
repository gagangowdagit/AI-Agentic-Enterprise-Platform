package com.rag.ragbackend.processing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OllamaEmbeddingService implements EmbeddingService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String model;

    @Autowired
    public OllamaEmbeddingService(@Value("${ollama.api.url}") String apiUrl,
                                  @Value("${ollama.embedding.model}") String model) {
        this(RestClient.builder().baseUrl(apiUrl).build(), model, new ObjectMapper());
    }

    OllamaEmbeddingService(RestClient restClient, String model, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.model = model;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<List<Double>> embed(List<String> textChunks) {
        if (textChunks == null || textChunks.isEmpty()) {
            return List.of();
        }

        Map<String, Object> request = new HashMap<>();
        request.put("model", model);
        request.put("input", textChunks);

        String response = restClient.post()
                .uri("/api/embed")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);

        return readEmbeddings(response, textChunks.size());
    }

    private List<List<Double>> readEmbeddings(String serializedResponse, int expectedCount) {
        try {
            JsonNode embeddings = objectMapper.readTree(serializedResponse).path("embeddings");
            if (!embeddings.isArray() || embeddings.size() != expectedCount) {
                throw new IllegalStateException("Ollama returned an unexpected number of embeddings.");
            }

            return java.util.stream.StreamSupport.stream(embeddings.spliterator(), false)
                    .map(this::readEmbedding)
                    .toList();
        } catch (IllegalStateException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException("Ollama returned an invalid embedding response.", exception);
        }
    }

    private List<Double> readEmbedding(JsonNode values) {
        if (!values.isArray() || values.isEmpty()) {
            throw new IllegalStateException("Ollama returned an empty embedding.");
        }

        double magnitudeSquared = 0;
        java.util.ArrayList<Double> embedding = new java.util.ArrayList<>(values.size());
        for (JsonNode value : values) {
            if (!value.isNumber()) {
                throw new IllegalStateException("Ollama returned a non-numeric embedding value.");
            }
            double numericValue = value.doubleValue();
            embedding.add(numericValue);
            magnitudeSquared += numericValue * numericValue;
        }

        double magnitude = Math.sqrt(magnitudeSquared);
        if (magnitude == 0) {
            throw new IllegalStateException("Ollama returned a zero-length embedding.");
        }
        return embedding.stream().map(value -> value / magnitude).toList();
    }
}