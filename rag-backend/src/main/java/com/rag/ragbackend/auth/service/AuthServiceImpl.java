package com.rag.ragbackend.auth.service;

import com.rag.ragbackend.auth.dto.AuthResponse;
import com.rag.ragbackend.auth.dto.LoginRequest;
import com.rag.ragbackend.entity.Employee;
import com.rag.ragbackend.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        String storedPassword = employee.getPassword();
        boolean passwordMatches = storedPassword != null && (
                storedPassword.startsWith("$2")
                        ? passwordEncoder.matches(request.getPassword(), storedPassword)
                        : storedPassword.equals(request.getPassword())
        );

        if (!passwordMatches) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String fullName = String.join(" ",
                employee.getFirstName() == null ? "" : employee.getFirstName().trim(),
                employee.getLastName() == null ? "" : employee.getLastName().trim()).trim();

        return new AuthResponse(
                employee.getId() != null ? Long.valueOf(employee.getId()) : null,
                fullName.isEmpty() ? employee.getEmail() : fullName,
                employee.getEmail(),
                employee.getRole(),
                "Login successful"
        );
    }
}
