package com.rag.ragbackend.execution;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AgentExecutionServiceTest {

    @Test
    void createsAndCompletesExecution() {
        AgentExecutionRepository repository = mock(AgentExecutionRepository.class);
        AgentExecutionService service = new AgentExecutionServiceImpl(repository);
        when(repository.save(any(AgentExecution.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AgentExecution execution = service.start("project-1", "STARTING");
        String runningStatus = execution.getStatus();
        when(repository.findById(execution.getExecutionId())).thenReturn(Optional.of(execution));
        service.updateStep(execution.getExecutionId(), "GENERATING_RESPONSE");
        AgentExecution completed = service.complete(execution.getExecutionId(), "COMPLETED");

        assertEquals("RUNNING", runningStatus);
        assertEquals("COMPLETED", completed.getStatus());
        assertEquals("COMPLETED", completed.getCurrentStep());
        assertNotNull(completed.getStartTime());
        assertNotNull(completed.getEndTime());
        verify(repository, times(3)).save(execution);
    }

    @Test
    void marksExecutionFailed() {
        AgentExecutionRepository repository = mock(AgentExecutionRepository.class);
        AgentExecutionService service = new AgentExecutionServiceImpl(repository);
        AgentExecution execution = new AgentExecution("project-1", "STARTING");
        when(repository.findById(execution.getExecutionId())).thenReturn(Optional.of(execution));
        when(repository.save(execution)).thenReturn(execution);

        AgentExecution failed = service.fail(execution.getExecutionId(), "LLM unavailable");

        assertEquals("FAILED", failed.getStatus());
        assertEquals("LLM unavailable", failed.getCurrentStep());
        assertNotNull(failed.getEndTime());
    }

    @Test
    void rejectsUnknownExecution() {
        AgentExecutionRepository repository = mock(AgentExecutionRepository.class);
        AgentExecutionService service = new AgentExecutionServiceImpl(repository);
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.updateStep("missing", "STEP"));
    }

    @Test
    void publishesStepUpdatesToSubscribers() {
        AgentExecutionRepository repository = mock(AgentExecutionRepository.class);
        AgentExecutionService service = new AgentExecutionServiceImpl(repository);
        when(repository.save(any(AgentExecution.class))).thenAnswer(invocation -> invocation.getArgument(0));
        AgentExecution execution = service.start("project-1", "STARTING");
        when(repository.findById(execution.getExecutionId())).thenReturn(Optional.of(execution));
        AtomicReference<AgentExecution> latest = new AtomicReference<>();

        Runnable cleanup = service.subscribe(execution.getExecutionId(), latest::set);
        service.updateStep(execution.getExecutionId(), "RETRIEVING_CONTEXT");

        assertEquals("RETRIEVING_CONTEXT", latest.get().getCurrentStep());
        cleanup.run();
    }
}