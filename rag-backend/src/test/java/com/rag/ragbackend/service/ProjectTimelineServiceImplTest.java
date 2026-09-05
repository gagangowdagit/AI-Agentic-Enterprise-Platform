package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.ProjectTimeline;
import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.entity.ProjectPriority;
import com.rag.ragbackend.entity.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProjectTimelineServiceImplTest {

    @Test
    void buildsTimelineFromProjectAndProjectTasks() {
        ProjectService projectService = mock(ProjectService.class);
        TaskService taskService = mock(TaskService.class);
        LocalDate endDate = LocalDate.now().plusDays(12);
        Project project = new Project(7L, "Alpha", "Scope", "active",
                LocalDate.now().minusDays(4), endDate, ProjectPriority.HIGH);
        when(projectService.getProject("7")).thenReturn(project);
        when(taskService.getTasksByProjectId("7")).thenReturn(List.of(
                new Task("task-1", "7", "Prepare release", null, "in_progress", "high")));
        ProjectTimelineService service = new ProjectTimelineServiceImpl(projectService, taskService);

        ProjectTimeline timeline = service.getTimeline("7");

        assertEquals("7", timeline.projectId());
        assertEquals(project.getStartDate(), timeline.startDate());
        assertEquals(endDate, timeline.endDate());
        assertEquals("active", timeline.status());
        assertEquals(12L, timeline.daysRemaining());
        assertEquals(1, timeline.tasks().size());
        assertEquals("task-1", timeline.tasks().get(0).id());
        assertEquals("Prepare release", timeline.tasks().get(0).title());
        assertEquals("in_progress", timeline.tasks().get(0).status());
        assertNull(timeline.tasks().get(0).startDate());
        assertNull(timeline.tasks().get(0).endDate());
        verify(projectService).getProject("7");
        verify(taskService).getTasksByProjectId("7");
    }

    @Test
    void leavesDaysRemainingUnsetWithoutProjectEndDate() {
        ProjectService projectService = mock(ProjectService.class);
        TaskService taskService = mock(TaskService.class);
        when(projectService.getProject("7")).thenReturn(new Project(7L, "Alpha", "active"));
        when(taskService.getTasksByProjectId("7")).thenReturn(List.of());
        ProjectTimelineService service = new ProjectTimelineServiceImpl(projectService, taskService);

        assertNull(service.getTimeline("7").daysRemaining());
    }
}