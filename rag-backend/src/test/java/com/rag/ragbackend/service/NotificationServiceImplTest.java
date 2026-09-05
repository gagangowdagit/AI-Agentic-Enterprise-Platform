package com.rag.ragbackend.service;

import com.rag.ragbackend.entity.Notification;
import com.rag.ragbackend.repository.NotificationRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceImplTest {

    @Test
    void createsUnreadNotificationWithTimestamp() {
        NotificationRepository repository = mock(NotificationRepository.class);
        when(repository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        NotificationService service = new NotificationServiceImpl(repository);

        Notification notification = service.createNotification(7L, "TASK", "Task completed", "The task is done.");

        assertEquals(7L, notification.getUserId());
        assertEquals("TASK", notification.getType());
        assertEquals("Task completed", notification.getTitle());
        assertEquals("The task is done.", notification.getMessage());
        assertFalse(notification.isRead());
        assertNotNull(notification.getCreatedAt());
        verify(repository).save(notification);
    }

    @Test
    void retrievesNotificationsForUser() {
        NotificationRepository repository = mock(NotificationRepository.class);
        List<Notification> expected = List.of(new Notification(7L, "INFO", "Title", "Message"));
        when(repository.findByUserIdOrderByCreatedAtDesc(7L)).thenReturn(expected);
        NotificationService service = new NotificationServiceImpl(repository);

        assertEquals(expected, service.getNotificationsByUserId(7L));
        verify(repository).findByUserIdOrderByCreatedAtDesc(7L);
    }

    @Test
    void marksNotificationAsReadAndPersistsIt() {
        NotificationRepository repository = mock(NotificationRepository.class);
        Notification notification = new Notification(7L, "INFO", "Title", "Message");
        when(repository.findById(11L)).thenReturn(Optional.of(notification));
        when(repository.save(notification)).thenReturn(notification);
        NotificationService service = new NotificationServiceImpl(repository);

        Notification result = service.markAsRead(11L);

        assertTrue(result.isRead());
        verify(repository).save(notification);
    }

    @Test
    void rejectsUnknownNotificationWhenMarkingAsRead() {
        NotificationRepository repository = mock(NotificationRepository.class);
        when(repository.findById(99L)).thenReturn(Optional.empty());
        NotificationService service = new NotificationServiceImpl(repository);

        assertThrows(IllegalArgumentException.class, () -> service.markAsRead(99L));
    }
}