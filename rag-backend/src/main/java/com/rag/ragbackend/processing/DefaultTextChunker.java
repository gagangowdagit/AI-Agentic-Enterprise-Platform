package com.rag.ragbackend.processing;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultTextChunker implements TextChunker {

    private static final int TARGET_CHUNK_SIZE = 750;
    private static final int OVERLAP_SIZE = 100;

    @Override
    public List<String> chunkText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String normalizedText = text.trim();
        if (normalizedText.length() <= TARGET_CHUNK_SIZE) {
            return List.of(normalizedText);
        }

        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < normalizedText.length()) {
            int end = Math.min(start + TARGET_CHUNK_SIZE, normalizedText.length());
            String chunk = normalizedText.substring(start, end);
            chunks.add(chunk);

            if (end == normalizedText.length()) {
                break;
            }

            start = Math.max(start + TARGET_CHUNK_SIZE - OVERLAP_SIZE, end - OVERLAP_SIZE);
        }

        return chunks;
    }
}
