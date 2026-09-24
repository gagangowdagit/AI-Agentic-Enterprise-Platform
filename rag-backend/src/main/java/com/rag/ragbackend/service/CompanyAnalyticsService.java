package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.CompanyAnalyticsResponse;
import com.rag.ragbackend.dto.DepartmentAnalyticsResponse;
import com.rag.ragbackend.dto.EmployeeAnalyticsResponse;

import java.util.List;

public interface CompanyAnalyticsService {
    CompanyAnalyticsResponse getOverview();

    List<DepartmentAnalyticsResponse> getDepartmentBreakdown();

    List<EmployeeAnalyticsResponse> getEmployeeBreakdown();
}