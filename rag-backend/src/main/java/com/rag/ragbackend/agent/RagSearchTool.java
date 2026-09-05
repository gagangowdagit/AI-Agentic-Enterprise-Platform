package com.rag.ragbackend.agent;

import com.rag.ragbackend.entity.DocumentChunk;
import com.rag.ragbackend.processing.RagService;

import java.util.List;
import java.util.Map;

public class RagSearchTool implements Tool {

    private final RagService ragService;

    public RagSearchTool(RagService ragService) {
        this.ragService = ragService;
    }

    @Override
    public String getName() {
        return "rag-search";
    }

    @Override
    public String getDescription() {
        return "Searches project knowledge using the existing RAG service.";
    }

    @Override
    public List<ToolArgument> getInputDefinition() {
        return List.of(
                new SimpleToolArgument("projectId", "The project identifier to search.", true),
                new SimpleToolArgument("query", "The user question to retrieve project context for.", true),
                new SimpleToolArgument("topK", "The number of relevant chunks to return.", false)
        );
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        String projectId = arguments == null ? null : String.valueOf(arguments.getOrDefault("projectId", ""));
        String query = arguments == null ? null : String.valueOf(arguments.getOrDefault("query", ""));
        Integer topK = arguments == null ? 5 : (Integer) arguments.getOrDefault("topK", 5);

        if (projectId == null || projectId.isBlank()) {
            throw new IllegalArgumentException("Project ID is required for rag-search tool.");
        }
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Query is required for rag-search tool.");
        }

        List<DocumentChunk> chunks = ragService.retrieveRelevantChunks(projectId, query, topK);
        return chunks;
    }

    private record SimpleToolArgument(String name, String description, boolean required) implements ToolArgument {
        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean isRequired() {
            return required;
        }
    }
}
