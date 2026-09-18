package com.rag.ragbackend.dto;

import com.rag.ragbackend.entity.MeetingStatus;

import java.time.LocalDate;

public class MeetingDto {
    private Long id;
    private String title;
    private String description;
    private String projectId;
    private String projectName;
    private LocalDate meetingDate;
    private String startTime;
    private String endTime;
    private Integer participantCount;
    private MeetingStatus status;
    private String googleMeetUrl;

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
}
