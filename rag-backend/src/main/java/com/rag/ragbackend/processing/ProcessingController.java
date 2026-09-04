package com.rag.ragbackend.processing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/documents")
public class ProcessingController {

    private final ProcessingService processingService;

    public ProcessingController(ProcessingService processingService) {
        this.processingService = processingService;
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<String> processDocument(@PathVariable Long id) {
        String result = processingService.processDocument(id);
        return ResponseEntity.ok(result);
    }
}
