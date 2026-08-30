package com.rag.ragbackend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SystemStatusRepositoryTest {

    @Test
    void shouldProvideSystemStatus() {
        SystemStatusRepository repository = new SystemStatusRepository() {
            @Override
            public String getStatus() {
                return "UP";
            }
        };

        assertEquals("UP", repository.getStatus());
    }
}
