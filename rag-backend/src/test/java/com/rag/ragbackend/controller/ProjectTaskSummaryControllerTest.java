package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.ProjectTaskSummary;
import com.rag.ragbackend.entity.Task;
import com.rag.ragbackend.exception.GlobalExceptionHandler;
import com.rag.ragbackend.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectTaskSummaryControllerTest {

    @Test
    void returnsTaskSummaryForProject() throws Exception {
        TaskService taskService = mock(TaskService.class);
        ProjectTaskSummary summary = new ProjectTaskSummary(5, 2, 1, 2,
                List.of(new Task("task-5", "project-1", "Overdue task", null, "todo", "high")),
                40.0, true);
        when(taskService.getTaskSummaryByProjectId("project-1")).thenReturn(summary);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ProjectTaskSummaryController(taskService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/api/v1/projects/project-1/task-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalTasks").value(5))
                .andExpect(jsonPath("$.data.completedTasks").value(2))
                .andExpect(jsonPath("$.data.inProgressTasks").value(1))
                .andExpect(jsonPath("$.data.pendingTasks").value(2))
                .andExpect(jsonPath("$.data.overdueTasks[0].id").value("task-5"))
                .andExpect(jsonPath("$.data.completionPercentage").value(40.0))
                .andExpect(jsonPath("$.data.overdueTrackingAvailable").value(true));

        verify(taskService).getTaskSummaryByProjectId("project-1");
    }
}