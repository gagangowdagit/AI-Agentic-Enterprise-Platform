package com.rag.ragbackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.rag.ragbackend.dto.SystemStatusResponse;
import com.rag.ragbackend.mapper.SystemStatusMapper;
import com.rag.ragbackend.repository.SystemStatusRepository;

class SystemStatusServiceTest {

    @Test
    void shouldReturnSystemStatus() {
        SystemStatusRepository repository = new SystemStatusRepository();
        SystemStatusMapper mapper = new SystemStatusMapper();
        SystemStatusService service = new SystemStatusService(repository, mapper);

        SystemStatusResponse response = service.getSystemStatus();

        assertEquals("UP", response.getStatus());
        assertEquals("rag-backend", response.getService());
        assertEquals("System is running successfully", response.getMessage());
    }
}
