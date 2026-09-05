package com.rag.ragbackend.service;

import com.rag.ragbackend.entity.Project;

import java.util.List;

public interface ProjectService {

    List<Project> getAllProjects();

    Project getProject(String projectId);

    Project getProjectDetails(String projectId);

    Project createProject(Project project);

    Project updateProject(String projectId, Project project);

    void deleteProject(String projectId);
}
