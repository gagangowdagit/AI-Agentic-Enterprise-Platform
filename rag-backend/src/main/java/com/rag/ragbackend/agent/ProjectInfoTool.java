package com.rag.ragbackend.agent;

import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.repository.ProjectRepository;
import com.rag.ragbackend.service.ProjectService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProjectInfoTool implements Tool {

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;

    public ProjectInfoTool(ProjectService projectService, ProjectRepository projectRepository) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
    }

    @Override
    public String getName() {
        return "project-info";
    }

    @Override
    public String getDescription() {
        return "Retrieves project details by project ID.";
    }

    @Override
    public List<ToolArgument> getInputDefinition() {
        return List.of(new SimpleToolArgument("projectId", "The project identifier to fetch.", true));
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        String projectId = arguments == null ? null : String.valueOf(arguments.getOrDefault("projectId", ""));
        if (projectId == null || projectId.isBlank()) {
            throw new IllegalArgumentException("Project ID is required for project-info tool.");
        }

        Object repositoryId;
        try {
            repositoryId = Long.valueOf(projectId);
        } catch (NumberFormatException exception) {
            repositoryId = projectId;
        }
        Optional<Project> project = projectRepository.findById(repositoryId);
        if (project.isPresent()) {
            return project.get();
        }

        return projectService.getAllProjects().stream()
                .filter(item -> (item.getId() != null && item.getId().toString().equals(projectId))
                    || projectId.equals(item.getLegacyId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
    }

    private record SimpleToolArgument(String name, String description, boolean required) implements ToolArgument {
        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean isRequired() {
            return required;
        }
    }
}
