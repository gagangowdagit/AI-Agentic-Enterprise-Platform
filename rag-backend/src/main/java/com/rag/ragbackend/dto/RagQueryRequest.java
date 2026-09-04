package com.rag.ragbackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RagQueryRequest {

    @NotBlank(message = "projectId is required")
    private String projectId;

    @NotBlank(message = "query is required")
    private String query;

    @NotNull(message = "topK is required")
    @Min(value = 1, message = "topK must be greater than zero")
    private Integer topK = 3;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }
}