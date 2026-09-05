package com.rag.ragbackend.execution;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentExecutionRepository extends JpaRepository<AgentExecution, String> {
}