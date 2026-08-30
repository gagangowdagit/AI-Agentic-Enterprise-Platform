package com.rag.ragbackend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rag.ragbackend.dto.ApiResponse;

@RestController
@RequestMapping("/api/test")
class TestValidationController {

    @PostMapping("/validate")
    public ApiResponse<String> validate(@Valid @RequestBody TestRequest request) {
        return ApiResponse.success("validated", "Validation successful");
    }

    public static class TestRequest {
        @NotBlank(message = "name is required")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
