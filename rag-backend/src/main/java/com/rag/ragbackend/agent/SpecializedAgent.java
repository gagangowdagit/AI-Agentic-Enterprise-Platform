package com.rag.ragbackend.agent;

import com.rag.ragbackend.processing.LlmResponse;

public interface SpecializedAgent {

    String getName();

    String getCapabilityDescription();

    LlmResponse execute(String userRequest);

    default LlmResponse execute(String userRequest, String context) {
        return execute(userRequest);
    }
}