package com.rag.ragbackend.service;

import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.exception.ProjectNotFoundException;
import com.rag.ragbackend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final DocumentService documentService;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this(projectRepository, null);
    }

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, DocumentService documentService) {
        this.projectRepository = projectRepository;
        this.documentService = documentService;
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Project getProject(String projectId) {
        return projectRepository.findById(toRepositoryId(projectId))
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
    }

    @Override
    public Project getProjectDetails(String projectId) {
        return getProject(projectId);
    }

    @Override
    public Project createProject(Project project) {
        project.setId(null);
        return projectRepository.save(project);
    }

    @Override
    public Project updateProject(String projectId, Project project) {
        Project existingProject = getProject(projectId);
        existingProject.setName(project.getName());
        existingProject.setDescription(project.getDescription());
        existingProject.setStatus(project.getStatus());
        existingProject.setStartDate(project.getStartDate());
        existingProject.setEndDate(project.getEndDate());
        existingProject.setPriority(project.getPriority());
        existingProject.setDepartment(project.getDepartment());
        return projectRepository.save(existingProject);
    }

    @Override
    public void deleteProject(String projectId) {
        Project existingProject = getProject(projectId);
        if (documentService != null) {
            documentService.deleteDocumentsByProjectId(projectId);
        }
        projectRepository.delete(existingProject);
    }

    private Object toRepositoryId(String projectId) {
        try {
            return Long.valueOf(projectId);
        } catch (NumberFormatException exception) {
            return projectId;
        }
    }
}
