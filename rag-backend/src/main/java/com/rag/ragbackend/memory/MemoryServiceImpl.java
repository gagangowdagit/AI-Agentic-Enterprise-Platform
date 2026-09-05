package com.rag.ragbackend.memory;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MemoryServiceImpl implements MemoryService {

    private static final int MAX_RELEVANT_MEMORIES = 5;
    private final MemoryRepository memoryRepository;

    public MemoryServiceImpl(MemoryRepository memoryRepository) {
        this.memoryRepository = memoryRepository;
    }

    @Override
    public Memory saveMemory(Memory memory) {
        if (memory == null) {
            throw new IllegalArgumentException("Memory is required.");
        }
        requireValue(memory.getProjectId(), "Project ID");
        requireValue(memory.getContent(), "Memory content");
        requireValue(memory.getMemoryType(), "Memory type");

        if (memory.getId() == null || memory.getId().isBlank()) {
            memory.setId(UUID.randomUUID().toString());
        }
        if (memory.getCreatedAt() == null) {
            memory.setCreatedAt(LocalDateTime.now());
        }
        return memoryRepository.save(memory);
    }

    @Override
    public List<Memory> getMemoriesByProjectId(String projectId) {
        requireValue(projectId, "Project ID");
        return memoryRepository.findByProjectIdOrderByCreatedAtAsc(projectId);
    }

    @Override
    public List<Memory> getRelevantMemories(String projectId, String query) {
        requireValue(query, "Memory query");
        Set<String> queryTerms = terms(query);
        return getMemoriesByProjectId(projectId).stream()
                .map(memory -> new ScoredMemory(memory, relevanceScore(memory, queryTerms)))
                .filter(scoredMemory -> scoredMemory.score() > 0)
                .sorted(Comparator.comparingInt(ScoredMemory::score).reversed())
                .limit(MAX_RELEVANT_MEMORIES)
                .map(ScoredMemory::memory)
                .toList();
    }

    private int relevanceScore(Memory memory, Set<String> queryTerms) {
        Set<String> memoryTerms = terms(memory.getContent() + " " + memory.getMemoryType());
        return (int) queryTerms.stream().filter(memoryTerms::contains).count();
    }

    private Set<String> terms(String value) {
        return java.util.Arrays.stream(value.toLowerCase(Locale.ROOT).split("\\W+"))
                .filter(term -> term.length() > 3)
                .collect(Collectors.toSet());
    }

    private void requireValue(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }

    private record ScoredMemory(Memory memory, int score) {
    }
}