package com.rag.ragbackend.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.rag.ragbackend.dto.SystemStatusResponse;
import com.rag.ragbackend.model.SystemStatus;

class SystemStatusMapperTest {

    @Test
    void shouldMapSystemStatusToResponse() {
        SystemStatusMapper mapper = new SystemStatusMapper();
        SystemStatus status = new SystemStatus("UP", "rag-backend", "System is running successfully");

        SystemStatusResponse response = mapper.toResponse(status);

        assertNotNull(response);
        assertEquals("UP", response.getStatus());
        assertEquals("rag-backend", response.getService());
        assertEquals("System is running successfully", response.getMessage());
    }

    @Test
    void shouldReturnNullForNullStatus() {
        SystemStatusMapper mapper = new SystemStatusMapper();

        assertNull(mapper.toResponse(null));
    }
}
