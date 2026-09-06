package com.rag.ragbackend.memory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "memories")
public class Memory {

    @Id
    private String id;

    @Column(name = "project_id", nullable = false)
    private Integer projectId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "memory_type", nullable = false)
    private String memoryType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Memory() {
    }

    public Memory(String projectId, String content, String memoryType) {
        this(UUID.randomUUID().toString(), projectId, content, memoryType, LocalDateTime.now());
    }

    public Memory(String id, String projectId, String content, String memoryType, LocalDateTime createdAt) {
        this.id = id;
        this.projectId = parseProjectId(projectId);
        this.content = content;
        this.memoryType = memoryType;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = parseProjectId(projectId);
    }

    private static Integer parseProjectId(String projectId) {
        if (projectId == null || projectId.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(projectId);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMemoryType() {
        return memoryType;
    }

    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}