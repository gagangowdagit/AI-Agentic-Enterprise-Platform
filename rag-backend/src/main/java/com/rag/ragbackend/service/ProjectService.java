package com.rag.ragbackend.service;

import com.rag.ragbackend.entity.Project;

import java.util.List;

public interface ProjectService {

    List<Project> getAllProjects();

    Project createProject(Project project);
}
