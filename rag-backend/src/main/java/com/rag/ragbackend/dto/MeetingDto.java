package com.rag.ragbackend.dto;

import com.rag.ragbackend.entity.MeetingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MeetingDto {
    private Long id;
    private String title;
    private String description;
    private String agenda;
    private String projectId;
    private String projectName;
    private LocalDate meetingDate;
    private String startTime;
    private String endTime;
    private Integer participantCount;
    private MeetingStatus status;
    private String googleMeetUrl;
    private List<Integer> participantIds = new ArrayList<>();
    private List<EmployeeTeamResponse> participants = new ArrayList<>();
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MeetingDto() {
    }

    public MeetingDto(Long id, String title, String description, String projectId, String projectName,
                      LocalDate meetingDate, String startTime, String endTime,
                      Integer participantCount, MeetingStatus status, String googleMeetUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.projectId = projectId;
        this.projectName = projectName;
        this.meetingDate = meetingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participantCount = participantCount;
        this.status = status;
        this.googleMeetUrl = googleMeetUrl;
    }

    public MeetingDto(Long id, String title, String description, String projectId, String projectName,
                      LocalDate meetingDate, String startTime, String endTime,
                      Integer participantCount, MeetingStatus status, String googleMeetUrl,
                      String agenda, List<Integer> participantIds, String createdBy,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.agenda = agenda;
        this.projectId = projectId;
        this.projectName = projectName;
        this.meetingDate = meetingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participantCount = participantCount;
        this.status = status;
        this.googleMeetUrl = googleMeetUrl;
        this.participantIds = participantIds == null ? new ArrayList<>() : participantIds;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public MeetingDto(Long id, String title, String description, String projectId, String projectName,
                      LocalDate meetingDate, String startTime, String endTime,
                      Integer participantCount, MeetingStatus status, String googleMeetUrl,
                      String agenda, List<Integer> participantIds, List<EmployeeTeamResponse> participants,
                      String createdBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, title, description, projectId, projectName, meetingDate, startTime, endTime,
                participantCount, status, googleMeetUrl, agenda, participantIds, createdBy, createdAt, updatedAt);
        this.participants = participants == null ? new ArrayList<>() : participants;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public LocalDate getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(LocalDate meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
    }

    public MeetingStatus getStatus() {
        return status;
    }

    public void setStatus(MeetingStatus status) {
        this.status = status;
    }

    public String getGoogleMeetUrl() {
        return googleMeetUrl;
    }

    public void setGoogleMeetUrl(String googleMeetUrl) {
        this.googleMeetUrl = googleMeetUrl;
    }

    public List<Integer> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<Integer> participantIds) {
        this.participantIds = participantIds == null ? new ArrayList<>() : participantIds;
    }

    public List<EmployeeTeamResponse> getParticipants() {
        return participants;
    }

    public void setParticipants(List<EmployeeTeamResponse> participants) {
        this.participants = participants == null ? new ArrayList<>() : participants;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
