package com.rag.ragbackend.service;

import com.rag.ragbackend.entity.Document;
import com.rag.ragbackend.processing.ProcessingService;
import com.rag.ragbackend.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final ProcessingService processingService;
    private static final Path UPLOADS_DIR = Paths.get(System.getProperty("user.home"), "rag-backend-uploads")
            .toAbsolutePath()
            .normalize();

    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this(documentRepository, null);
    }

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository, ProcessingService processingService) {
        this.documentRepository = documentRepository;
        this.processingService = processingService;
    }

    private Path resolveProjectUploadPath(String projectId) {
        String safeProjectId = projectId == null ? "unknown-project" : projectId.replaceAll("[^a-zA-Z0-9._-]", "_");
        return UPLOADS_DIR.resolve(safeProjectId).toAbsolutePath().normalize();
    }

    private Path resolveStoredFilePath(String filePath) {
        Path resolvedPath = Paths.get(filePath).toAbsolutePath().normalize();
        return resolvedPath.isAbsolute() ? resolvedPath : UPLOADS_DIR.resolve(filePath).toAbsolutePath().normalize();
    }

    @Override
    public List<Document> getDocumentsByProjectId(String projectId) {
        return documentRepository.findByProjectId(Integer.valueOf(projectId));
    }

    @Override
    public void deleteDocumentsByProjectId(String projectId) {
        getDocumentsByProjectId(projectId).stream()
                .map(Document::getId)
                .forEach(this::deleteDocument);
    }

    @Override
    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
    }

    @Override
    public Document uploadDocument(String projectId, MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.isBlank()) {
                throw new RuntimeException("Uploaded file must have a valid filename.");
            }

            Path uploadsPath = resolveProjectUploadPath(projectId);
            Files.createDirectories(uploadsPath);

            String safeFileName = Paths.get(originalFileName).getFileName().toString();
            Path filePath = uploadsPath.resolve(safeFileName).toAbsolutePath().normalize();
            file.transferTo(filePath.toFile());

            Document document = new Document();
            document.setProjectId(Integer.valueOf(projectId));
            document.setFileName(safeFileName);
            document.setFileType(file.getContentType());
            document.setFileSize(file.getSize());
            document.setFilePath(filePath.toString());
            document.setUploadedAt(LocalDateTime.now());

            Document savedDocument = documentRepository.save(document);
            if (processingService != null) {
                processingService.processDocument(savedDocument.getId());
            }
            return savedDocument;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload document: " + e.getMessage(), e);
        }
    }

    @Override
    public Resource downloadDocument(Long id) {
        Document document = getDocumentById(id);
        Path filePath = resolveStoredFilePath(document.getFilePath());

        if (!Files.exists(filePath)) {
            throw new RuntimeException("Document file not found for id: " + id);
        }

        return new FileSystemResource(filePath);
    }

    @Override
    public boolean deleteDocument(Long id) {
        Document document = documentRepository.findById(id).orElse(null);
        if (document == null) {
            return false;
        }

        Path filePath = resolveStoredFilePath(document.getFilePath());

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Ignore file deletion errors so document metadata can still be removed safely.
        }

        documentRepository.delete(document);
        return true;
    }

    @Override
    public Document createDocument(Document document) {
        return documentRepository.save(document);
    }
}

