package com.rag.ragbackend.processing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.ragbackend.entity.ChunkEmbedding;
import com.rag.ragbackend.entity.DocumentChunk;
import com.rag.ragbackend.repository.ChunkEmbeddingRepository;
import com.rag.ragbackend.repository.DocumentChunkRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JavaSimilaritySearchService implements SimilaritySearchService {

    private final ChunkEmbeddingRepository chunkEmbeddingRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final ObjectMapper objectMapper;

    public JavaSimilaritySearchService(ChunkEmbeddingRepository chunkEmbeddingRepository,
                                       DocumentChunkRepository documentChunkRepository) {
        this.chunkEmbeddingRepository = chunkEmbeddingRepository;
        this.documentChunkRepository = documentChunkRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<DocumentChunk> search(List<Double> queryEmbedding, int topK) {
        return search(queryEmbedding, topK, null);
    }

    @Override
    public List<DocumentChunk> search(List<Double> queryEmbedding, int topK,
                                     List<DocumentChunk> candidateChunks) {
        if (queryEmbedding == null || queryEmbedding.isEmpty() || topK <= 0) {
            return List.of();
        }

        double queryMagnitude = magnitude(queryEmbedding);
        if (queryMagnitude == 0) {
            return List.of();
        }

        Map<Long, DocumentChunk> chunksById = new HashMap<>();
        if (candidateChunks != null) {
            for (DocumentChunk chunk : candidateChunks) {
                chunksById.put(chunk.getId(), chunk);
            }
        }

        List<ScoredChunk> scoredChunks = new ArrayList<>();
        for (ChunkEmbedding storedEmbedding : chunkEmbeddingRepository.findAll()) {
            if (candidateChunks != null && !chunksById.containsKey(storedEmbedding.getChunkId())) {
                continue;
            }

            List<Double> embedding = parseEmbedding(storedEmbedding.getEmbedding());
            if (embedding.size() != queryEmbedding.size()) {
                throw new IllegalArgumentException("Embedding dimension does not match the query dimension.");
            }

            double embeddingMagnitude = magnitude(embedding);
            if (embeddingMagnitude == 0) {
                continue;
            }

            scoredChunks.add(new ScoredChunk(
                    storedEmbedding.getChunkId(),
                    cosineSimilarity(queryEmbedding, embedding, queryMagnitude, embeddingMagnitude)));
        }

        scoredChunks.sort(Comparator.comparingDouble(ScoredChunk::score).reversed()
                .thenComparing(ScoredChunk::chunkId));
        List<ScoredChunk> topMatches = scoredChunks.stream().limit(topK).toList();

        if (candidateChunks == null) {
            for (DocumentChunk chunk : documentChunkRepository.findAllById(
                    topMatches.stream().map(ScoredChunk::chunkId).toList())) {
                chunksById.put(chunk.getId(), chunk);
            }
        }

        return topMatches.stream()
                .map(match -> chunksById.get(match.chunkId()))
                .filter(chunk -> chunk != null)
                .toList();
    }

    private List<Double> parseEmbedding(String serializedEmbedding) {
        try {
            JsonNode values = objectMapper.readTree(serializedEmbedding);
            if (values == null || !values.isArray()) {
                throw new IllegalArgumentException("Stored embedding must be a JSON array.");
            }

            List<Double> embedding = new ArrayList<>(values.size());
            for (JsonNode value : values) {
                if (!value.isNumber()) {
                    throw new IllegalArgumentException("Stored embedding contains a non-numeric value.");
                }
                embedding.add(value.doubleValue());
            }
            return embedding;
        } catch (Exception exception) {
            if (exception instanceof IllegalArgumentException illegalArgumentException) {
                throw illegalArgumentException;
            }
            throw new IllegalArgumentException("Stored embedding is not valid JSON.", exception);
        }
    }

    private double cosineSimilarity(List<Double> left, List<Double> right,
                                    double leftMagnitude, double rightMagnitude) {
        double dotProduct = 0;
        for (int index = 0; index < left.size(); index++) {
            dotProduct += left.get(index) * right.get(index);
        }
        return dotProduct / (leftMagnitude * rightMagnitude);
    }

    private double magnitude(List<Double> vector) {
        double sumOfSquares = 0;
        for (Double value : vector) {
            if (value == null || !Double.isFinite(value)) {
                throw new IllegalArgumentException("Embedding values must be finite numbers.");
            }
            sumOfSquares += value * value;
        }
        return Math.sqrt(sumOfSquares);
    }

    private record ScoredChunk(Long chunkId, double score) {
    }
}