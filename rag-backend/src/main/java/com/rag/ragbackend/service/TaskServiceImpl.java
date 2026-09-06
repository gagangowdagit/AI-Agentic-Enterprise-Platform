package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.ProjectTaskSummary;
import com.rag.ragbackend.entity.Task;
import com.rag.ragbackend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this(taskRepository, null);
    }

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Task createTask(Task task) {
        Task savedTask = taskRepository.save(task);
        if (savedTask.getAssignedUserId() != null) {
            notifySafely(savedTask.getAssignedUserId(), "TASK_ASSIGNED", "Task assigned",
                    "Task '" + savedTask.getTitle() + "' was assigned to you.");
        }
        return savedTask;
    }

    @Override
    public Task updateTask(String taskId, Task task) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        Long previousAssignee = existingTask.getAssignedUserId();
        String previousStatus = existingTask.getStatus();
        if (task.getProjectId() != null) {
            existingTask.setProjectId(task.getProjectId());
        }
        if (task.getAssignedUserId() != null) {
            existingTask.setAssignedUserId(task.getAssignedUserId());
        }
        if (task.getTitle() != null) {
            existingTask.setTitle(task.getTitle());
        }
        if (task.getDescription() != null) {
            existingTask.setDescription(task.getDescription());
        }
        if (task.getStatus() != null) {
            existingTask.setStatus(task.getStatus());
        }
        if (task.getPriority() != null) {
            existingTask.setPriority(task.getPriority());
        }
        Task savedTask = taskRepository.save(existingTask);
        if (savedTask.getAssignedUserId() != null
            && !savedTask.getAssignedUserId().equals(previousAssignee)) {
            notifySafely(savedTask.getAssignedUserId(), "TASK_ASSIGNED", "Task assigned",
                "Task '" + savedTask.getTitle() + "' was assigned to you.");
        }
        if (savedTask.getAssignedUserId() != null
            && task.getStatus() != null
            && !java.util.Objects.equals(previousStatus, savedTask.getStatus())) {
            notifySafely(savedTask.getAssignedUserId(), "TASK_STATUS_CHANGED", "Task status changed",
                "Task '" + savedTask.getTitle() + "' is now " + savedTask.getStatus() + ".");
        }
        return savedTask;
    }

    @Override
    public List<Task> getTasksByProjectId(String projectId) {
        return taskRepository.findByProjectId(Integer.valueOf(projectId));
    }

    @Override
    public ProjectTaskSummary getTaskSummaryByProjectId(String projectId) {
        List<Task> tasks = getTasksByProjectId(projectId);
        int completedTasks = (int) tasks.stream().filter(this::isCompleted).count();
        int inProgressTasks = (int) tasks.stream().filter(this::isInProgress).count();
        int pendingTasks = tasks.size() - completedTasks - inProgressTasks;
        double completionPercentage = tasks.isEmpty() ? 0.0 : completedTasks * 100.0 / tasks.size();

        return new ProjectTaskSummary(
                tasks.size(),
                completedTasks,
                inProgressTasks,
                pendingTasks,
                List.of(),
                completionPercentage,
                false);
    }

    private boolean isCompleted(Task task) {
        String status = normalizeStatus(task.getStatus());
        return status.equals("completed") || status.equals("done") || status.equals("closed");
    }

    private boolean isInProgress(Task task) {
        String status = normalizeStatus(task.getStatus());
        return status.equals("in_progress") || status.equals("in-progress")
                || status.equals("in progress") || status.equals("working") || status.equals("active");
    }

    private String normalizeStatus(String status) {
        return status == null ? "" : status.toLowerCase(Locale.ROOT).trim();
    }

    private void notifySafely(Long userId, String type, String title, String message) {
        if (notificationService == null) {
            return;
        }
        try {
            notificationService.createNotification(userId, type, title, message);
        } catch (RuntimeException ignored) {
            // Notifications must not make the task operation fail.
        }
    }
}