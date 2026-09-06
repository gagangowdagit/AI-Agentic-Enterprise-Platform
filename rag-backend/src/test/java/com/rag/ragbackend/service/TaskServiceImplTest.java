package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.ProjectTaskSummary;
import com.rag.ragbackend.entity.Task;
import com.rag.ragbackend.repository.TaskRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
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
        Task task = new Task("task-1", "1", 7L, "Prepare report", null, "todo", "high");
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
        Task existingTask = new Task("task-1", "1", "Prepare report", null, "todo", "high");
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
        Task task = new Task("task-1", "1", 7L, "Prepare report", null, "todo", "high");
        when(taskRepository.save(task)).thenReturn(task);
        when(notificationService.createNotification(any(Long.class), any(String.class), any(String.class), any(String.class)))
                .thenThrow(new IllegalStateException("notification unavailable"));
        TaskService service = new TaskServiceImpl(taskRepository, notificationService);

        assertEquals(task, service.createTask(task));
    }

    @Test
    void summarizesTasksByProjectAndClassifiesStatuses() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        List<Task> projectTasks = List.of(
                new Task("task-1", "1", "Complete release", null, "completed", "high"),
                new Task("task-2", "1", "Review release", null, "done", "medium"),
                new Task("task-3", "1", "Fix deployment", null, "in_progress", "high"),
                new Task("task-4", "1", "Write docs", null, "todo", "low"),
                new Task("task-5", "2", "Other project", null, "completed", "low"));
        when(taskRepository.findByProjectId(1)).thenReturn(projectTasks.subList(0, 4));
        TaskService service = new TaskServiceImpl(taskRepository);

        ProjectTaskSummary summary = service.getTaskSummaryByProjectId("1");

        assertEquals(4, summary.totalTasks());
        assertEquals(2, summary.completedTasks());
        assertEquals(1, summary.inProgressTasks());
        assertEquals(1, summary.pendingTasks());
        assertEquals(50.0, summary.completionPercentage());
        assertEquals(List.of(), summary.overdueTasks());
        assertEquals(false, summary.overdueTrackingAvailable());
        verify(taskRepository).findByProjectId(1);
    }
}