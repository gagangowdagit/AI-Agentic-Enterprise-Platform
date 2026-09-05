package com.rag.ragbackend.controller;

import com.rag.ragbackend.entity.Document;
import com.rag.ragbackend.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectDocumentsController {

    private final DocumentService documentService;

    public ProjectDocumentsController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/{projectId}/documents")
    public ResponseEntity<List<Document>> getProjectDocuments(@PathVariable String projectId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(documentService.getDocumentsByProjectId(projectId));
    }
}