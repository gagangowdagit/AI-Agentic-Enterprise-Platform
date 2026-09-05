package com.rag.ragbackend.agent;

import com.rag.ragbackend.execution.AgentExecution;
import com.rag.ragbackend.execution.AgentExecutionService;
import com.rag.ragbackend.processing.LlmResponse;
import com.rag.ragbackend.processing.LlmService;
import com.rag.ragbackend.processing.RagService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AgentExecutionTrackingTest {

    @Test
    void recordsRunningStepsAndCompletion() {
        LlmService llmService = mock(LlmService.class);
        AgentExecutionService executionService = mock(AgentExecutionService.class);
        AgentExecution execution = new AgentExecution(null, "STARTING");
        when(executionService.start(null, "STARTING")).thenReturn(execution);
        when(llmService.generateAnswer("What is 2 + 2?", List.of()))
                .thenReturn(new LlmResponse("What is 2 + 2?", "4", List.of()));
        AgentService agentService = new AgentService(
                llmService, mock(RagService.class), new AgentPlanner(), new ToolRegistry(), null, executionService);

        LlmResponse response = agentService.execute("What is 2 + 2?");

        assertEquals("4", response.answer());
        verify(executionService).start(null, "STARTING");
        verify(executionService).updateStep(execution.getExecutionId(), "PLANNING");
        verify(executionService).updateStep(execution.getExecutionId(), "GENERATING_RESPONSE");
        verify(executionService).complete(execution.getExecutionId(), "COMPLETED");
    }

    @Test
    void marksExecutionFailedWhenAgentExecutionThrows() {
        LlmService llmService = mock(LlmService.class);
        AgentExecutionService executionService = mock(AgentExecutionService.class);
        AgentExecution execution = new AgentExecution("project-1", "STARTING");
        when(executionService.start("project-1", "STARTING")).thenReturn(execution);
        when(llmService.generateAnswer(any(String.class), any(List.class)))
                .thenThrow(new IllegalStateException("LLM unavailable"));
        AgentService agentService = new AgentService(
                llmService, mock(RagService.class), new AgentPlanner(), new ToolRegistry(), null, executionService);

        assertThrows(IllegalStateException.class, () -> agentService.execute("What is 2 + 2?", "project-1", 5));

        verify(executionService).fail(execution.getExecutionId(), "LLM unavailable");
    }
}