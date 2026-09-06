package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.EmployeeTeamResponse;
import com.rag.ragbackend.exception.EmployeeNotFoundException;
import com.rag.ragbackend.exception.GlobalExceptionHandler;
import com.rag.ragbackend.service.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectTeamControllerTest {

    @Test
    void returnsTeamMembersAndDepartmentDetailsForProject() throws Exception {
        TeamService teamService = mock(TeamService.class);
        when(teamService.getTeamMembers("7")).thenReturn(List.of(
                new EmployeeTeamResponse(
                        4,
                        "Ada",
                        "Lovelace",
                        "ada@example.com",
                        "Developer",
                        new EmployeeTeamResponse.DepartmentInfo(2, "Engineering"))));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ProjectTeamController(teamService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        mockMvc.perform(get("/api/v1/projects/7/team"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4))
                .andExpect(jsonPath("$[0].firstName").value("Ada"))
                .andExpect(jsonPath("$[0].lastName").value("Lovelace"))
                .andExpect(jsonPath("$[0].email").value("ada@example.com"))
                .andExpect(jsonPath("$[0].role").value("Developer"))
                .andExpect(jsonPath("$[0].department.id").value(2))
                .andExpect(jsonPath("$[0].department.name").value("Engineering"));

        verify(teamService).getTeamMembers("7");
    }

    @Test
    void returnsEmptyListWhenProjectHasNoEmployees() throws Exception {
        TeamService teamService = mock(TeamService.class);
        when(teamService.getTeamMembers("7")).thenReturn(List.of());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ProjectTeamController(teamService)).build();

        mockMvc.perform(get("/api/v1/projects/7/team"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

            @Test
            void addsAnExistingEmployeeToAProject() throws Exception {
            TeamService teamService = mock(TeamService.class);
            when(teamService.addTeamMember("7", 4)).thenReturn(new EmployeeTeamResponse(
                4, "Ada", "Lovelace", "ada@example.com", "Developer", null));
            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ProjectTeamController(teamService)).build();

            mockMvc.perform(post("/api/v1/projects/7/team")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"employeeId\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.firstName").value("Ada"));

            verify(teamService).addTeamMember("7", 4);
            }

            @Test
            void removesAnEmployeeFromAProject() throws Exception {
            TeamService teamService = mock(TeamService.class);
            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ProjectTeamController(teamService)).build();

            mockMvc.perform(delete("/api/v1/projects/7/team/4"))
                .andExpect(status().isNoContent());

            verify(teamService).removeTeamMember("7", 4);
            }

            @Test
            void returnsNotFoundWhenEmployeeDoesNotExist() throws Exception {
            TeamService teamService = mock(TeamService.class);
            when(teamService.addTeamMember("7", 4)).thenThrow(new EmployeeNotFoundException(4));
            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ProjectTeamController(teamService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

            mockMvc.perform(post("/api/v1/projects/7/team")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"employeeId\":4}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("EMPLOYEE_NOT_FOUND"));
            }
}