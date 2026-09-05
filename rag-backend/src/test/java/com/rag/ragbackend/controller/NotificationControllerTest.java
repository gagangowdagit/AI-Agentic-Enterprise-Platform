package com.rag.ragbackend.controller;

import com.rag.ragbackend.entity.Notification;
import com.rag.ragbackend.exception.GlobalExceptionHandler;
import com.rag.ragbackend.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationControllerTest {

    @Test
    void retrievesNotificationsForUser() throws Exception {
        NotificationService service = mock(NotificationService.class);
        Notification notification = notification(1L, false);
        when(service.getNotificationsByUserId(7L)).thenReturn(List.of(notification));
        MockMvc mockMvc = mockMvc(service);

        mockMvc.perform(get("/api/v1/notifications/users/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value(7))
                .andExpect(jsonPath("$[0].read").value(false));

        verify(service).getNotificationsByUserId(7L);
    }

    @Test
    void retrievesUnreadNotificationsForUser() throws Exception {
        NotificationService service = mock(NotificationService.class);
        Notification notification = notification(2L, false);
        when(service.getUnreadNotificationsByUserId(7L)).thenReturn(List.of(notification));
        MockMvc mockMvc = mockMvc(service);

        mockMvc.perform(get("/api/v1/notifications/users/7/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].read").value(false));

        verify(service).getUnreadNotificationsByUserId(7L);
    }

    @Test
    void marksNotificationAsRead() throws Exception {
        NotificationService service = mock(NotificationService.class);
        Notification notification = notification(3L, true);
        when(service.markAsRead(3L)).thenReturn(notification);
        MockMvc mockMvc = mockMvc(service);

        mockMvc.perform(post("/api/v1/notifications/3/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.read").value(true));

        verify(service).markAsRead(3L);
    }

    private MockMvc mockMvc(NotificationService service) {
        return MockMvcBuilders.standaloneSetup(new NotificationController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Notification notification(Long id, boolean read) {
        Notification notification = new Notification(7L, "INFO", "Title", "Message");
        notification.setId(id);
        notification.setRead(read);
        return notification;
    }
}