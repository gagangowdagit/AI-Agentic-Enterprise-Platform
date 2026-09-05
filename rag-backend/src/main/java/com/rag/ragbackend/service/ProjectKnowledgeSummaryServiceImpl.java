package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.ProjectKnowledgeSummary;
import com.rag.ragbackend.entity.Document;
import com.rag.ragbackend.repository.DocumentChunkRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectKnowledgeSummaryServiceImpl implements ProjectKnowledgeSummaryService {

    private final ProjectService projectService;
    private final DocumentService documentService;
    private final DocumentChunkRepository documentChunkRepository;

    public ProjectKnowledgeSummaryServiceImpl(
            ProjectService projectService,
            DocumentService documentService,
            DocumentChunkRepository documentChunkRepository) {
        this.projectService = projectService;
        this.documentService = documentService;
        this.documentChunkRepository = documentChunkRepository;
    }

    @Override
    public ProjectKnowledgeSummary getSummary(String projectId) {
        projectService.getProject(projectId);
        List<Document> documents = documentService.getDocumentsByProjectId(projectId);

        int processedDocuments = 0;
        int totalDocumentChunks = 0;
        for (Document document : documents) {
            int documentChunkCount = document.getId() == null
                    ? 0
                    : documentChunkRepository.findByDocumentId(document.getId()).size();
            totalDocumentChunks += documentChunkCount;

            if ((document.getExtractedText() != null && !document.getExtractedText().isBlank())
                    || documentChunkCount > 0) {
                processedDocuments++;
            }
        }

        String status = determineStatus(documents.size(), processedDocuments);
        return new ProjectKnowledgeSummary(
                documents.size(),
                processedDocuments,
                totalDocumentChunks,
                status);
    }

    private String determineStatus(int totalDocuments, int processedDocuments) {
        if (totalDocuments == 0) {
            return "EMPTY";
        }
        return processedDocuments == totalDocuments ? "READY" : "PROCESSING";
    }
}