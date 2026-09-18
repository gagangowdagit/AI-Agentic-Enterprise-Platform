package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.MeetingDto;
import com.rag.ragbackend.entity.Meeting;
import com.rag.ragbackend.repository.MeetingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;

    public MeetingServiceImpl(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    @Override
    public List<MeetingDto> getMeetings() {
        return meetingRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private MeetingDto toDto(Meeting meeting) {
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
                meeting.getGoogleMeetUrl()
        );
    }
}
