package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.ProjectTimeline;
import com.rag.ragbackend.dto.ProjectTimelineTask;
import com.rag.ragbackend.exception.GlobalExceptionHandler;
import com.rag.ragbackend.service.ProjectTimelineService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectTimelineControllerTest {

    @Test
    void returnsProjectTimelineAndSupportedTaskFields() throws Exception {
        ProjectTimelineService timelineService = mock(ProjectTimelineService.class);
        ProjectTimeline timeline = new ProjectTimeline(
                "project-1",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30),
                "active",
                25L,
                List.of(new ProjectTimelineTask("task-1", "Prepare release", "in_progress", null, null)));
        when(timelineService.getTimeline("project-1")).thenReturn(timeline);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ProjectTimelineController(timelineService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/api/v1/projects/project-1/timeline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.projectId").value("project-1"))
                .andExpect(jsonPath("$.data.startDate").value("2026-09-01"))
                .andExpect(jsonPath("$.data.endDate").value("2026-09-30"))
                .andExpect(jsonPath("$.data.status").value("active"))
                .andExpect(jsonPath("$.data.daysRemaining").value(25))
                .andExpect(jsonPath("$.data.tasks[0].id").value("task-1"))
                .andExpect(jsonPath("$.data.tasks[0].title").value("Prepare release"))
                .andExpect(jsonPath("$.data.tasks[0].status").value("in_progress"))
                .andExpect(jsonPath("$.data.tasks[0].startDate").doesNotExist())
                .andExpect(jsonPath("$.data.tasks[0].endDate").doesNotExist());

        verify(timelineService).getTimeline("project-1");
    }
}