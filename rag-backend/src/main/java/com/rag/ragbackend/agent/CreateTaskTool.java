package com.rag.ragbackend.agent;

import com.rag.ragbackend.entity.Task;
import com.rag.ragbackend.service.TaskService;

import java.util.List;
import java.util.Map;

public class CreateTaskTool implements Tool {

    private final TaskService taskService;

    public CreateTaskTool(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public String getName() {
        return "CREATE_TASK";
    }

    @Override
    public String getDescription() {
        return "Creates a task in a project.";
    }

    @Override
    public List<ToolArgument> getInputDefinition() {
        return List.of(
                new Argument("id", "The task identifier.", true),
                new Argument("projectId", "The project identifier for the task.", true),
                new Argument("assignedUserId", "The user identifier assigned to the task.", false),
                new Argument("title", "The task title.", true),
                new Argument("description", "The task description.", false),
                new Argument("status", "The initial task status.", false),
                new Argument("priority", "The task priority.", false)
        );
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        Map<String, Object> input = arguments == null ? Map.of() : arguments;
        return taskService.createTask(new Task(
                requiredString(input, "id"),
                requiredString(input, "projectId"),
                optionalLong(input, "assignedUserId"),
                requiredString(input, "title"),
                optionalString(input, "description"),
                optionalString(input, "status"),
                optionalString(input, "priority")
        ));
    }

    private static String requiredString(Map<String, Object> input, String name) {
        String value = optionalString(input, name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Argument '" + name + "' is required for CREATE_TASK.");
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