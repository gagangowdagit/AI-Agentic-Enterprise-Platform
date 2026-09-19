package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.MeetingDto;
import com.rag.ragbackend.entity.MeetingStatus;
import com.rag.ragbackend.exception.GlobalExceptionHandler;
import com.rag.ragbackend.service.MeetingService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MeetingControllerTest {

    private final MeetingService meetingService = mock(MeetingService.class);
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new MeetingController(meetingService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void createsMeetingFromRequestPayload() throws Exception {
        MeetingDto created = new MeetingDto(
                1L,
                "Sprint Planning",
                "Planning sprint scope",
                "PRJ-1001",
                "AI Agentic Enterprise Platform",
                LocalDate.of(2026, 9, 30),
                "09:00",
                "10:00",
                6,
                MeetingStatus.Scheduled,
                "https://meet.google.com/abc-defg-hij"
        );

        when(meetingService.createMeeting(any())).thenReturn(created);

        mockMvc.perform(post("/api/v1/meetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Sprint Planning",
                                  "description": "Planning sprint scope",
                                  "projectId": "PRJ-1001",
                                  "projectName": "AI Agentic Enterprise Platform",
                                  "meetingDate": "2026-09-30",
                                  "startTime": "09:00",
                                  "endTime": "10:00",
                                  "googleMeetUrl": "https://meet.google.com/abc-defg-hij",
                                  "agenda": "Discuss scope and dependencies",
                                  "participantIds": [1, 2, 3]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Sprint Planning"))
                .andExpect(jsonPath("$.projectId").value("PRJ-1001"))
                .andExpect(jsonPath("$.status").value("Scheduled"));

        verify(meetingService).createMeeting(any());
    }

    @Test
    void getsMeetingDetailsById() throws Exception {
        MeetingDto meeting = new MeetingDto(
                7L,
                "Sprint Planning",
                "Planning sprint scope",
                "PRJ-1001",
                "AI Agentic Enterprise Platform",
                LocalDate.of(2026, 9, 30),
                "09:00",
                "10:00",
                2,
                MeetingStatus.Scheduled,
                "https://meet.google.com/abc-defg-hij",
                "Discuss scope and dependencies",
                java.util.List.of(1, 2),
                "admin",
                java.time.LocalDateTime.of(2026, 9, 20, 8, 30),
                java.time.LocalDateTime.of(2026, 9, 20, 8, 45)
        );

        when(meetingService.getMeetingById(7L)).thenReturn(meeting);

        mockMvc.perform(get("/api/v1/meetings/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.title").value("Sprint Planning"))
                .andExpect(jsonPath("$.projectName").value("AI Agentic Enterprise Platform"))
                .andExpect(jsonPath("$.status").value("Scheduled"));

        verify(meetingService).getMeetingById(7L);
    }
}
