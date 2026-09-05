package com.rag.ragbackend.agent;

import com.rag.ragbackend.processing.LlmResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AgentCoordinatorTest {

    @Test
    void registersAndSelectsAgentByName() {
        SpecializedAgent researchAgent = mockAgent("research-agent", "Searches research documents.");
        AgentCoordinator coordinator = new AgentCoordinator(List.of(researchAgent));

        assertEquals(researchAgent, coordinator.selectAgent("Ask the research-agent to investigate this"));
        assertEquals(List.of(researchAgent), coordinator.getAgents());
    }

    @Test
    void selectsAgentByCapabilityAndExecutesSynchronously() {
        SpecializedAgent taskAgent = mockAgent("task-agent", "Manages task assignments.");
        LlmResponse expected = new LlmResponse("create a task", "created", List.of());
        when(taskAgent.execute("create a task")).thenReturn(expected);
        AgentCoordinator coordinator = new AgentCoordinator(List.of(taskAgent));

        LlmResponse actual = coordinator.execute("create a task");

        assertEquals(expected, actual);
        verify(taskAgent).execute("create a task");
    }

    @Test
    void delegatesToAllMatchingAgentsSequentiallyWithContext() {
        SpecializedAgent researchAgent = mockAgent("research-agent", "research documents.");
        SpecializedAgent taskAgent = mockAgent("task-agent", "task assignments.");
        LlmResponse researchResult = new LlmResponse("research and create a task", "research result", List.of());
        LlmResponse taskResult = new LlmResponse("research and create a task", "task result", List.of());
        when(researchAgent.execute("research and create a task", "project-123 context")).thenReturn(researchResult);
        when(taskAgent.execute("research and create a task", "project-123 context")).thenReturn(taskResult);
        AgentCoordinator coordinator = new AgentCoordinator(List.of(researchAgent, taskAgent));

        List<LlmResponse> results = coordinator.delegate("research and create a task", "project-123 context");

        assertEquals(List.of(researchResult, taskResult), results);
        verify(researchAgent).execute("research and create a task", "project-123 context");
        verify(taskAgent).execute("research and create a task", "project-123 context");
    }

    @Test
    void combinesMultipleAgentResultsThroughLlmService() {
        SpecializedAgent researchAgent = mockAgent("research-agent", "research documents.");
        SpecializedAgent taskAgent = mockAgent("task-agent", "task assignments.");
        LlmResponse researchResult = new LlmResponse("research and create a task", "research result", List.of());
        LlmResponse taskResult = new LlmResponse("research and create a task", "task result", List.of());
        when(researchAgent.execute("research and create a task")).thenReturn(researchResult);
        when(taskAgent.execute("research and create a task")).thenReturn(taskResult);
        com.rag.ragbackend.processing.LlmService llmService = mock(com.rag.ragbackend.processing.LlmService.class);
        LlmResponse combinedResult = new LlmResponse("research and create a task", "combined result", List.of());
        when(llmService.generateAnswer("research and create a task", List.of(), List.of(researchResult, taskResult)))
                .thenReturn(combinedResult);
        AgentCoordinator coordinator = new AgentCoordinator(List.of(researchAgent, taskAgent), llmService);

        LlmResponse actual = coordinator.execute("research and create a task");

        assertEquals(combinedResult, actual);
        verify(llmService).generateAnswer("research and create a task", List.of(), List.of(researchResult, taskResult));
    }

    @Test
    void singleAgentDelegationStillReturnsOneResult() {
        SpecializedAgent taskAgent = mockAgent("task-agent", "Manages tasks.");
        LlmResponse expected = new LlmResponse("create a task", "created", List.of());
        when(taskAgent.execute("create a task", "project context")).thenReturn(expected);
        AgentCoordinator coordinator = new AgentCoordinator(List.of(taskAgent));

        assertEquals(List.of(expected), coordinator.delegate("create a task", "project context"));
        verify(taskAgent).execute("create a task", "project context");
    }

    @Test
    void rejectsRequestsWhenNoAgentMatches() {
        SpecializedAgent researchAgent = mockAgent("research-agent", "Searches research documents.");
        SpecializedAgent taskAgent = mockAgent("task-agent", "Manages task assignments.");
        AgentCoordinator coordinator = new AgentCoordinator(List.of(researchAgent, taskAgent));

        assertThrows(IllegalArgumentException.class, () -> coordinator.selectAgent("send a notification"));
    }

    @Test
    void existingAgentIsACompatibleSpecializedAgent() {
        AgentService agentService = new AgentService(
                mock(com.rag.ragbackend.processing.LlmService.class),
                mock(com.rag.ragbackend.processing.RagService.class),
                new AgentPlanner(),
                new ToolRegistry());

        assertInstanceOf(SpecializedAgent.class, agentService);
        assertEquals("general-agent", agentService.getName());
    }

    private SpecializedAgent mockAgent(String name, String capability) {
        SpecializedAgent agent = mock(SpecializedAgent.class);
        when(agent.getName()).thenReturn(name);
        when(agent.getCapabilityDescription()).thenReturn(capability);
        return agent;
    }
}