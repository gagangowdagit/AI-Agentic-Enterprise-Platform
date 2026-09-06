package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.EmployeeTeamResponse;
import com.rag.ragbackend.service.TeamService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectTeamController {

    private final TeamService teamService;

    public ProjectTeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/{projectId}/team")
    public ResponseEntity<List<EmployeeTeamResponse>> getTeam(@PathVariable String projectId) {
        return ResponseEntity.ok(teamService.getTeamMembers(projectId));
    }

    @GetMapping("/{projectId}/team/available")
    public ResponseEntity<List<EmployeeTeamResponse>> getAvailableEmployees(@PathVariable String projectId) {
        return ResponseEntity.ok(teamService.getAvailableEmployees(projectId));
    }

    @PostMapping("/{projectId}/team")
    public ResponseEntity<EmployeeTeamResponse> addTeamMember(
            @PathVariable String projectId,
            @Valid @RequestBody AddTeamMemberRequest request) {
        return ResponseEntity.ok(teamService.addTeamMember(projectId, request.employeeId()));
    }

    @DeleteMapping("/{projectId}/team/{employeeId}")
    public ResponseEntity<Void> removeTeamMember(
            @PathVariable String projectId,
            @PathVariable Integer employeeId) {
        teamService.removeTeamMember(projectId, employeeId);
        return ResponseEntity.noContent().build();
    }

    public record AddTeamMemberRequest(@NotNull Integer employeeId) {
    }
}