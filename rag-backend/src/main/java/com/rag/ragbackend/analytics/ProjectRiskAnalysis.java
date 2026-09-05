package com.rag.ragbackend.analytics;

import com.rag.ragbackend.processing.LlmResponse;

import java.util.List;

public record ProjectRiskAnalysis(
        ProjectAnalytics metrics,
        List<String> risks,
        List<String> bottlenecks,
        List<String> priorities,
        List<String> recommendations,
        LlmResponse aiAnalysis) {
}