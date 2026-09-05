package com.rag.ragbackend.agent;

import com.rag.ragbackend.processing.LlmResponse;
import com.rag.ragbackend.processing.LlmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class AgentCoordinator {

    private final List<SpecializedAgent> agents = new ArrayList<>();
    private final LlmService llmService;

    @Autowired
    public AgentCoordinator(List<SpecializedAgent> agents, LlmService llmService) {
        this.llmService = llmService;
        if (agents != null) {
            agents.forEach(this::register);
        }
    }

    public AgentCoordinator(List<SpecializedAgent> agents) {
        this(agents, null);
    }

    public void register(SpecializedAgent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("Specialized agent is required.");
        }
        if (agent.getName() == null || agent.getName().isBlank()) {
            throw new IllegalArgumentException("Specialized agent name is required.");
        }
        agents.removeIf(existing -> existing.getName().equals(agent.getName()));
        agents.add(agent);
    }

    public SpecializedAgent selectAgent(String userRequest) {
        return selectAgents(userRequest).get(0);
    }

    public List<SpecializedAgent> selectAgents(String userRequest) {
        if (userRequest == null || userRequest.isBlank()) {
            throw new IllegalArgumentException("Agent request is required.");
        }
        if (agents.isEmpty()) {
            throw new IllegalStateException("No specialized agents are registered.");
        }

        String normalizedRequest = userRequest.toLowerCase(Locale.ROOT);
        List<SpecializedAgent> matchingAgents = agents.stream()
                .filter(agent -> normalizedRequest.contains(agent.getName().toLowerCase(Locale.ROOT))
                        || hasCapabilityMatch(normalizedRequest, agent.getCapabilityDescription()))
                .toList();

        if (!matchingAgents.isEmpty()) {
            return matchingAgents;
        }
        if (agents.size() == 1) {
            return List.of(agents.get(0));
        }
        throw new IllegalArgumentException("No specialized agent matches request: " + userRequest);
    }

    public LlmResponse execute(String userRequest) {
        return execute(userRequest, null);
    }

    public LlmResponse execute(String userRequest, String context) {
        List<LlmResponse> results = selectAgents(userRequest).stream()
                .map(agent -> context == null
                        ? agent.execute(userRequest)
                        : agent.execute(userRequest, context))
                .toList();

        if (results.size() == 1) {
            return results.get(0);
        }
        if (llmService == null) {
            throw new IllegalStateException("LlmService is required to combine multiple agent results.");
        }
        return llmService.generateAnswer(userRequest, List.of(), results);
    }

    public List<LlmResponse> delegate(String userRequest, String context) {
        return selectAgents(userRequest).stream()
                .map(agent -> agent.execute(userRequest, context))
                .toList();
    }

    public List<SpecializedAgent> getAgents() {
        return List.copyOf(agents);
    }

    private boolean hasCapabilityMatch(String normalizedRequest, String capabilityDescription) {
        if (capabilityDescription == null || capabilityDescription.isBlank()) {
            return false;
        }
        for (String capabilityWord : capabilityDescription.toLowerCase(Locale.ROOT).split("\\W+")) {
            if (capabilityWord.length() > 3 && normalizedRequest.contains(capabilityWord)) {
                return true;
            }
        }
        return false;
    }
}