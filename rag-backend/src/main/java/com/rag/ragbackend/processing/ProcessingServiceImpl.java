package com.rag.ragbackend.processing;

import com.rag.ragbackend.entity.Document;
import com.rag.ragbackend.entity.DocumentChunk;
import com.rag.ragbackend.entity.ChunkEmbedding;
import com.rag.ragbackend.repository.ChunkEmbeddingRepository;
import com.rag.ragbackend.repository.DocumentChunkRepository;
import com.rag.ragbackend.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessingServiceImpl implements ProcessingService {

    private final DocumentRepository documentRepository;
    private final TxtTextExtractor txtTextExtractor;
    private final PdfTextExtractor pdfTextExtractor;
    private final DocxTextExtractor docxTextExtractor;
    private final TextChunker textChunker;
    private final DocumentChunkRepository documentChunkRepository;
    private final EmbeddingService embeddingService;
    private final ChunkEmbeddingRepository chunkEmbeddingRepository;

    public ProcessingServiceImpl(DocumentRepository documentRepository,
                                TxtTextExtractor txtTextExtractor,
                                PdfTextExtractor pdfTextExtractor,
                                DocxTextExtractor docxTextExtractor) {
        this(documentRepository, txtTextExtractor, pdfTextExtractor, docxTextExtractor, new DefaultTextChunker(), null,
            new DeterministicEmbeddingService());
    }

    public ProcessingServiceImpl(DocumentRepository documentRepository,
                                TxtTextExtractor txtTextExtractor,
                                PdfTextExtractor pdfTextExtractor,
                                DocxTextExtractor docxTextExtractor,
                                TextChunker textChunker) {
                    this(documentRepository, txtTextExtractor, pdfTextExtractor, docxTextExtractor, textChunker, null,
                        new DeterministicEmbeddingService());
    }

    public ProcessingServiceImpl(DocumentRepository documentRepository,
                                TxtTextExtractor txtTextExtractor,
                                PdfTextExtractor pdfTextExtractor,
                                DocxTextExtractor docxTextExtractor,
                                TextChunker textChunker,
                                DocumentChunkRepository documentChunkRepository) {
                    this(documentRepository, txtTextExtractor, pdfTextExtractor, docxTextExtractor, textChunker,
                        documentChunkRepository, new DeterministicEmbeddingService());
                    }

                    public ProcessingServiceImpl(DocumentRepository documentRepository,
                                TxtTextExtractor txtTextExtractor,
                                PdfTextExtractor pdfTextExtractor,
                                DocxTextExtractor docxTextExtractor,
                                TextChunker textChunker,
                                DocumentChunkRepository documentChunkRepository,
                                EmbeddingService embeddingService) {
                    this(documentRepository, txtTextExtractor, pdfTextExtractor, docxTextExtractor, textChunker,
                        documentChunkRepository, embeddingService, null);
                    }

                    @org.springframework.beans.factory.annotation.Autowired
                    public ProcessingServiceImpl(DocumentRepository documentRepository,
                                TxtTextExtractor txtTextExtractor,
                                PdfTextExtractor pdfTextExtractor,
                                DocxTextExtractor docxTextExtractor,
                                TextChunker textChunker,
                                DocumentChunkRepository documentChunkRepository,
                                EmbeddingService embeddingService,
                                ChunkEmbeddingRepository chunkEmbeddingRepository) {
        this.documentRepository = documentRepository;
        this.txtTextExtractor = txtTextExtractor;
        this.pdfTextExtractor = pdfTextExtractor;
        this.docxTextExtractor = docxTextExtractor;
        this.textChunker = textChunker;
        this.documentChunkRepository = documentChunkRepository;
        this.embeddingService = embeddingService;
        this.chunkEmbeddingRepository = chunkEmbeddingRepository;
    }

    @Override
    @Transactional
    public String processDocument(Long documentId) {
        if (documentId == null) {
            return "Document ID is required for processing.";
        }

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + documentId));

        String extractedText = document.getExtractedText();
        if (extractedText == null || extractedText.isBlank()) {
            String fileType = document.getFileType();
            String fileName = document.getFileName();

            if (isTxtDocument(fileType, fileName)) {
                extractedText = txtTextExtractor.extract(document.getFilePath());
            } else if (isPdfDocument(fileType, fileName)) {
                extractedText = pdfTextExtractor.extract(document.getFilePath());
            } else if (isDocxDocument(fileType, fileName)) {
                extractedText = docxTextExtractor.extract(document.getFilePath());
            } else if (fileType == null || fileType.isBlank()) {
                return "Document " + documentId + " has no supported file type for processing.";
            } else {
                return "Document " + documentId + " is queued for processing.";
            }
        }

        document.setExtractedText(extractedText);
        documentRepository.save(document);

        if (documentChunkRepository != null) {
            saveDocumentChunks(documentId, extractedText);
        }

        return extractedText;
    }

    private void saveDocumentChunks(Long documentId, String extractedText) {
        if (chunkEmbeddingRepository != null) {
            chunkEmbeddingRepository.deleteByDocumentId(documentId);
        }
        documentChunkRepository.deleteByDocumentId(documentId);

        List<String> chunks = textChunker.chunkText(extractedText);
        List<List<Double>> embeddings = embeddingService.embed(chunks);
        if (embeddings == null || embeddings.size() != chunks.size()) {
            throw new IllegalStateException("Embedding service must return one embedding for each document chunk.");
        }
        List<DocumentChunk> documentChunks = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            documentChunks.add(new DocumentChunk(documentId, i, chunks.get(i)));
        }
        List<DocumentChunk> savedChunks = documentChunkRepository.saveAll(documentChunks);

        if (chunkEmbeddingRepository != null) {
            List<ChunkEmbedding> chunkEmbeddings = new ArrayList<>();
            for (int i = 0; i < savedChunks.size(); i++) {
                chunkEmbeddings.add(new ChunkEmbedding(savedChunks.get(i).getId(), serializeEmbedding(embeddings.get(i))));
            }
            chunkEmbeddingRepository.saveAll(chunkEmbeddings);
        }
    }

    private String serializeEmbedding(List<Double> embedding) {
        return embedding.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(",", "[", "]"));
    }

    @Override
    public List<String> chunkDocumentText(Long documentId) {
        if (documentId == null) {
            return List.of();
        }

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + documentId));

        String textToChunk = document.getExtractedText();
        if (textToChunk == null || textToChunk.isBlank()) {
            textToChunk = processDocument(documentId);
        }

        return textChunker.chunkText(textToChunk);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentChunk> getDocumentChunks(Long documentId) {
        if (documentId == null) {
            return List.of();
        }

        return documentChunkRepository.findByDocumentIdOrderByChunkIndexAsc(documentId);
    }

    private boolean isTxtDocument(String fileType, String fileName) {
        String normalizedType = fileType == null ? "" : fileType.toLowerCase();
        String normalizedName = fileName == null ? "" : fileName.toLowerCase();

        return normalizedType.contains("text/plain") || normalizedName.endsWith(".txt");
    }

    private boolean isPdfDocument(String fileType, String fileName) {
        String normalizedType = fileType == null ? "" : fileType.toLowerCase();
        String normalizedName = fileName == null ? "" : fileName.toLowerCase();

        return normalizedType.contains("application/pdf") || normalizedName.endsWith(".pdf");
    }

    private boolean isDocxDocument(String fileType, String fileName) {
        String normalizedType = fileType == null ? "" : fileType.toLowerCase();
        String normalizedName = fileName == null ? "" : fileName.toLowerCase();

        return normalizedType.contains("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                || normalizedType.contains("application/docx")
                || normalizedName.endsWith(".docx");
    }
}
