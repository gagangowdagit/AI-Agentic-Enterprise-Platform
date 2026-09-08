# AI, RAG, and Agent Design

## Current AI Components

The current implementation deliberately uses small interfaces around local Ollama services:

- `EmbeddingService` sends text batches to `/api/embed` and normalizes returned vectors.
- `LlmService` sends grounded prompts to `/api/generate` with streaming disabled.
- `RagService` scopes documents, retrieves chunks, and delegates similarity selection.
- Agent/project-analysis services reuse application services rather than bypassing business rules.

Configured local models are `nomic-embed-text` and `llama3.2:3b`. Ollama must be running separately; model availability is an environment prerequisite.

## Current RAG Pipeline

```text
Document -> extract text -> chunk -> embed -> persist
Question -> embed -> project scope -> similarity top-K
         -> grounded prompt -> Ollama generate -> answer + sources
```

The prompt instructs the model to use retrieved context and say it does not know when the documents do not support an answer. This is a grounding control, not a proof of factual correctness.

## Current Limitations

- No durable conversation history or token-aware memory window.
- No streaming response.
- No external vector index, retrieval evaluation harness, or reranker.
- No complete model health/readiness endpoint.
- Agent execution and tool authorization need a production-grade contract.

## Agent Roadmap

Agents should be introduced as bounded application workflows:

```text
Request -> classify -> authorize -> validate tool input -> execute
        -> audit result -> optionally ask for confirmation -> respond
```

Each tool must define a name, version, input schema, output schema, permission, timeout, retry policy, idempotency behavior, and audit event. Mutating tools must require explicit confirmation until policy and testing demonstrate otherwise.

Future specialized capabilities may include document search, project/task lookup, analytics, report generation, and task actions. A supervisor or multi-agent design is optional; a deterministic workflow is preferable when it is easier to audit.

## AI Quality and Safety Backlog

- Build a small golden dataset for retrieval and answer grounding.
- Measure retrieval relevance, answer support, latency, and model failure rate.
- Add prompt-injection tests using untrusted document content.
- Redact secrets and personal data from logs.
- Enforce project authorization before retrieval, not only in the prompt.
- Add timeout, retry, circuit-breaker, and degraded-mode behavior.
- Persist prompt/model versions for reproducibility.
