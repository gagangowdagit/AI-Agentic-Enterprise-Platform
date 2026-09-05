package com.rag.ragbackend.agent;

import java.util.List;
import java.util.Map;

public interface Tool {

    String getName();

    String getDescription();

    List<ToolArgument> getInputDefinition();

    Object execute(Map<String, Object> arguments);

    interface ToolArgument {
        String getName();
        String getDescription();
        boolean isRequired();
    }
}
