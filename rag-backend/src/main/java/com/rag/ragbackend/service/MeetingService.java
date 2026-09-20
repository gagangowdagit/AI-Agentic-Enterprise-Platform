package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.CreateMeetingRequest;
import com.rag.ragbackend.dto.MeetingDto;

import java.util.List;

public interface MeetingService {
    List<MeetingDto> getMeetings();

    MeetingDto getMeetingById(Long meetingId);

    MeetingDto createMeeting(CreateMeetingRequest request);

    MeetingDto updateMeeting(Long meetingId, CreateMeetingRequest request);

    MeetingDto cancelMeeting(Long meetingId);

    MeetingDto saveMeetingTranscript(Long meetingId, String transcriptText, String fileName);

    MeetingDto regenerateMeetingSummary(Long meetingId);

    void deleteMeeting(Long meetingId);
}
