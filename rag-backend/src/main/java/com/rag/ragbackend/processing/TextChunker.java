package com.rag.ragbackend.processing;

import java.util.List;

public interface TextChunker {

    List<String> chunkText(String text);
}
