package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.CreateMeetingRequest;
import com.rag.ragbackend.dto.MeetingDto;
import com.rag.ragbackend.service.MeetingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/meetings/{meetingId}")
    public ResponseEntity<MeetingDto> getMeetingById(@PathVariable Long meetingId) {
        return ResponseEntity.ok(meetingService.getMeetingById(meetingId));
    }

    @PostMapping("/meetings")
    public ResponseEntity<MeetingDto> createMeeting(@Valid @RequestBody CreateMeetingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(meetingService.createMeeting(request));
    }
}
