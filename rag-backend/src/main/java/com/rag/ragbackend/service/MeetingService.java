package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.MeetingDto;

import java.util.List;

public interface MeetingService {
    List<MeetingDto> getMeetings();
}
