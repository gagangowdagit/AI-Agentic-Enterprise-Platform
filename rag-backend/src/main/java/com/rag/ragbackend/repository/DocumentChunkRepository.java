package com.rag.ragbackend.repository;

import com.rag.ragbackend.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {

    List<DocumentChunk> findByDocumentIdOrderByChunkIndexAsc(Long documentId);

    List<DocumentChunk> findByDocumentId(Long documentId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("delete from DocumentChunk dc where dc.documentId = :documentId")
    void deleteByDocumentId(Long documentId);
}
