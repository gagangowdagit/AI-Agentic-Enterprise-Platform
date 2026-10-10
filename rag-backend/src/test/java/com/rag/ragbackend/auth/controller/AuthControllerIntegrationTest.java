package com.rag.ragbackend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.ragbackend.auth.dto.LoginRequest;
import com.rag.ragbackend.auth.service.JwtService;
import com.rag.ragbackend.entity.Employee;
import com.rag.ragbackend.repository.EmployeeRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.jwt.secret=dGFzazItdGVzdC1vbmx5LXNpZ25pbmcta2V5LTMyLWJ5dGVz")
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private Employee employee;

    private static final String TEST_EMAIL = "test.user@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_ROLE = "MANAGER";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilters(springSecurityFilterChain)
            .build();
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
        this.employee = employeeRepository.save(employee);
    }

    @Test
    void loginWithCorrectCredentialsReturnsSuccess() throws Exception {
        LoginRequest request = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Test Employee"))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.role").value(TEST_ROLE))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900))
                .andExpect(jsonPath("$.expiresAt").isNotEmpty())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();

            String token = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("accessToken").asText();
            assertTrue(jwtService.isTokenValid(token));
            assertEquals(employee.getId(), jwtService.extractEmployeeId(token));
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
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.accessToken").doesNotExist());
    }

    @Test
    void loginWithNonExistentEmailReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("nonexistent@example.com", TEST_PASSWORD);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.accessToken").doesNotExist());
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

            @Test
            void protectedEndpointRejectsMissingToken() throws Exception {
            mockMvc.perform(post("/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"check\"}"))
                .andExpect(status().isUnauthorized());
            }

            @Test
            void protectedEndpointRejectsInvalidToken() throws Exception {
            mockMvc.perform(post("/api/validate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-jwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"check\"}"))
                .andExpect(status().isUnauthorized());
            }

            @Test
            void protectedEndpointRejectsTokenWithInvalidSignature() throws Exception {
            SecretKey otherKey = Keys.hmacShaKeyFor(
                "different-test-signing-key-that-is-not-the-app-key".getBytes(StandardCharsets.UTF_8));
            Instant now = Instant.now();
            String incorrectlySignedToken = Jwts.builder()
                .subject(employee.getId().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(900)))
                .signWith(otherKey, Jwts.SIG.HS256)
                .compact();

            mockMvc.perform(post("/api/validate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + incorrectlySignedToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"check\"}"))
                .andExpect(status().isUnauthorized());
            }

            @Test
            void protectedEndpointRejectsExpiredToken() throws Exception {
            mockMvc.perform(post("/api/validate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"check\"}"))
                .andExpect(status().isUnauthorized());
            }

            @Test
            void protectedEndpointAcceptsValidToken() throws Exception {
            String token = jwtService.generateAccessToken(employee.getId()).token();

            mockMvc.perform(post("/api/validate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"check\"}"))
                .andExpect(status().isOk());
            }

            @Test
            void projectApiRequiresAndAcceptsValidToken() throws Exception {
            mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isUnauthorized());

            String token = jwtService.generateAccessToken(employee.getId()).token();
            mockMvc.perform(get("/api/v1/projects")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
            }

            @Test
            void healthAndSignupDependenciesRemainPublic() throws Exception {
            mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk());

            mockMvc.perform(get("/api/v1/departments"))
                .andExpect(status().isOk());

            mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
            }

            @Test
            void corsPreflightIsNotBlockedByAuthentication() throws Exception {
            mockMvc.perform(options("/api/v1/projects")
                .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                    .header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"));
            }

            private String expiredToken() {
            SecretKey testKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode("dGFzazItdGVzdC1vbmx5LXNpZ25pbmcta2V5LTMyLWJ5dGVz"));
            Instant now = Instant.now();
            return Jwts.builder()
                .subject(employee.getId().toString())
                .issuedAt(Date.from(now.minusSeconds(120)))
                .expiration(Date.from(now.minusSeconds(60)))
                .signWith(testKey, Jwts.SIG.HS256)
                .compact();
            }
}
