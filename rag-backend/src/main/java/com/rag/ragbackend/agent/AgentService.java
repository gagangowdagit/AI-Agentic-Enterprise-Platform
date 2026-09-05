package com.rag.ragbackend.agent;

import com.rag.ragbackend.entity.DocumentChunk;
import com.rag.ragbackend.execution.AgentExecution;
import com.rag.ragbackend.execution.AgentExecutionService;
import com.rag.ragbackend.memory.Memory;
import com.rag.ragbackend.memory.MemoryService;
import com.rag.ragbackend.processing.LlmResponse;
import com.rag.ragbackend.processing.LlmService;
import com.rag.ragbackend.processing.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class AgentService implements Agent {

    private final LlmService llmService;
    private final RagService ragService;
    private final AgentPlanner agentPlanner;
    private final ToolRegistry toolRegistry;
    private final ToolExecutor toolExecutor;
    private final MemoryService memoryService;
    private final AgentExecutionService executionService;

    @Autowired
    public AgentService(
            LlmService llmService,
            RagService ragService,
            AgentPlanner agentPlanner,
            ToolRegistry toolRegistry,
            MemoryService memoryService,
            AgentExecutionService executionService) {
        this.llmService = llmService;
        this.ragService = ragService;
        this.agentPlanner = agentPlanner;
        this.toolRegistry = toolRegistry == null ? new ToolRegistry() : toolRegistry;
        this.toolExecutor = new ToolExecutor(this.toolRegistry);
        this.memoryService = memoryService;
        this.executionService = executionService;
    }

    public AgentService(
            LlmService llmService,
            RagService ragService,
            AgentPlanner agentPlanner,
            ToolRegistry toolRegistry,
            MemoryService memoryService) {
        this(llmService, ragService, agentPlanner, toolRegistry, memoryService, null);
    }

    public AgentService(LlmService llmService, RagService ragService, AgentPlanner agentPlanner, ToolRegistry toolRegistry) {
        this(llmService, ragService, agentPlanner, toolRegistry, null, null);
    }

    @Override
    public String getName() {
        return "general-agent";
    }

    @Override
    public String getCapabilityDescription() {
        return "Answers general questions, searches project knowledge, and executes registered project tools.";
    }

    @Override
    public LlmResponse execute(String userRequest) {
        validateRequest(userRequest);
        return runTracked(null, execution -> executeWithDecision(userRequest, null, 5, execution));
    }

    public LlmResponse execute(String userRequest, String projectId, int topK) {
        validateRequest(userRequest);
        return runTracked(projectId, execution -> executeRequest(
                userRequest,
                projectId,
                topK,
                Map.of(),
                retrieveMemories(projectId, userRequest),
            execution));
    }

    public LlmResponse execute(String userRequest, String projectId, int topK, Map<String, Object> toolArguments) {
        validateRequest(userRequest);
        return runTracked(projectId, execution -> executeRequest(
            userRequest,
            projectId,
            topK,
            toolArguments,
            retrieveMemories(projectId, userRequest),
            execution));
    }

    public LlmResponse executeAndStore(
            String userRequest,
            String projectId,
            int topK,
            Map<String, Object> toolArguments,
            String memoryType) {
        LlmResponse response = execute(userRequest, projectId, topK, toolArguments);
        storeInteraction(projectId, userRequest, response.answer(), memoryType);
        return response;
    }

    public Memory storeInteraction(String projectId, String userRequest, String result, String memoryType) {
        validateRequest(userRequest);
        if (memoryService == null) {
            throw new IllegalStateException("MemoryService is required to store agent interactions.");
        }
        return memoryService.saveMemory(new Memory(
                projectId,
                "Request: " + userRequest + "\nResult: " + result,
                memoryType));
    }

    public Object executeTool(String userRequest, Map<String, Object> arguments) {
        validateRequest(userRequest);
        Optional<Tool> matchingTool = findToolForRequest(userRequest);
        if (matchingTool.isEmpty()) {
            throw new IllegalArgumentException("No tool available for request: " + userRequest);
        }
        return toolExecutor.execute(matchingTool.get().getName(), arguments == null ? Map.of() : arguments);
    }

    private LlmResponse executeRequest(
            String userRequest,
            String projectId,
            int topK,
            Map<String, Object> toolArguments,
            List<Memory> memories) {
        return executeRequest(userRequest, projectId, topK, toolArguments, memories, null);
    }

    private LlmResponse executeRequest(
            String userRequest,
            String projectId,
            int topK,
            Map<String, Object> toolArguments,
            List<Memory> memories,
            AgentExecution execution) {
        updateStep(execution, "PLANNING");
        AgentDecision decision = agentPlanner.decide(userRequest);

        if (decision == AgentDecision.USE_TOOL) {
            updateStep(execution, "EXECUTING_TOOL");
            Object toolResult = executeTool(userRequest, toolArguments == null ? Map.of() : toolArguments);
            updateStep(execution, "GENERATING_RESPONSE");
            return generateAnswer(userRequest, List.of(), toolResult, memories);
        }

        if (decision == AgentDecision.USE_RAG) {
            if (projectId == null || projectId.isBlank()) {
                throw new IllegalArgumentException("Project ID is required when using RAG.");
            }
            updateStep(execution, "RETRIEVING_CONTEXT");
            List<DocumentChunk> relevantChunks = ragService.retrieveRelevantChunks(projectId, userRequest, topK);
            updateStep(execution, "GENERATING_RESPONSE");
            return generateAnswer(userRequest, relevantChunks, null, memories);
        }

        updateStep(execution, "GENERATING_RESPONSE");
        return generateAnswer(userRequest, List.of(), null, memories);
    }

    private Optional<Tool> findToolForRequest(String userRequest) {
        String normalized = userRequest.toLowerCase();
        if (normalized.contains("create task")) {
            return toolRegistry.getTool("CREATE_TASK");
        }
        if (normalized.contains("update task")) {
            return toolRegistry.getTool("UPDATE_TASK");
        }
        if (normalized.contains("list project tasks") || normalized.contains("get project tasks")) {
            return toolRegistry.getTool("GET_PROJECT_TASKS");
        }
        if (normalized.contains("project") || normalized.contains("details") || normalized.contains("status")) {
            return toolRegistry.getTool("project-info");
        }
        if (normalized.contains("search") || normalized.contains("document") || normalized.contains("knowledge") || normalized.contains("summary")) {
            return toolRegistry.getTool("rag-search");
        }
        return Optional.empty();
    }

    private LlmResponse executeWithDecision(
            String userRequest,
            String projectId,
            int topK,
            AgentExecution execution) {
        return executeRequest(userRequest, projectId, topK, Map.of(), List.of(), execution);
    }

    private LlmResponse runTracked(String projectId, Function<AgentExecution, LlmResponse> executionAction) {
        if (executionService == null) {
            return executionAction.apply(null);
        }

        AgentExecution execution = executionService.start(projectId, "STARTING");
        try {
            LlmResponse response = executionAction.apply(execution);
            executionService.complete(execution.getExecutionId(), "COMPLETED");
            return response;
        } catch (RuntimeException exception) {
            executionService.fail(execution.getExecutionId(), exception.getMessage());
            throw exception;
        }
    }

    private void updateStep(AgentExecution execution, String step) {
        if (execution != null && executionService != null) {
            executionService.updateStep(execution.getExecutionId(), step);
        }
    }

    private List<Memory> retrieveMemories(String projectId, String userRequest) {
        if (memoryService == null || projectId == null || projectId.isBlank()) {
            return List.of();
        }
        return memoryService.getRelevantMemories(projectId, userRequest);
    }

    private LlmResponse generateAnswer(
            String userRequest,
            List<DocumentChunk> documentContext,
            Object toolResult,
            List<Memory> memories) {
        if (!memories.isEmpty()) {
            return llmService.generateAnswer(userRequest, documentContext, toolResult, memories);
        }
        if (toolResult != null) {
            return llmService.generateAnswer(userRequest, documentContext, toolResult);
        }
        return llmService.generateAnswer(userRequest, documentContext);
    }

    private void validateRequest(String userRequest) {
        if (userRequest == null || userRequest.isBlank()) {
            throw new IllegalArgumentException("Agent request is required.");
        }
    }
}