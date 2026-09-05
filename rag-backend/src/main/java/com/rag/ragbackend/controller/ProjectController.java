package com.rag.ragbackend.controller;

import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.status(HttpStatus.OK).body(projects);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProject(@PathVariable String projectId) {
        return ResponseEntity.ok(projectService.getProject(projectId));
    }

    @GetMapping("/{projectId}/details")
    public ResponseEntity<Project> getProjectDetails(@PathVariable String projectId) {
        return ResponseEntity.ok(projectService.getProjectDetails(projectId));
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@Valid @RequestBody Project project) {
        Project createdProject = projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<Project> updateProject(@PathVariable String projectId,
                                                 @Valid @RequestBody Project project) {
        return ResponseEntity.ok(projectService.updateProject(projectId, project));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable String projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }
}
