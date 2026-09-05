package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.ApiResponse;
import com.rag.ragbackend.execution.AgentExecution;
import com.rag.ragbackend.execution.AgentExecutionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/agents/executions")
public class AgentExecutionController {

    private final AgentExecutionService executionService;

    public AgentExecutionController(AgentExecutionService executionService) {
        this.executionService = executionService;
    }

    @GetMapping("/{executionId}")
    public ResponseEntity<ApiResponse<AgentExecution>> getStatus(@PathVariable String executionId) {
        return ResponseEntity.ok(ApiResponse.success(
                executionService.get(executionId),
                "Agent execution status retrieved."));
    }

    @GetMapping(value = "/{executionId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable String executionId) {
        SseEmitter emitter = new SseEmitter(0L);
        Runnable cleanup = executionService.subscribe(executionId, execution -> send(emitter, execution));
        emitter.onCompletion(cleanup);
        emitter.onTimeout(() -> {
            cleanup.run();
            emitter.complete();
        });
        send(emitter, executionService.get(executionId));
        return emitter;
    }

    private void send(SseEmitter emitter, AgentExecution execution) {
        try {
            emitter.send(SseEmitter.event()
                    .name("execution")
                    .data(execution));
            if ("COMPLETED".equals(execution.getStatus()) || "FAILED".equals(execution.getStatus())) {
                emitter.complete();
            }
        } catch (Exception exception) {
            emitter.completeWithError(exception);
        }
    }
}