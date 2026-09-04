package com.rag.ragbackend.processing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DeterministicEmbeddingService implements EmbeddingService {

    private static final int VECTOR_DIMENSION = 8;

    @Override
    public List<List<Double>> embed(List<String> textChunks) {
        if (textChunks == null || textChunks.isEmpty()) {
            return List.of();
        }

        List<List<Double>> embeddings = new ArrayList<>(textChunks.size());
        for (String textChunk : textChunks) {
            embeddings.add(createVector(textChunk));
        }
        return embeddings;
    }

    private List<Double> createVector(String textChunk) {
        byte[] digest = digest(textChunk == null ? "" : textChunk);
        List<Double> vector = new ArrayList<>(VECTOR_DIMENSION);
        for (int index = 0; index < VECTOR_DIMENSION; index++) {
            int unsignedByte = digest[index] & 0xff;
            vector.add((unsignedByte / 127.5) - 1.0);
        }
        return List.copyOf(vector);
    }

    private byte[] digest(String text) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}