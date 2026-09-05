package com.rag.ragbackend.agent;

import com.rag.ragbackend.entity.Task;
import com.rag.ragbackend.service.TaskService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskToolsTest {

    @Test
    void createTaskToolDelegatesToTaskService() {
        TaskService taskService = mock(TaskService.class);
        Task expected = new Task("task-1", "project-1", "Prepare report", "Draft report", "todo", "high");
        when(taskService.createTask(any(Task.class))).thenReturn(expected);
        CreateTaskTool tool = new CreateTaskTool(taskService);

        Object result = tool.execute(Map.of(
                "id", "task-1",
                "projectId", "project-1",
                "title", "Prepare report",
                "description", "Draft report",
                "status", "todo",
                "priority", "high"));

        assertEquals(expected, result);
        verify(taskService).createTask(any(Task.class));
    }

    @Test
    void updateTaskToolDelegatesPatchToTaskService() {
        TaskService taskService = mock(TaskService.class);
        Task expected = new Task("task-1", null, null, null, "done", null);
        when(taskService.updateTask(eq("task-1"), any(Task.class))).thenReturn(expected);
        UpdateTaskTool tool = new UpdateTaskTool(taskService);

        Object result = tool.execute(Map.of("taskId", "task-1", "status", "done"));

        assertEquals(expected, result);
        verify(taskService).updateTask(eq("task-1"), any(Task.class));
    }

    @Test
    void getProjectTasksToolDelegatesToTaskService() {
        TaskService taskService = mock(TaskService.class);
        List<Task> expected = List.of(new Task("task-1", "project-1", "Prepare report", null, "todo", null));
        when(taskService.getTasksByProjectId("project-1")).thenReturn(expected);
        GetProjectTasksTool tool = new GetProjectTasksTool(taskService);

        assertEquals(expected, tool.execute(Map.of("projectId", "project-1")));
        verify(taskService).getTasksByProjectId("project-1");
    }

    @Test
    void toolExecutorRejectsUnknownToolAndInvalidArguments() {
        ToolRegistry registry = new ToolRegistry();
        registry.register(new CreateTaskTool(mock(TaskService.class)));
        registry.register(new UpdateTaskTool(mock(TaskService.class)));
        registry.register(new GetProjectTasksTool(mock(TaskService.class)));
        ToolExecutor executor = new ToolExecutor(registry);

        assertThrows(IllegalArgumentException.class, () -> executor.execute("missing", Map.of()));
        assertThrows(IllegalArgumentException.class, () -> executor.execute("CREATE_TASK", Map.of("projectId", "project-1")));
        assertThrows(IllegalArgumentException.class, () -> executor.execute("UPDATE_TASK", Map.of("taskId", "task-1")));
        assertThrows(IllegalArgumentException.class, () -> executor.execute("GET_PROJECT_TASKS", Map.of()));
    }
}