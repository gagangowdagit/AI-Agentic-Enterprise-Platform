package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.CreateMeetingRequest;
import com.rag.ragbackend.dto.EmployeeTeamResponse;
import com.rag.ragbackend.dto.MeetingDto;
import com.rag.ragbackend.entity.Employee;
import com.rag.ragbackend.entity.Meeting;
import com.rag.ragbackend.entity.MeetingStatus;
import com.rag.ragbackend.exception.EmployeeNotFoundException;
import com.rag.ragbackend.repository.EmployeeRepository;
import com.rag.ragbackend.repository.MeetingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final EmployeeRepository employeeRepository;

    public MeetingServiceImpl(MeetingRepository meetingRepository, EmployeeRepository employeeRepository) {
        this.meetingRepository = meetingRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<MeetingDto> getMeetings() {
        return meetingRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public MeetingDto getMeetingById(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));
        return toDto(meeting);
    }

    @Override
    public MeetingDto createMeeting(CreateMeetingRequest request) {
        Meeting meeting = new Meeting();
        meeting.setTitle(request.title().trim());
        meeting.setDescription(request.description());
        meeting.setProjectId(request.projectId().trim());
        meeting.setProjectName(request.projectName() == null || request.projectName().isBlank()
                ? "Unassigned Project"
                : request.projectName().trim());
        meeting.setMeetingDate(request.meetingDate());
        meeting.setStartTime(request.startTime().trim());
        meeting.setEndTime(request.endTime().trim());
        meeting.setAgenda(request.agenda());
        meeting.setGoogleMeetUrl(request.googleMeetUrl());
        meeting.setStatus(MeetingStatus.Scheduled);
        meeting.setCreatedBy("system");
        LocalDateTime now = LocalDateTime.now();
        meeting.setCreatedAt(now);
        meeting.setUpdatedAt(now);

        List<Integer> participantIds = request.participantIds() == null ? List.of() : request.participantIds();
        Set<Integer> uniqueParticipantIds = new LinkedHashSet<>(participantIds);
        if (uniqueParticipantIds.size() != participantIds.size()) {
            throw new IllegalArgumentException("Duplicate participant IDs are not allowed");
        }

        List<Employee> participantsFromDb = employeeRepository.findAllById(uniqueParticipantIds);
        if (participantsFromDb.size() != uniqueParticipantIds.size()) {
            Set<Integer> foundIds = participantsFromDb.stream().map(Employee::getId).collect(Collectors.toSet());
            Integer missingId = uniqueParticipantIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .findFirst()
                    .orElse(null);
            if (missingId != null) {
                throw new EmployeeNotFoundException(missingId);
            }
        }

        Set<Employee> participants = new LinkedHashSet<>(participantsFromDb);
        meeting.setParticipants(participants);
        meeting.setParticipantCount(participants.size());

        Meeting savedMeeting = meetingRepository.save(meeting);
        return toDto(savedMeeting);
    }

    private MeetingDto toDto(Meeting meeting) {
        List<Integer> participantIds = meeting.getParticipants() == null
                ? new ArrayList<>()
                : meeting.getParticipants().stream().map(Employee::getId).collect(Collectors.toList());

        List<EmployeeTeamResponse> participantDetails = meeting.getParticipants() == null
                ? new ArrayList<>()
                : meeting.getParticipants().stream()
                        .sorted((left, right) -> left.getLastName().compareToIgnoreCase(right.getLastName()))
                        .map(EmployeeTeamResponse::from)
                        .collect(Collectors.toList());

        return new MeetingDto(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getDescription(),
                meeting.getProjectId(),
                meeting.getProjectName(),
                meeting.getMeetingDate(),
                meeting.getStartTime(),
                meeting.getEndTime(),
                meeting.getParticipantCount(),
                meeting.getStatus(),
                meeting.getGoogleMeetUrl(),
                meeting.getAgenda(),
                participantIds,
                participantDetails,
                meeting.getCreatedBy(),
                meeting.getCreatedAt(),
                meeting.getUpdatedAt()
        );
    }
}
