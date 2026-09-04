package com.rag.ragbackend.processing;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class TxtTextExtractor implements TextExtractor {

    @Override
    public String extract(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Document file path is required.");
        }

        try {
            return Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read text file: " + filePath, e);
        }
    }
}
