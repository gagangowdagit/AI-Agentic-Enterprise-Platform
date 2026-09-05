package com.rag.ragbackend.agent;

import com.rag.ragbackend.entity.DocumentChunk;
import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.entity.Task;
import com.rag.ragbackend.memory.Memory;
import com.rag.ragbackend.memory.MemoryService;
import com.rag.ragbackend.processing.LlmResponse;
import com.rag.ragbackend.processing.LlmService;
import com.rag.ragbackend.processing.RagService;
import com.rag.ragbackend.repository.ProjectRepository;
import com.rag.ragbackend.service.ProjectService;
import com.rag.ragbackend.service.TaskService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AgentServiceTest {

    @Test
    void decidesDirectAnswerForGeneralQuestion() {
        LlmService llmService = mock(LlmService.class);
        RagService ragService = mock(RagService.class);
        AgentPlanner planner = new AgentPlanner();
        AgentService agentService = new AgentService(llmService, ragService, planner, new ToolRegistry());
        LlmResponse expected = new LlmResponse("What is the capital of France?", "Paris", List.of());
        when(llmService.generateAnswer("What is the capital of France?", List.of())).thenReturn(expected);

        LlmResponse actual = agentService.execute("What is the capital of France?");

        assertEquals(expected, actual);
        verify(llmService).generateAnswer("What is the capital of France?", List.of());
    }

    @Test
    void decidesUseRagForProjectSpecificQuestion() {
        LlmService llmService = mock(LlmService.class);
        RagService ragService = mock(RagService.class);
        AgentPlanner planner = new AgentPlanner();
        AgentService agentService = new AgentService(llmService, ragService, planner, new ToolRegistry());
        String projectId = "project-123";
        String request = "Summarize the project contract details";
        DocumentChunk chunk = new DocumentChunk();
        List<DocumentChunk> relevantChunks = List.of(chunk);
        LlmResponse expected = new LlmResponse(request, "Summary based on project docs", relevantChunks);

        when(ragService.retrieveRelevantChunks(projectId, request, 5)).thenReturn(relevantChunks);
        when(llmService.generateAnswer(request, relevantChunks)).thenReturn(expected);

        LlmResponse actual = agentService.execute(request, projectId, 5);

        assertEquals(expected, actual);
        verify(ragService).retrieveRelevantChunks(projectId, request, 5);
        verify(llmService).generateAnswer(request, relevantChunks);
    }

    @Test
    void toolRegistryRegistersAndReturnsTool() {
        ToolRegistry toolRegistry = new ToolRegistry();
        ProjectService projectService = mock(ProjectService.class);
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        ProjectInfoTool projectInfoTool = new ProjectInfoTool(projectService, projectRepository);

        toolRegistry.register(projectInfoTool);

        assertEquals(projectInfoTool.getName(), toolRegistry.getTool("project-info").orElseThrow().getName());
    }

    @Test
    void projectInfoToolReturnsProjectDetails() {
        ProjectService projectService = mock(ProjectService.class);
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        ProjectInfoTool projectInfoTool = new ProjectInfoTool(projectService, projectRepository);
        Project project = new Project("project-123", "Alpha", "active");
        when(projectRepository.findById("project-123")).thenReturn(Optional.of(project));

        Object result = projectInfoTool.execute(Map.of("projectId", "project-123"));

        assertEquals(project, result);
    }

    @Test
    void ragSearchToolUsesExistingRagService() {
        RagService ragService = mock(RagService.class);
        RagSearchTool ragSearchTool = new RagSearchTool(ragService);
        DocumentChunk chunk = new DocumentChunk();
        List<DocumentChunk> expected = List.of(chunk);
        when(ragService.retrieveRelevantChunks("project-123", "find project knowledge", 5)).thenReturn(expected);

        Object result = ragSearchTool.execute(Map.of("projectId", "project-123", "query", "find project knowledge", "topK", 5));

        assertEquals(expected, result);
    }

    @Test
    void agentExecutesDirectAnswerPathThroughOllama() {
        LlmService llmService = mock(LlmService.class);
        RagService ragService = mock(RagService.class);
        AgentPlanner planner = new AgentPlanner();
        ToolRegistry toolRegistry = new ToolRegistry();
        AgentService agentService = new AgentService(llmService, ragService, planner, toolRegistry);
        LlmResponse expected = new LlmResponse("What is 2 + 2?", "4", List.of());
        when(llmService.generateAnswer("What is 2 + 2?", List.of())).thenReturn(expected);

        LlmResponse actual = agentService.execute("What is 2 + 2?");

        assertEquals(expected, actual);
    }

    @Test
    void agentExecutesRagPathThroughExistingServices() {
        LlmService llmService = mock(LlmService.class);
        RagService ragService = mock(RagService.class);
        AgentPlanner planner = new AgentPlanner();
        ToolRegistry toolRegistry = new ToolRegistry();
        AgentService agentService = new AgentService(llmService, ragService, planner, toolRegistry);
        String request = "Summarize the project contract details";
        List<DocumentChunk> relevantChunks = List.of(new DocumentChunk());
        LlmResponse expected = new LlmResponse(request, "Summary", relevantChunks);
        when(ragService.retrieveRelevantChunks("project-123", request, 5)).thenReturn(relevantChunks);
        when(llmService.generateAnswer(request, relevantChunks)).thenReturn(expected);

        LlmResponse actual = agentService.execute(request, "project-123", 5);

        assertEquals(expected, actual);
    }

    @Test
    void agentExecutesToolPathWhenThePlannerChoosesIt() {
        LlmService llmService = mock(LlmService.class);
        RagService ragService = mock(RagService.class);
        ToolRegistry toolRegistry = new ToolRegistry();
        ProjectService projectService = mock(ProjectService.class);
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        Project project = new Project("project-123", "Alpha", "active");
        when(projectRepository.findById("project-123")).thenReturn(Optional.of(project));
        ProjectInfoTool projectInfoTool = new ProjectInfoTool(projectService, projectRepository);
        toolRegistry.register(projectInfoTool);
        AgentService agentService = new AgentService(llmService, ragService, new AgentPlanner(), toolRegistry);
        when(llmService.generateAnswer("show project details for project-123", List.of(), project))
                .thenReturn(new LlmResponse("show project details for project-123", "Project Alpha is active", List.of()));

        LlmResponse actual = agentService.execute("show project details for project-123", "project-123", 5, Map.of("projectId", "project-123"));

        assertEquals("Project Alpha is active", actual.answer());
        verify(llmService).generateAnswer("show project details for project-123", List.of(), project);
    }

    @Test
    void agentSelectsTaskToolAndPassesResultToLlm() {
        LlmService llmService = mock(LlmService.class);
        RagService ragService = mock(RagService.class);
        TaskService taskService = mock(TaskService.class);
        ToolRegistry toolRegistry = new ToolRegistry();
        toolRegistry.register(new CreateTaskTool(taskService));
        AgentService agentService = new AgentService(llmService, ragService, new AgentPlanner(), toolRegistry);
        Task task = new Task("task-1", "project-123", "Prepare report", null, "todo", "high");
        when(taskService.createTask(any(Task.class))).thenReturn(task);
        String request = "create task for project-123";
        LlmResponse expected = new LlmResponse(request, "Task task-1 created", List.of());
        when(llmService.generateAnswer(request, List.of(), task)).thenReturn(expected);

        LlmResponse actual = agentService.execute(request, "project-123", 5, Map.of(
                "id", "task-1",
                "projectId", "project-123",
                "title", "Prepare report"));

        assertEquals(expected, actual);
        verify(taskService).createTask(any(Task.class));
        verify(llmService).generateAnswer(request, List.of(), task);
    }

    @Test
    void agentInvokesProjectInfoToolWhenRequestMatches() {
        LlmService llmService = mock(LlmService.class);
        RagService ragService = mock(RagService.class);
        ToolRegistry toolRegistry = new ToolRegistry();
        ProjectService projectService = mock(ProjectService.class);
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        ProjectInfoTool projectInfoTool = new ProjectInfoTool(projectService, projectRepository);
        Project project = new Project("project-123", "Alpha", "active");
        when(projectRepository.findById("project-123")).thenReturn(Optional.of(project));
        toolRegistry.register(projectInfoTool);
        AgentService agentService = new AgentService(llmService, ragService, new AgentPlanner(), toolRegistry);

        Object result = agentService.executeTool("show project details for project-123", Map.of("projectId", "project-123"));

        assertEquals(project, result);
    }

    @Test
    void rejectsBlankUserRequest() {
        AgentService agentService = new AgentService(mock(LlmService.class), mock(RagService.class), new AgentPlanner(), new ToolRegistry());

        assertThrows(IllegalArgumentException.class, () -> agentService.execute("  "));
    }

    @Test
    void toolExecutorValidatesToolNameAndRequiredArguments() {
        ToolRegistry toolRegistry = new ToolRegistry();
        ProjectService projectService = mock(ProjectService.class);
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        Project project = new Project("project-123", "Alpha", "active");
        when(projectRepository.findById("project-123")).thenReturn(Optional.of(project));
        toolRegistry.register(new ProjectInfoTool(projectService, projectRepository));

        ToolExecutor toolExecutor = new ToolExecutor(toolRegistry);

        assertEquals(project, toolExecutor.execute("project-info", Map.of("projectId", "project-123")));
        assertThrows(IllegalArgumentException.class, () -> toolExecutor.execute("missing-tool", Map.of()));
        assertThrows(IllegalArgumentException.class, () -> toolExecutor.execute("project-info", Map.of()));
    }

    @Test
    void retrievesProjectMemoriesAndPassesThemToLlm() {
        LlmService llmService = mock(LlmService.class);
        RagService ragService = mock(RagService.class);
        MemoryService memoryService = mock(MemoryService.class);
        AgentService agentService = new AgentService(llmService, ragService, new AgentPlanner(), new ToolRegistry(), memoryService);
        String request = "Summarize the project contract details";
        Memory memory = new Memory("memory-1", "project-123", "The contract renews annually.", "FACT", null);
        List<Memory> memories = List.of(memory);
        LlmResponse expected = new LlmResponse(request, "Annual renewal", List.of());
        when(memoryService.getRelevantMemories("project-123", request)).thenReturn(memories);
        when(llmService.generateAnswer(request, List.of(), null, memories)).thenReturn(expected);

        LlmResponse actual = agentService.execute(request, "project-123", 5);

        assertEquals(expected, actual);
        verify(memoryService).getRelevantMemories("project-123", request);
        verify(llmService).generateAnswer(request, List.of(), null, memories);
    }

    @Test
    void storesCompletedInteractionAsProjectMemory() {
        LlmService llmService = mock(LlmService.class);
        RagService ragService = mock(RagService.class);
        MemoryService memoryService = mock(MemoryService.class);
        AgentService agentService = new AgentService(llmService, ragService, new AgentPlanner(), new ToolRegistry(), memoryService);
        String request = "What is 2 + 2?";
        LlmResponse expected = new LlmResponse(request, "The project is active.", List.of());
        when(memoryService.getRelevantMemories("project-123", request)).thenReturn(List.of());
        when(llmService.generateAnswer(request, List.of())).thenReturn(expected);
        when(memoryService.saveMemory(any(Memory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LlmResponse actual = agentService.executeAndStore(request, "project-123", 5, Map.of(), "INTERACTION");

        assertEquals(expected, actual);
        verify(memoryService).saveMemory(org.mockito.ArgumentMatchers.argThat(memory ->
                "project-123".equals(memory.getProjectId())
                        && "INTERACTION".equals(memory.getMemoryType())
                        && memory.getContent().contains(request)
                        && memory.getContent().contains(expected.answer())));
    }
}