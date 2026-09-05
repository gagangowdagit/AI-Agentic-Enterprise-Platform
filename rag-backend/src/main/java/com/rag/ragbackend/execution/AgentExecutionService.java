package com.rag.ragbackend.execution;

import java.util.function.Consumer;

public interface AgentExecutionService {

    AgentExecution start(String projectId, String initialStep);

    AgentExecution updateStep(String executionId, String step);

    AgentExecution complete(String executionId, String message);

    AgentExecution fail(String executionId, String message);

    AgentExecution get(String executionId);

    Runnable subscribe(String executionId, Consumer<AgentExecution> listener);
}