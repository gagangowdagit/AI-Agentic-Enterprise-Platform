package com.rag.ragbackend.agent;

import com.rag.ragbackend.service.TaskService;

import java.util.List;
import java.util.Map;

public class GetProjectTasksTool implements Tool {

    private final TaskService taskService;

    public GetProjectTasksTool(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public String getName() {
        return "GET_PROJECT_TASKS";
    }

    @Override
    public String getDescription() {
        return "Lists all tasks belonging to a project.";
    }

    @Override
    public List<ToolArgument> getInputDefinition() {
        return List.of(new Argument("projectId", "The project identifier to list tasks for.", true));
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        Map<String, Object> input = arguments == null ? Map.of() : arguments;
        String projectId = input.get("projectId") == null ? null : String.valueOf(input.get("projectId")).trim();
        if (projectId == null || projectId.isBlank()) {
            throw new IllegalArgumentException("Argument 'projectId' is required for GET_PROJECT_TASKS.");
        }
        return taskService.getTasksByProjectId(projectId);
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