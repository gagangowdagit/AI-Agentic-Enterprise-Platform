package com.rag.ragbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rag.ragbackend.dto.SystemStatusResponse;
import com.rag.ragbackend.service.SystemStatusService;

@RestController
@RequestMapping({"/api", "/api/v1"})
public class SystemController {

    private final SystemStatusService systemStatusService;

    public SystemController(SystemStatusService systemStatusService) {
        this.systemStatusService = systemStatusService;
    }

    @GetMapping("/system/status")
    public SystemStatusResponse getSystemStatus() {
        return systemStatusService.getSystemStatus();
    }
}
