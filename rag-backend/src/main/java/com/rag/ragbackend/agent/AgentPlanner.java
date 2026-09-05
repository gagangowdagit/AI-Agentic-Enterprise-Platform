package com.rag.ragbackend.agent;

import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class AgentPlanner {

    public AgentDecision decide(String userRequest) {
        if (userRequest == null || userRequest.isBlank()) {
            throw new IllegalArgumentException("Agent request is required.");
        }

        String normalized = userRequest.toLowerCase(Locale.ROOT).trim();

        boolean isExplicitToolRequest = normalized.contains("show project")
                || normalized.contains("project info")
                || normalized.contains("project status")
                || normalized.contains("search project")
                || normalized.contains("find in project")
                || normalized.contains("project knowledge")
                || normalized.contains("knowledge search")
                || normalized.contains("create task")
                || normalized.contains("update task")
                || normalized.contains("list project tasks")
                || normalized.contains("get project tasks");

        boolean looksProjectSpecific = normalized.contains("project")
                || normalized.contains("contract")
                || normalized.contains("document")
                || normalized.contains("report")
                || normalized.contains("team")
                || normalized.contains("policy")
                || normalized.contains("requirement")
                || normalized.contains("summary")
                || normalized.contains("analysis");

        if (isExplicitToolRequest) {
            return AgentDecision.USE_TOOL;
        }

        if (looksProjectSpecific) {
            return AgentDecision.USE_RAG;
        }

        return AgentDecision.ANSWER_DIRECTLY;
    }
}
