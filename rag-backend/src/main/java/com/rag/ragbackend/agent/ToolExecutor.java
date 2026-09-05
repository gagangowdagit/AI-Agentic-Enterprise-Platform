package com.rag.ragbackend.agent;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class ToolExecutor {

    private final ToolRegistry toolRegistry;

    public ToolExecutor(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry == null ? new ToolRegistry() : toolRegistry;
    }

    public Object execute(String toolName, Map<String, Object> arguments) {
        if (toolName == null || toolName.isBlank()) {
            throw new IllegalArgumentException("Tool name is required.");
        }

        Tool tool = toolRegistry.getTool(toolName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown tool: " + toolName));

        Map<String, Object> validatedArguments = arguments == null ? Map.of() : arguments;
        validateRequiredArguments(tool, validatedArguments);
        return tool.execute(validatedArguments);
    }

    private void validateRequiredArguments(Tool tool, Map<String, Object> arguments) {
        for (Tool.ToolArgument argument : tool.getInputDefinition()) {
            if (!argument.isRequired()) {
                continue;
            }

            Object value = arguments.get(argument.getName());
            if (value == null || (value instanceof String valueString && valueString.isBlank())) {
                throw new IllegalArgumentException("Missing required argument '" + argument.getName() + "' for tool '" + tool.getName() + "'.");
            }
        }
    }
}