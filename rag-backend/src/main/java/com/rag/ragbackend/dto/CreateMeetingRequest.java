package com.rag.ragbackend.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CreateMeetingRequest(
        @NotBlank(message = "Meeting title is required") String title,
        String description,
        @NotBlank(message = "Project is required") String projectId,
        String projectName,
        @NotNull(message = "Meeting date is required") LocalDate meetingDate,
        @NotBlank(message = "Start time is required") String startTime,
        @NotBlank(message = "End time is required") String endTime,
        String googleMeetUrl,
        String agenda,
        List<Integer> participantIds,
        List<AgendaItemRequest> agendaItems
) {
    public record AgendaItemRequest(String title, Integer displayOrder) {
    }

    @AssertTrue(message = "End time must be after start time")
    public boolean isValidTimeRange() {
        if (startTime == null || endTime == null || startTime.isBlank() || endTime.isBlank()) {
            return true;
        }

        try {
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");
            if (startParts.length != 2 || endParts.length != 2) {
                return false;
            }

            int startMinutes = Integer.parseInt(startParts[0]) * 60 + Integer.parseInt(startParts[1]);
            int endMinutes = Integer.parseInt(endParts[0]) * 60 + Integer.parseInt(endParts[1]);
            return endMinutes > startMinutes;
        } catch (Exception ex) {
            return false;
        }
    }

    @AssertTrue(message = "Google Meet URL must be a valid http/https URL when provided")
    public boolean isValidGoogleMeetUrl() {
        if (googleMeetUrl == null || googleMeetUrl.isBlank()) {
            return true;
        }

        try {
            String normalized = googleMeetUrl.trim();
            java.net.URI uri = java.net.URI.create(normalized);
            String scheme = uri.getScheme();
            return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    && uri.getHost() != null && !uri.getHost().isBlank();
        } catch (Exception ex) {
            return false;
        }
    }

    @AssertTrue(message = "Agenda items cannot be blank")
    public boolean isValidAgendaItems() {
        if (agendaItems == null || agendaItems.isEmpty()) {
            return true;
        }

        for (AgendaItemRequest item : agendaItems) {
            if (item == null) {
                return false;
            }

            if (item.title() == null || item.title().trim().isEmpty()) {
                return false;
            }

            if (item.displayOrder() != null && item.displayOrder() < 1) {
                return false;
            }
        }

        return true;
    }
}
