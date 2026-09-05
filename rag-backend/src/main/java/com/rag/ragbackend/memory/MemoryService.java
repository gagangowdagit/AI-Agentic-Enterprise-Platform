package com.rag.ragbackend.memory;

import java.util.List;

public interface MemoryService {

    Memory saveMemory(Memory memory);

    List<Memory> getMemoriesByProjectId(String projectId);

    List<Memory> getRelevantMemories(String projectId, String query);
}