package com.rag.ragbackend.agent;

import com.rag.ragbackend.processing.LlmResponse;
import com.rag.ragbackend.processing.LlmService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AgentServiceTest {

    @Test
    void delegatesUserRequestToExistingLlmServiceWithoutContext() {
        LlmService llmService = mock(LlmService.class);
        AgentService agentService = new AgentService(llmService);
        LlmResponse expected = new LlmResponse("Summarize this", "Summary", List.of());
        when(llmService.generateAnswer("Summarize this", List.of())).thenReturn(expected);

        LlmResponse actual = agentService.execute("Summarize this");

        assertEquals(expected, actual);
        verify(llmService).generateAnswer("Summarize this", List.of());
    }

    @Test
    void rejectsBlankUserRequest() {
        AgentService agentService = new AgentService(mock(LlmService.class));

        assertThrows(IllegalArgumentException.class, () -> agentService.execute("  "));
    }
}