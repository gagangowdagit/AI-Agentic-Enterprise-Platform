package com.rag.ragbackend.processing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.ragbackend.entity.DocumentChunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OllamaLlmService implements LlmService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String model;

    @Autowired
    public OllamaLlmService(@Value("${ollama.api.url}") String apiUrl,
                            @Value("${ollama.llm.model}") String model) {
        this(RestClient.builder().baseUrl(apiUrl).build(), model, new ObjectMapper());
    }

    OllamaLlmService(RestClient restClient, String model, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.model = model;
        this.objectMapper = objectMapper;
    }

    @Override
    public LlmResponse generateAnswer(String query, List<DocumentChunk> contextChunks) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Query is required.");
        }

        List<DocumentChunk> context = contextChunks == null ? List.of() : List.copyOf(contextChunks);
        Map<String, Object> request = new HashMap<>();
        request.put("model", model);
        request.put("prompt", buildPrompt(query, context));
        request.put("stream", false);

        String response = restClient.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);

        return new LlmResponse(query, readAnswer(response), context);
    }

    private String buildPrompt(String query, List<DocumentChunk> contextChunks) {
        StringBuilder prompt = new StringBuilder(
            "You are answering a question about the user's documents. Use only the retrieved context below. "
                + "Do not use outside knowledge, guess, or invent facts. "
                        + "For an either/or question, compare each option with the context and select the option explicitly supported by the context, even if the other option is absent; state that the unsupported option is not stated. "
                + "If the answer is not stated or supported by the context, say: I do not know based on the provided documents.\n\n"
                + "Retrieved context:\n");
        for (int index = 0; index < contextChunks.size(); index++) {
            prompt.append("[Chunk ").append(index + 1).append("]\n")
                    .append(contextChunks.get(index).getContent()).append("\n");
        }
        return prompt.append("\nQuestion:\n").append(query)
            .append("\n\nAnswer using only the retrieved context.").toString();
    }

    private String readAnswer(String serializedResponse) {
        try {
            JsonNode answer = objectMapper.readTree(serializedResponse).path("response");
            if (!answer.isTextual() || answer.asText().isBlank()) {
                throw new IllegalStateException("Ollama returned no answer.");
            }
            return answer.asText();
        } catch (IllegalStateException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException("Ollama returned an invalid answer response.", exception);
        }
    }
}