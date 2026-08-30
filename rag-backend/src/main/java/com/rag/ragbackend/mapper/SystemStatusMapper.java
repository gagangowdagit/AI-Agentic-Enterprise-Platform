package com.rag.ragbackend.mapper;

import org.springframework.stereotype.Component;

import com.rag.ragbackend.dto.SystemStatusResponse;
import com.rag.ragbackend.model.SystemStatus;

@Component
public class SystemStatusMapper {

    public SystemStatusResponse toResponse(SystemStatus status) {
        if (status == null) {
            return null;
        }

        return new SystemStatusResponse(
            status.getStatus(),
            status.getService(),
            status.getMessage()
        );
    }
}
