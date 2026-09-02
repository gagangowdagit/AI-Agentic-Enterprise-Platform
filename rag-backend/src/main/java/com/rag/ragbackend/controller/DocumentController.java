package com.rag.ragbackend.controller;

import com.rag.ragbackend.entity.Document;
import com.rag.ragbackend.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseEntity<List<Document>> getDocumentsByProject(@RequestParam String projectId) {
        List<Document> documents = documentService.getDocumentsByProjectId(projectId);
        return ResponseEntity.status(HttpStatus.OK).body(documents);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        Document document = documentService.getDocumentById(id);
        Resource resource = documentService.downloadDocument(id);

        String contentType = document.getFileType() != null ? document.getFileType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        boolean deleted = documentService.deleteDocument(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Document> uploadDocument(
            @RequestParam String projectId,
            @RequestParam MultipartFile file) {
        Document uploadedDocument = documentService.uploadDocument(projectId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadedDocument);
    }
}
