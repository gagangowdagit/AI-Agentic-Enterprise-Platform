# AI, RAG, and Agent Design

## Current AI Components

The current implementation uses small service abstractions around local Ollama execution:

- `EmbeddingService` sends text batches to the Ollama embedding endpoint and normalizes returned vectors.
- `LlmService` generates grounded responses from retrieved context.
- `RagService` scopes documents to a project, loads relevant chunks, and delegates similarity selection.
- `AgentService` and related agent classes coordinate workflow execution and application-level tool usage.

The configured local models are `nomic-embed-text` and `llama3.2:3b`. Ollama must be available locally for live AI flows.

## Current RAG Pipeline

```text
Document -> extract text -> chunk -> embed -> persist
Question -> embed -> project scope -> similarity top-K
         -> grounded prompt -> LLM -> answer + context
```

The model is instructed to stay grounded in project context and avoid asserting unsupported facts. This is a practical answer-generation control, not a guarantee of factual correctness.

## Current Agent Workflow

The project includes agent-style orchestration for project-aware AI operations:

```text
Request -> classify -> validate -> use project tools/services -> respond
```

This is real application orchestration, but it is still scoped to the current domain and not a fully autonomous multi-agent platform.

## Final Scope Boundaries

- no long-lived durable conversation storage yet
- no streaming response or live token output
- no external vector database at production scale
- no advanced retrieval evaluation harness yet
- agent tools run within the application's implemented service boundaries; project/resource-level authorization is not provided by the current JWT principal

## Safety and Quality Boundaries

The project contains a real AI/RAG implementation and bounded agent workflow patterns. Generated responses can still be inaccurate, model availability is required for live AI operations, and prompt/retrieval quality has not been validated as a production security control. This final deliverable is a local application, not a fully autonomous or production enterprise AI system.
