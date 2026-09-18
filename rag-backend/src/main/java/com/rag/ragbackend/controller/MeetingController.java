package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.MeetingDto;
import com.rag.ragbackend.service.MeetingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @GetMapping("/meetings")
    public ResponseEntity<List<MeetingDto>> getMeetings() {
        return ResponseEntity.ok(meetingService.getMeetings());
    }
}
