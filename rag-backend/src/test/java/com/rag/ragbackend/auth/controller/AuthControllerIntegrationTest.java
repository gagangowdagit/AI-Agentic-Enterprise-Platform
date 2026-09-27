package com.rag.ragbackend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.ragbackend.auth.dto.LoginRequest;
import com.rag.ragbackend.entity.Employee;
import com.rag.ragbackend.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final String TEST_EMAIL = "test.user@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_ROLE = "MANAGER";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();

        employeeRepository.findAll().stream()
                .filter(employee -> TEST_EMAIL.equalsIgnoreCase(employee.getEmail()))
                .forEach(employeeRepository::delete);

        Employee employee = new Employee();
        employee.setFirstName("Test");
        employee.setLastName("Employee");
        employee.setEmail(TEST_EMAIL);
        employee.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        employee.setRole(TEST_ROLE);
        employeeRepository.save(employee);
    }

    @Test
    void loginWithCorrectCredentialsReturnsSuccess() throws Exception {
        LoginRequest request = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Test Employee"))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.role").value(TEST_ROLE))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void loginWithEmployeeCredentialsReturnsSuccess() throws Exception {
        LoginRequest request = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.role").value(TEST_ROLE))
                .andExpect(jsonPath("$.name").value("Test Employee"));
    }


    @Test
    void loginWithWrongPasswordReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest(TEST_EMAIL, "wrongPassword");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithNonExistentEmailReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("nonexistent@example.com", TEST_PASSWORD);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithMissingEmailReturnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setPassword(TEST_PASSWORD);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginWithMissingPasswordReturnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail(TEST_EMAIL);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginWithInvalidEmailFormatReturnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest("not-an-email", TEST_PASSWORD);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
