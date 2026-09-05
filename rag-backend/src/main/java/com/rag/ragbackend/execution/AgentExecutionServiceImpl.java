package com.rag.ragbackend.execution;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
public class AgentExecutionServiceImpl implements AgentExecutionService {

    private final AgentExecutionRepository executionRepository;
    private final Map<String, Map<String, Consumer<AgentExecution>>> listeners = new ConcurrentHashMap<>();

    public AgentExecutionServiceImpl(AgentExecutionRepository executionRepository) {
        this.executionRepository = executionRepository;
    }

    @Override
    public AgentExecution start(String projectId, String initialStep) {
        requireValue(initialStep, "Initial step");
        AgentExecution execution = executionRepository.save(new AgentExecution(projectId, initialStep));
        publish(execution);
        return execution;
    }

    @Override
    public AgentExecution updateStep(String executionId, String step) {
        AgentExecution execution = find(executionId);
        requireValue(step, "Execution step");
        execution.setCurrentStep(step);
        AgentExecution saved = executionRepository.save(execution);
        publish(saved);
        return saved;
    }

    @Override
    public AgentExecution complete(String executionId, String message) {
        return finish(executionId, "COMPLETED", message);
    }

    @Override
    public AgentExecution fail(String executionId, String message) {
        return finish(executionId, "FAILED", message);
    }

    private AgentExecution finish(String executionId, String status, String message) {
        AgentExecution execution = find(executionId);
        execution.setStatus(status);
        execution.setCurrentStep(message);
        execution.setEndTime(LocalDateTime.now());
        AgentExecution saved = executionRepository.save(execution);
        publish(saved);
        return saved;
    }

    @Override
    public AgentExecution get(String executionId) {
        return find(executionId);
    }

    @Override
    public Runnable subscribe(String executionId, Consumer<AgentExecution> listener) {
        find(executionId);
        if (listener == null) {
            throw new IllegalArgumentException("Execution listener is required.");
        }
        String listenerId = UUID.randomUUID().toString();
        listeners.computeIfAbsent(executionId, ignored -> new ConcurrentHashMap<>())
                .put(listenerId, listener);
        return () -> removeListener(executionId, listenerId);
    }

    private AgentExecution find(String executionId) {
        requireValue(executionId, "Execution ID");
        return executionRepository.findById(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found: " + executionId));
    }

    private void publish(AgentExecution execution) {
        Map<String, Consumer<AgentExecution>> executionListeners = listeners.get(execution.getExecutionId());
        if (executionListeners == null) {
            return;
        }
        List.copyOf(executionListeners.values()).forEach(listener -> listener.accept(execution));
    }

    private void removeListener(String executionId, String listenerId) {
        Map<String, Consumer<AgentExecution>> executionListeners = listeners.get(executionId);
        if (executionListeners == null) {
            return;
        }
        executionListeners.remove(listenerId);
        if (executionListeners.isEmpty()) {
            listeners.remove(executionId);
        }
    }

    private void requireValue(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }
}