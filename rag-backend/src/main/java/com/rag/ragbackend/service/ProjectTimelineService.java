package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.ProjectTimeline;

public interface ProjectTimelineService {

    ProjectTimeline getTimeline(String projectId);
}