package com.rag.ragbackend.exception;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(Integer employeeId) {
        super("Employee not found: " + employeeId);
    }
}