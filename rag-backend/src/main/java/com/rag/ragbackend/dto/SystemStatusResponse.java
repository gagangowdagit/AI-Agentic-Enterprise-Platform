package com.rag.ragbackend.dto;

public class SystemStatusResponse {

    private String status;
    private String service;
    private String message;

    public SystemStatusResponse() {
    }

    public SystemStatusResponse(String status, String service, String message) {
        this.status = status;
        this.service = service;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
