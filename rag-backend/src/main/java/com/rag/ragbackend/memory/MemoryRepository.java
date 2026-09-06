package com.rag.ragbackend.memory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoryRepository extends JpaRepository<Memory, String> {
    List<Memory> findByProjectIdOrderByCreatedAtAsc(Integer projectId);
}