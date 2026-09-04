package com.rag.ragbackend.processing;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Component
public class DocxTextExtractor implements TextExtractor {

    @Override
    public String extract(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Document file path is required.");
        }

        try (InputStream inputStream = Files.newInputStream(Path.of(filePath));
             XWPFDocument document = new XWPFDocument(inputStream)) {
            return document.getParagraphs()
                    .stream()
                    .map(XWPFParagraph::getText)
                    .filter(text -> !text.isBlank())
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read DOCX file: " + filePath, e);
        }
    }
}
