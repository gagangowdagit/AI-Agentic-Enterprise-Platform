package com.rag.ragbackend.dto;

import java.time.LocalDate;

public record ProjectTimelineTask(
        String id,
        String title,
        String status,
        LocalDate startDate,
        LocalDate endDate) {
}