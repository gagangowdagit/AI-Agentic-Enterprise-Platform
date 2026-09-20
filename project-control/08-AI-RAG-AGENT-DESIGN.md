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

## Current Limitations

- no long-lived durable conversation storage yet
- no streaming response or live token output
- no external vector database at production scale
- no advanced retrieval evaluation harness yet
- no production-grade tool authorization policy yet

## Current Safety and Quality Direction

The project is moving toward the following hardening priorities:

- stronger route-level authorization checks
- prompt and retrieval safety reviews
- better failure handling for missing models and malformed AI responses
- more robust evaluation for grounding quality
- auditable tool execution boundaries for future agent expansion

This is the honest current state: the project contains a real AI/RAG architecture and agentic workflow patterns, but it is still a bounded, auditable local application rather than a fully autonomous enterprise AI system.
