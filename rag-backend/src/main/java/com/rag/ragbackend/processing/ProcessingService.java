package com.rag.ragbackend.processing;

import com.rag.ragbackend.entity.DocumentChunk;

import java.util.List;

public interface ProcessingService {

    String processDocument(Long documentId);

    List<String> chunkDocumentText(Long documentId);

    List<DocumentChunk> getDocumentChunks(Long documentId);
}
