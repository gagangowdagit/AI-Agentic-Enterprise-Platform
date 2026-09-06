package com.rag.ragbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    private String id;

    private Integer projectId;
    private Long assignedUserId;
    private String title;
    private String description;
    private String status;
    private String priority;

    public Task() {
    }

    public Task(String id, String projectId, String title, String description, String status, String priority) {
        this.id = id;
        this.projectId = parseProjectId(projectId);
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
    }

    public Task(String id, String projectId, Long assignedUserId, String title, String description, String status, String priority) {
        this(id, projectId, title, description, status, priority);
        this.assignedUserId = assignedUserId;
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

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}