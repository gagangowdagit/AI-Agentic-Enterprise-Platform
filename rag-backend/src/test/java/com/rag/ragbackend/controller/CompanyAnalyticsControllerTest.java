package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.CompanyAnalyticsResponse;
import com.rag.ragbackend.dto.DepartmentAnalyticsResponse;
import com.rag.ragbackend.dto.EmployeeAnalyticsResponse;
import com.rag.ragbackend.service.CompanyAnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CompanyAnalyticsControllerTest {

    @Test
    void returnsOverview() throws Exception {
        CompanyAnalyticsService service = mock(CompanyAnalyticsService.class);
        when(service.getOverview()).thenReturn(new CompanyAnalyticsResponse(3, 2, 8, 1, 1, 1));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new CompanyAnalyticsController(service)).build();

        mockMvc.perform(get("/api/v1/analytics/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProjects").value(3))
                .andExpect(jsonPath("$.totalDepartments").value(2))
                .andExpect(jsonPath("$.totalEmployees").value(8));

        verify(service).getOverview();
    }

    @Test
    void returnsDepartmentBreakdown() throws Exception {
        CompanyAnalyticsService service = mock(CompanyAnalyticsService.class);
        when(service.getDepartmentBreakdown()).thenReturn(List.of(
                new DepartmentAnalyticsResponse(1, "Engineering", "Platform team", 1, 2)
        ));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new CompanyAnalyticsController(service)).build();

        mockMvc.perform(get("/api/v1/analytics/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Engineering"))
                .andExpect(jsonPath("$[0].employeeCount").value(2));

        verify(service).getDepartmentBreakdown();
    }

    @Test
    void returnsEmployeeBreakdown() throws Exception {
        CompanyAnalyticsService service = mock(CompanyAnalyticsService.class);
        when(service.getEmployeeBreakdown()).thenReturn(List.of(
                new EmployeeAnalyticsResponse(10, "Ada", "Lovelace", "ada@company.com", "Engineer", "Engineering")
        ));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new CompanyAnalyticsController(service)).build();

        mockMvc.perform(get("/api/v1/analytics/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].firstName").value("Ada"))
                .andExpect(jsonPath("$[0].departmentName").value("Engineering"));

        verify(service).getEmployeeBreakdown();
    }
}
