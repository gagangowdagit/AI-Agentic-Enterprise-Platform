package com.rag.ragbackend.processing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class PdfTextExtractor implements TextExtractor {

    @Override
    public String extract(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Document file path is required.");
        }

        try (PDDocument document = PDDocument.load(Path.of(filePath).toFile())) {
            if (document.isEncrypted()) {
                throw new IllegalStateException("PDF is encrypted and cannot be processed: " + filePath);
            }

            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read PDF file: " + filePath, e);
        }
    }
}
