package com.rag.ragbackend.service;

import com.rag.ragbackend.entity.Task;
import com.rag.ragbackend.repository.TaskRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskServiceImplTest {

    @Test
    void createsAssignmentNotificationForAssignedTask() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        NotificationService notificationService = mock(NotificationService.class);
        Task task = new Task("task-1", "project-1", 7L, "Prepare report", null, "todo", "high");
        when(taskRepository.save(task)).thenReturn(task);
        TaskService service = new TaskServiceImpl(taskRepository, notificationService);

        assertEquals(task, service.createTask(task));

        verify(notificationService).createNotification(
                7L, "TASK_ASSIGNED", "Task assigned", "Task 'Prepare report' was assigned to you.");
    }

    @Test
    void createsNotificationsForAssignmentAndStatusChanges() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        NotificationService notificationService = mock(NotificationService.class);
        Task existingTask = new Task("task-1", "project-1", "Prepare report", null, "todo", "high");
        Task update = new Task("task-1", null, 7L, null, null, "done", null);
        when(taskRepository.findById("task-1")).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);
        TaskService service = new TaskServiceImpl(taskRepository, notificationService);

        service.updateTask("task-1", update);

        verify(notificationService).createNotification(
                7L, "TASK_ASSIGNED", "Task assigned", "Task 'Prepare report' was assigned to you.");
        verify(notificationService).createNotification(
                7L, "TASK_STATUS_CHANGED", "Task status changed", "Task 'Prepare report' is now done.");
    }

    @Test
    void notificationFailureDoesNotFailTaskOperation() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        NotificationService notificationService = mock(NotificationService.class);
        Task task = new Task("task-1", "project-1", 7L, "Prepare report", null, "todo", "high");
        when(taskRepository.save(task)).thenReturn(task);
        when(notificationService.createNotification(any(Long.class), any(String.class), any(String.class), any(String.class)))
                .thenThrow(new IllegalStateException("notification unavailable"));
        TaskService service = new TaskServiceImpl(taskRepository, notificationService);

        assertEquals(task, service.createTask(task));
    }
}