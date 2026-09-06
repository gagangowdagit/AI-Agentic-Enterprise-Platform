package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.CreateDepartmentRequest;
import com.rag.ragbackend.dto.DepartmentResponse;
import com.rag.ragbackend.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DepartmentControllerTest {

    @Test
    void returnsDepartments() throws Exception {
        DepartmentService service = mock(DepartmentService.class);
        when(service.getDepartments()).thenReturn(List.of(new DepartmentResponse(1, "Engineering", "Builds products")));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new DepartmentController(service)).build();

        mockMvc.perform(get("/api/v1/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Engineering"));

        verify(service).getDepartments();
    }

    @Test
    void createsDepartment() throws Exception {
        DepartmentService service = mock(DepartmentService.class);
        when(service.createDepartment(any(CreateDepartmentRequest.class)))
                .thenReturn(new DepartmentResponse(1, "Engineering", "Builds products"));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new DepartmentController(service)).build();

        mockMvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Engineering\",\"description\":\"Builds products\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Engineering"));
    }
}