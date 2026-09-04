package com.rag.ragbackend.processing;

import com.rag.ragbackend.entity.DocumentChunk;

import java.util.List;

public record LlmResponse(String query, String answer, List<DocumentChunk> contextChunks,
						  List<SourceMetadata> sources) {

	public LlmResponse(String query, String answer, List<DocumentChunk> contextChunks) {
		this(query, answer, contextChunks, toSources(contextChunks));
	}

	private static List<SourceMetadata> toSources(List<DocumentChunk> contextChunks) {
		if (contextChunks == null) {
			return List.of();
		}
		return contextChunks.stream()
				.map(chunk -> new SourceMetadata(
						chunk.getDocumentId(), chunk.getDocumentFileName(), chunk.getChunkIndex()))
				.distinct()
				.toList();
	}

	public record SourceMetadata(Long documentId, String fileName, Integer chunkIndex) {
	}
}