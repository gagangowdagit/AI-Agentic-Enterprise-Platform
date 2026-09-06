package com.rag.ragbackend.repository;

import com.rag.ragbackend.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByProjectId(Integer projectId);
}
