package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.CreateMeetingRequest;
import com.rag.ragbackend.dto.EmployeeTeamResponse;
import com.rag.ragbackend.dto.MeetingDto;
import com.rag.ragbackend.entity.Employee;
import com.rag.ragbackend.entity.Meeting;
import com.rag.ragbackend.entity.MeetingStatus;
import com.rag.ragbackend.exception.EmployeeNotFoundException;
import com.rag.ragbackend.processing.LlmService;
import com.rag.ragbackend.repository.EmployeeRepository;
import com.rag.ragbackend.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final EmployeeRepository employeeRepository;
    private final LlmService llmService;

    public MeetingServiceImpl(MeetingRepository meetingRepository, EmployeeRepository employeeRepository) {
        this(meetingRepository, employeeRepository, null);
    }

    @Autowired
    public MeetingServiceImpl(MeetingRepository meetingRepository, EmployeeRepository employeeRepository, LlmService llmService) {
        this.meetingRepository = meetingRepository;
        this.employeeRepository = employeeRepository;
        this.llmService = llmService;
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
        meeting.setAgenda(buildAgendaText(request));
        meeting.setGoogleMeetUrl(normalizeGoogleMeetUrl(request.googleMeetUrl()));
        meeting.setStatus(resolveStatus(meeting));
        meeting.setCreatedBy("system");
        LocalDateTime now = LocalDateTime.now();
        meeting.setCreatedAt(now);
        meeting.setUpdatedAt(now);

        applyParticipants(meeting, request.participantIds());
        Meeting savedMeeting = meetingRepository.save(meeting);
        return toDto(savedMeeting);
    }

    @Override
    public MeetingDto updateMeeting(Long meetingId, CreateMeetingRequest request) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));

        meeting.setTitle(request.title().trim());
        meeting.setDescription(request.description());
        meeting.setProjectId(request.projectId().trim());
        meeting.setProjectName(request.projectName() == null || request.projectName().isBlank()
                ? "Unassigned Project"
                : request.projectName().trim());
        meeting.setMeetingDate(request.meetingDate());
        meeting.setStartTime(request.startTime().trim());
        meeting.setEndTime(request.endTime().trim());
        meeting.setAgenda(buildAgendaText(request));
        meeting.setGoogleMeetUrl(normalizeGoogleMeetUrl(request.googleMeetUrl()));
        if (meeting.getStatus() != MeetingStatus.Cancelled) {
            meeting.setStatus(resolveStatus(meeting));
        }
        meeting.setUpdatedAt(LocalDateTime.now());

        applyParticipants(meeting, request.participantIds());
        return toDto(meetingRepository.save(meeting));
    }

    @Override
    public MeetingDto cancelMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));

        meeting.setStatus(MeetingStatus.Cancelled);
        meeting.setUpdatedAt(LocalDateTime.now());
        return toDto(meetingRepository.save(meeting));
    }

    @Override
    public MeetingDto saveMeetingTranscript(Long meetingId, String transcriptText, String fileName) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));

        if (transcriptText == null || transcriptText.isBlank()) {
            throw new IllegalArgumentException("Transcript text is required");
        }

        meeting.setTranscriptText(transcriptText.trim());
        meeting.setTranscriptFileName(fileName == null || fileName.isBlank() ? "meeting-transcript.txt" : fileName.trim());
        meeting.setTranscriptUploadedAt(LocalDateTime.now());

        MeetingDto updated = regenerateMeetingSummaryInternal(meeting);
        meetingRepository.save(meeting);
        return updated;
    }

    @Override
    public MeetingDto regenerateMeetingSummary(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));

        if (meeting.getTranscriptText() == null || meeting.getTranscriptText().isBlank()) {
            throw new IllegalArgumentException("Transcript text is required before generating a summary");
        }

        MeetingDto updated = regenerateMeetingSummaryInternal(meeting);
        meetingRepository.save(meeting);
        return updated;
    }

    @Override
    public void deleteMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));

        if (meeting.getParticipants() != null) {
            meeting.getParticipants().clear();
            meeting.setParticipantCount(0);
            meetingRepository.save(meeting);
        }

        meetingRepository.delete(meeting);
    }

    private void applyParticipants(Meeting meeting, List<Integer> participantIds) {
        List<Integer> ids = participantIds == null ? List.of() : participantIds;
        Set<Integer> uniqueParticipantIds = new LinkedHashSet<>(ids);
        if (uniqueParticipantIds.size() != ids.size()) {
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
    }

    private MeetingStatus resolveStatus(Meeting meeting) {
        if (meeting == null || meeting.getStatus() == MeetingStatus.Cancelled) {
            return MeetingStatus.Cancelled;
        }

        if (meeting.getMeetingDate() == null || meeting.getStartTime() == null || meeting.getEndTime() == null) {
            return MeetingStatus.Scheduled;
        }

        try {
            LocalDate meetingDate = meeting.getMeetingDate();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = LocalDateTime.of(meetingDate, LocalTime.parse(meeting.getStartTime()));
            LocalDateTime end = LocalDateTime.of(meetingDate, LocalTime.parse(meeting.getEndTime()));

            if (now.isBefore(start)) {
                return MeetingStatus.Scheduled;
            }
            if (now.isAfter(end) || now.isEqual(end)) {
                return MeetingStatus.Completed;
            }
            if (now.isAfter(start) && now.isBefore(end)) {
                return MeetingStatus.In_Progress;
            }
        } catch (Exception ignored) {
            // Fallback to scheduled when time parsing fails.
        }

        return MeetingStatus.Scheduled;
    }

    private String normalizeGoogleMeetUrl(String googleMeetUrl) {
        if (googleMeetUrl == null || googleMeetUrl.isBlank()) {
            return null;
        }
        String trimmed = googleMeetUrl.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String buildAgendaText(CreateMeetingRequest request) {
        if (request.agendaItems() != null && !request.agendaItems().isEmpty()) {
            return request.agendaItems().stream()
                    .filter(item -> item != null && item.title() != null && !item.title().trim().isEmpty())
                    .sorted((left, right) -> {
                        int leftOrder = left.displayOrder() == null ? 0 : left.displayOrder();
                        int rightOrder = right.displayOrder() == null ? 0 : right.displayOrder();
                        return Integer.compare(leftOrder, rightOrder);
                    })
                    .map(item -> String.format("%d. %s", item.displayOrder() == null ? 0 : item.displayOrder(), item.title().trim()))
                    .collect(Collectors.joining("\n"));
        }

        return request.agenda();
    }

    private MeetingDto regenerateMeetingSummaryInternal(Meeting meeting) {
        ParsedMeetingInsights insights = buildMeetingInsights(meeting.getTranscriptText());
        meeting.setAiSummary(insights.summary());
        meeting.setAiActionItems(String.join("\n", insights.actionItems()));
        meeting.setAiDecisions(String.join("\n", insights.decisions()));
        meeting.setUpdatedAt(LocalDateTime.now());
        return toDto(meeting);
    }

    private ParsedMeetingInsights buildMeetingInsights(String transcriptText) {
        String normalizedTranscript = transcriptText == null ? "" : transcriptText.trim();
        if (normalizedTranscript.isBlank()) {
            return new ParsedMeetingInsights("No transcript available.", List.of(), List.of());
        }

        String summary = summarizeTranscript(normalizedTranscript);
        List<String> actionItems = extractListAfterLabel(normalizedTranscript, "action items|next steps|follow-ups|follow up");
        List<String> decisions = extractListAfterLabel(normalizedTranscript, "decisions?|outcomes?|agreed|confirmed|approved");

        if (actionItems.isEmpty()) {
            actionItems = List.of("Review transcript and define follow-up tasks.");
        }
        if (decisions.isEmpty()) {
            decisions = List.of("No explicit decision was captured in the transcript.");
        }

        return new ParsedMeetingInsights(summary, actionItems, decisions);
    }

    private String summarizeTranscript(String transcriptText) {
        String cleanTranscript = transcriptText.replaceAll("\\s+", " ").trim();
        if (cleanTranscript.length() <= 220) {
            return cleanTranscript;
        }
        return cleanTranscript.substring(0, 217).trim() + "...";
    }

    private List<String> extractListAfterLabel(String transcriptText, String labelPattern) {
        String patternText = "(?i)(?:" + labelPattern + ")\\s*[:\\-]?\\s*(.+?)(?=(?:\\b(?:action items|next steps|follow-ups|follow up|decisions?|outcomes?|agreed|confirmed|approved)\\b)|$)";
        Pattern pattern = Pattern.compile(patternText, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(transcriptText);
        List<String> values = new ArrayList<>();

        while (matcher.find()) {
            String value = matcher.group(1).trim();
            if (value.isBlank()) {
                continue;
            }
            values.addAll(splitInsightEntries(value));
        }

        return values.stream().map(this::normalizeInsightEntry).filter(item -> !item.isBlank()).distinct().collect(Collectors.toList());
    }

    private List<String> splitInsightEntries(String value) {
        String normalized = value.replaceAll("\\s*[-•*]\\s*", "; ")
                .replaceAll("\\s*\\|\\s*", "; ")
                .replaceAll("\\s*;\\s*", "; ");
        return Arrays.stream(normalized.split(";"))
                .map(String::trim)
                .filter(part -> !part.isBlank())
                .collect(Collectors.toList());
    }

    private String normalizeInsightEntry(String value) {
        return value.replaceAll("\\s+", " ").trim();
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

        MeetingStatus status = resolveStatus(meeting);
        List<String> aiActionItems = parseStoredTextList(meeting.getAiActionItems());
        List<String> aiDecisions = parseStoredTextList(meeting.getAiDecisions());

        MeetingDto dto = new MeetingDto(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getDescription(),
                meeting.getProjectId(),
                meeting.getProjectName(),
                meeting.getMeetingDate(),
                meeting.getStartTime(),
                meeting.getEndTime(),
                meeting.getParticipantCount(),
                status,
                meeting.getGoogleMeetUrl(),
                meeting.getAgenda(),
                participantIds,
                participantDetails,
                meeting.getCreatedBy(),
                meeting.getCreatedAt(),
                meeting.getUpdatedAt()
        );
        dto.setTranscriptText(meeting.getTranscriptText());
        dto.setTranscriptFileName(meeting.getTranscriptFileName());
        dto.setTranscriptUploadedAt(meeting.getTranscriptUploadedAt());
        dto.setAiSummary(meeting.getAiSummary());
        dto.setAiActionItems(aiActionItems);
        dto.setAiDecisions(aiDecisions);
        return dto;
    }

    private List<String> parseStoredTextList(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(rawValue.split("\\R+|;"))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .collect(Collectors.toList());
    }

    private record ParsedMeetingInsights(String summary, List<String> actionItems, List<String> decisions) {
    }
}
