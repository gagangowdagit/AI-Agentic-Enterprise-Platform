package com.rag.ragbackend.agent;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ToolRegistry {

    private final Map<String, Tool> tools = new LinkedHashMap<>();

    public void register(Tool tool) {
        if (tool == null) {
            throw new IllegalArgumentException("Tool is required.");
        }
        if (tool.getName() == null || tool.getName().isBlank()) {
            throw new IllegalArgumentException("Tool name is required.");
        }
        tools.put(tool.getName(), tool);
    }

    public Optional<Tool> getTool(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(tools.get(name));
    }

    public Map<String, Tool> getAll() {
        return Map.copyOf(tools);
    }
}
