package com.rag.ragbackend.service;

import com.rag.ragbackend.auth.dto.AuthResponse;
import com.rag.ragbackend.entity.Employee;
import com.rag.ragbackend.entity.User;
import com.rag.ragbackend.repository.EmployeeRepository;
import com.rag.ragbackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                          EmployeeRepository employeeRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public AuthResponse login(String email, String password) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            String storedPassword = employee.getPassword();
            boolean passwordMatches = storedPassword != null && (
                    storedPassword.startsWith("$2")
                            ? passwordEncoder.matches(password, storedPassword)
                            : Objects.equals(storedPassword, password)
            );

            if (passwordMatches) {
                String fullName = String.join(" ",
                        employee.getFirstName() == null ? "" : employee.getFirstName().trim(),
                        employee.getLastName() == null ? "" : employee.getLastName().trim()).trim();

                return new AuthResponse(employee.getId() != null ? Long.valueOf(employee.getId()) : null,
                        fullName.isEmpty() ? employee.getEmail() : fullName,
                        employee.getEmail(),
                        employee.getRole(),
                        "Login successful");
            }
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        String storedPassword = user.getPassword();
        boolean passwordMatches = storedPassword != null && (
                storedPassword.startsWith("$2")
                        ? passwordEncoder.matches(password, storedPassword)
                        : Objects.equals(storedPassword, password)
        );

        if (!passwordMatches) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), "Login successful");
    }
}