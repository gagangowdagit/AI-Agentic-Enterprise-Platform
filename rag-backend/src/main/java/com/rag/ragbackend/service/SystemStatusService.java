package com.rag.ragbackend.service;

import org.springframework.stereotype.Service;

import com.rag.ragbackend.dto.SystemStatusResponse;
import com.rag.ragbackend.mapper.SystemStatusMapper;
import com.rag.ragbackend.model.SystemStatus;
import com.rag.ragbackend.repository.SystemStatusRepository;

@Service
public class SystemStatusService {

    private final SystemStatusRepository systemStatusRepository;
    private final SystemStatusMapper systemStatusMapper;

    public SystemStatusService(SystemStatusRepository systemStatusRepository, SystemStatusMapper systemStatusMapper) {
        this.systemStatusRepository = systemStatusRepository;
        this.systemStatusMapper = systemStatusMapper;
    }

    public SystemStatusResponse getSystemStatus() {
        SystemStatus status = new SystemStatus(
            systemStatusRepository.getStatus(),
            "rag-backend",
            "System is running successfully"
        );

        return systemStatusMapper.toResponse(status);
    }
}
