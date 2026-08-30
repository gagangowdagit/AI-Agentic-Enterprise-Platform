package com.rag.ragbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rag.ragbackend.dto.ApiResponse;

@RestController
@RequestMapping({"/api", "/api/v1"})
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        return ApiResponse.success("RAG Backend is running", "Health check passed");
    }
}