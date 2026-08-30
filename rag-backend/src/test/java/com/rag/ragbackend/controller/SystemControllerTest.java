package com.rag.ragbackend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.rag.ragbackend.dto.SystemStatusResponse;
import com.rag.ragbackend.mapper.SystemStatusMapper;
import com.rag.ragbackend.repository.SystemStatusRepository;
import com.rag.ragbackend.service.SystemStatusService;

class SystemControllerTest {

    @Test
    void shouldReturnSystemStatus() {
        SystemStatusRepository repository = new SystemStatusRepository();
        SystemStatusMapper mapper = new SystemStatusMapper();
        SystemStatusService service = new SystemStatusService(repository, mapper);
        SystemController controller = new SystemController(service);

        SystemStatusResponse response = controller.getSystemStatus();

        assertEquals("UP", response.getStatus());
        assertEquals("rag-backend", response.getService());
        assertEquals("System is running successfully", response.getMessage());
    }
}
