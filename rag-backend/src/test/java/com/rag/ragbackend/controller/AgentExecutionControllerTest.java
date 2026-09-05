package com.rag.ragbackend.controller;

import com.rag.ragbackend.execution.AgentExecution;
import com.rag.ragbackend.execution.AgentExecutionService;
import com.rag.ragbackend.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AgentExecutionControllerTest {

    @Test
    void returnsCurrentExecutionStatus() throws Exception {
        AgentExecutionService service = mock(AgentExecutionService.class);
        AgentExecutionController controller = new AgentExecutionController(service);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        AgentExecution execution = new AgentExecution("project-1", "GENERATING_RESPONSE");
        when(service.get("execution-1")).thenReturn(execution);

        mockMvc.perform(get("/api/v1/agents/executions/execution-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("RUNNING"))
                .andExpect(jsonPath("$.data.currentStep").value("GENERATING_RESPONSE"));

        verify(service).get("execution-1");
    }

    @Test
    void createsSseStreamAndRegistersExecutionListener() {
        AgentExecutionService service = mock(AgentExecutionService.class);
        AgentExecutionController controller = new AgentExecutionController(service);
        AgentExecution execution = new AgentExecution("project-1", "STARTING");
        when(service.get("execution-1")).thenReturn(execution);
        when(service.subscribe(org.mockito.ArgumentMatchers.eq("execution-1"), org.mockito.ArgumentMatchers.any()))
            .thenReturn(() -> {
            });

        SseEmitter emitter = controller.stream("execution-1");

        assertNotNull(emitter);
        verify(service).subscribe(org.mockito.ArgumentMatchers.eq("execution-1"), org.mockito.ArgumentMatchers.any());
        verify(service).get("execution-1");
        emitter.complete();
    }
}