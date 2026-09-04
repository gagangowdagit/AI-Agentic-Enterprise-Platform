package com.rag.ragbackend.repository;

import com.rag.ragbackend.entity.ChunkEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface ChunkEmbeddingRepository extends JpaRepository<ChunkEmbedding, Long> {

    @Modifying
    @Transactional
    @Query("delete from ChunkEmbedding ce where ce.chunkId in (select dc.id from DocumentChunk dc where dc.documentId = :documentId)")
    void deleteByDocumentId(Long documentId);

    @Modifying
    @Transactional
    @Query("delete from ChunkEmbedding ce where ce.chunkId in :chunkIds")
    void deleteByChunkIds(Collection<Long> chunkIds);
}