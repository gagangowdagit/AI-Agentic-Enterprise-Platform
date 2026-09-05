package com.rag.ragbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    @JsonIgnore
    private String legacyId;

    @NotBlank
    private String name;

    @NotBlank
    private String status;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProjectPriority priority;

    public Project() {
    }

    public Project(String id, String name, String status) {
        this.legacyId = id;
        this.name = name;
        this.status = status;
        this.priority = ProjectPriority.MEDIUM;
    }

    public Project(Long id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.priority = ProjectPriority.MEDIUM;
    }

    public Project(String id, String name, String description, String status,
                   LocalDate startDate, LocalDate endDate, ProjectPriority priority) {
        this.legacyId = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priority = priority;
    }

    public Project(Long id, String name, String description, String status,
                   LocalDate startDate, LocalDate endDate, ProjectPriority priority) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLegacyId() {
        return legacyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public ProjectPriority getPriority() {
        return priority;
    }

    public void setPriority(ProjectPriority priority) {
        this.priority = priority;
    }
}
