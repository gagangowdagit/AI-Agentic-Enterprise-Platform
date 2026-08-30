package com.rag.ragbackend.dto;

import java.time.Instant;

public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;
    private String timestamp;
    private ErrorDetails error;

    public ApiResponse() {
        this.timestamp = Instant.now().toString();
    }

    public ApiResponse(boolean success, T data, String message, String timestamp, ErrorDetails error) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.timestamp = timestamp;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        response.message = message;
        return response;
    }

    public static <T> ApiResponse<T> error(String message, String code, String details) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        response.error = new ErrorDetails(code, details);
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ErrorDetails getError() {
        return error;
    }

    public void setError(ErrorDetails error) {
        this.error = error;
    }

    public static class ErrorDetails {
        private String code;
        private String details;

        public ErrorDetails() {
        }

        public ErrorDetails(String code, String details) {
            this.code = code;
            this.details = details;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }
    }
}
