package com.rag.ragbackend.agent;

import com.rag.ragbackend.entity.Task;
import com.rag.ragbackend.service.TaskService;

import java.util.List;
import java.util.Map;

public class UpdateTaskTool implements Tool {

    private final TaskService taskService;

    public UpdateTaskTool(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public String getName() {
        return "UPDATE_TASK";
    }

    @Override
    public String getDescription() {
        return "Updates the supplied fields of an existing task.";
    }

    @Override
    public List<ToolArgument> getInputDefinition() {
        return List.of(
                new Argument("taskId", "The task identifier to update.", true),
                new Argument("projectId", "The replacement project identifier.", false),
                new Argument("assignedUserId", "The replacement assigned user identifier.", false),
                new Argument("title", "The replacement task title.", false),
                new Argument("description", "The replacement task description.", false),
                new Argument("status", "The replacement task status.", false),
                new Argument("priority", "The replacement task priority.", false)
        );
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        Map<String, Object> input = arguments == null ? Map.of() : arguments;
        String taskId = requiredString(input, "taskId");
        if (input.keySet().stream().noneMatch(key -> !key.equals("taskId"))) {
            throw new IllegalArgumentException("At least one task field is required for UPDATE_TASK.");
        }
        return taskService.updateTask(taskId, new Task(
                taskId,
                optionalString(input, "projectId"),
                optionalLong(input, "assignedUserId"),
                optionalString(input, "title"),
                optionalString(input, "description"),
                optionalString(input, "status"),
                optionalString(input, "priority")
        ));
    }

    private static String requiredString(Map<String, Object> input, String name) {
        String value = optionalString(input, name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Argument '" + name + "' is required for UPDATE_TASK.");
        }
        return value;
    }

    private static String optionalString(Map<String, Object> input, String name) {
        Object value = input.get(name);
        return value == null ? null : String.valueOf(value).trim();
    }

    private static Long optionalLong(Map<String, Object> input, String name) {
        Object value = input.get(name);
        return value == null ? null : Long.valueOf(String.valueOf(value));
    }

    private record Argument(String name, String description, boolean required) implements ToolArgument {
        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean isRequired() {
            return required;
        }
    }
}