package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.ProjectTimeline;
import com.rag.ragbackend.dto.ProjectTimelineTask;
import com.rag.ragbackend.entity.Project;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ProjectTimelineServiceImpl implements ProjectTimelineService {

    private final ProjectService projectService;
    private final TaskService taskService;

    public ProjectTimelineServiceImpl(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @Override
    public ProjectTimeline getTimeline(String projectId) {
        Project project = projectService.getProject(projectId);
        Long daysRemaining = project.getEndDate() == null
                ? null
                : ChronoUnit.DAYS.between(LocalDate.now(), project.getEndDate());

        return new ProjectTimeline(
                projectId,
                project.getStartDate(),
                project.getEndDate(),
                project.getStatus(),
                daysRemaining,
                taskService.getTasksByProjectId(projectId).stream()
                        .map(task -> new ProjectTimelineTask(
                                task.getId(),
                                task.getTitle(),
                                task.getStatus(),
                                null,
                                null))
                        .toList());
    }
}