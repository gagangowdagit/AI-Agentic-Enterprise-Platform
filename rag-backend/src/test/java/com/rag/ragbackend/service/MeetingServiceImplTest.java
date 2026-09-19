package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.CreateMeetingRequest;
import com.rag.ragbackend.entity.Department;
import com.rag.ragbackend.entity.Employee;
import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.exception.EmployeeNotFoundException;
import com.rag.ragbackend.repository.EmployeeRepository;
import com.rag.ragbackend.repository.MeetingRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MeetingServiceImplTest {

    @Test
    void rejectsDuplicateParticipantIds() {
        MeetingRepository meetingRepository = mock(MeetingRepository.class);
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        MeetingServiceImpl service = new MeetingServiceImpl(meetingRepository, employeeRepository);

        new Department("Engineering", "Builds");
        new Project(1L, "Alpha", "active");

        CreateMeetingRequest request = new CreateMeetingRequest(
                "Sprint demo",
                "Demo",
                "PRJ-1",
                "Alpha",
                LocalDate.of(2026, 9, 22),
                "10:00",
                "11:00",
                "https://meet.google.com/abc-defg-hij",
                "Agenda",
                List.of(1, 1)
        );

        assertThrows(IllegalArgumentException.class, () -> service.createMeeting(request));
    }

    @Test
    void rejectsUnknownParticipantIds() {
        MeetingRepository meetingRepository = mock(MeetingRepository.class);
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        MeetingServiceImpl service = new MeetingServiceImpl(meetingRepository, employeeRepository);

        when(employeeRepository.findAllById(List.of(1, 2))).thenReturn(List.of(new Employee("Ada", "Lovelace", "ada@example.com", "Developer", null, null)));

        CreateMeetingRequest request = new CreateMeetingRequest(
                "Sprint demo",
                "Demo",
                "PRJ-1",
                "Alpha",
                LocalDate.of(2026, 9, 22),
                "10:00",
                "11:00",
                "https://meet.google.com/abc-defg-hij",
                "Agenda",
                List.of(1, 2)
        );

        assertThrows(EmployeeNotFoundException.class, () -> service.createMeeting(request));
    }
}