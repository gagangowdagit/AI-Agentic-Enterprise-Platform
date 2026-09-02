package com.rag.ragbackend.service;

import com.rag.ragbackend.entity.Document;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    List<Document> getDocumentsByProjectId(String projectId);

    Document getDocumentById(Long id);

    Document uploadDocument(String projectId, MultipartFile file);

    Resource downloadDocument(Long id);

    boolean deleteDocument(Long id);

    Document createDocument(Document document);
}
