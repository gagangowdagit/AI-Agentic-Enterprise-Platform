package com.rag.ragbackend.dto;

public record ProjectKnowledgeSummary(
        int totalDocuments,
        int processedDocuments,
        int totalDocumentChunks,
        String status) {
}