package com.rag.ragbackend.processing;

import com.rag.ragbackend.entity.Document;
import com.rag.ragbackend.entity.DocumentChunk;
import com.rag.ragbackend.exception.ProjectNotFoundException;
import com.rag.ragbackend.repository.DocumentChunkRepository;
import com.rag.ragbackend.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RagServiceImpl implements RagService {

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final EmbeddingService embeddingService;
    private final SimilaritySearchService similaritySearchService;

    public RagServiceImpl(DocumentRepository documentRepository,
                          DocumentChunkRepository documentChunkRepository,
                          EmbeddingService embeddingService,
                          SimilaritySearchService similaritySearchService) {
        this.documentRepository = documentRepository;
        this.documentChunkRepository = documentChunkRepository;
        this.embeddingService = embeddingService;
        this.similaritySearchService = similaritySearchService;
    }

    @Override
    public List<DocumentChunk> retrieveRelevantChunks(String projectId, String query, int topK) {
        if (projectId == null || projectId.isBlank() || query == null || query.isBlank() || topK <= 0) {
            return List.of();
        }

        List<List<Double>> queryEmbeddings = embeddingService.embed(List.of(query));
        if (queryEmbeddings == null || queryEmbeddings.size() != 1) {
            throw new IllegalStateException("Embedding service must return one embedding for the query.");
        }

        List<Document> projectDocuments = documentRepository.findByProjectId(projectId);
        if (projectDocuments.isEmpty()) {
            throw new ProjectNotFoundException(projectId);
        }

        List<DocumentChunk> projectChunks = new ArrayList<>();
        for (Document document : projectDocuments) {
            List<DocumentChunk> documentChunks = documentChunkRepository.findByDocumentId(document.getId());
            documentChunks.forEach(chunk -> chunk.setDocumentFileName(document.getFileName()));
            projectChunks.addAll(documentChunks);
        }

        return similaritySearchService.search(queryEmbeddings.get(0), topK, projectChunks);
    }
}