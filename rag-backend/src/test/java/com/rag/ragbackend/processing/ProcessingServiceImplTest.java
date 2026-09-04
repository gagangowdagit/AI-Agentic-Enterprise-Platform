package com.rag.ragbackend.processing;

import com.rag.ragbackend.entity.Document;
import com.rag.ragbackend.entity.DocumentChunk;
import com.rag.ragbackend.entity.ChunkEmbedding;
import com.rag.ragbackend.repository.DocumentChunkRepository;
import com.rag.ragbackend.repository.ChunkEmbeddingRepository;
import com.rag.ragbackend.repository.DocumentRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class ProcessingServiceImplTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldChunkLongTextIntoMultiplePiecesWithOverlap() {
        TextChunker textChunker = new DefaultTextChunker();
        String longText = "This is sentence one. ".repeat(120);

        List<String> chunks = textChunker.chunkText(longText);

        assertTrue(chunks.size() > 1);
        assertTrue(chunks.stream().allMatch(chunk -> chunk.length() <= 1000));
        assertTrue(chunks.stream().allMatch(chunk -> chunk.length() >= 500));

        boolean hasOverlap = false;
        for (int i = 1; i < chunks.size(); i++) {
            String previous = chunks.get(i - 1);
            String current = chunks.get(i);
            String overlap = previous.substring(Math.max(0, previous.length() - 100));
            if (current.startsWith(overlap)) {
                hasOverlap = true;
                break;
            }
        }

        assertTrue(hasOverlap);
    }

    @Test
    void shouldKeepShortTextAsSingleChunk() {
        TextChunker textChunker = new DefaultTextChunker();

        List<String> chunks = textChunker.chunkText("Short text example.");

        assertEquals(List.of("Short text example."), chunks);
    }

    @Test
    void shouldChunkExtractedTextAfterProcessingDocument() throws IOException {
        Path txtFile = tempDir.resolve("long-notes.txt");
        String longText = "This is sentence one. ".repeat(120);
        Files.writeString(txtFile, longText);

        DocumentRepository documentRepository = Mockito.mock(DocumentRepository.class);
        ProcessingService processingService = new ProcessingServiceImpl(
                documentRepository,
                new TxtTextExtractor(),
                new PdfTextExtractor(),
                new DocxTextExtractor(),
                new DefaultTextChunker()
        );

        Document document = new Document();
        document.setId(21L);
        document.setFileName("long-notes.txt");
        document.setFileType("text/plain");
        document.setFilePath(txtFile.toString());

        Mockito.when(documentRepository.findById(21L)).thenReturn(Optional.of(document));

        List<String> chunks = processingService.chunkDocumentText(21L);

        assertTrue(chunks.size() > 1);
        assertEquals(longText, document.getExtractedText());
    }

    @Test
    void shouldPersistChunksAndRemoveExistingOnReprocess() throws IOException {
        Path txtFile = tempDir.resolve("chunked.txt");
        String longText = "This is sentence one. ".repeat(120);
        Files.writeString(txtFile, longText);

        DocumentRepository documentRepository = Mockito.mock(DocumentRepository.class);
        DocumentChunkRepository documentChunkRepository = Mockito.mock(DocumentChunkRepository.class);
        ProcessingService processingService = new ProcessingServiceImpl(
                documentRepository,
                new TxtTextExtractor(),
                new PdfTextExtractor(),
                new DocxTextExtractor(),
                new DefaultTextChunker(),
                documentChunkRepository
        );

        Document document = new Document();
        document.setId(42L);
        document.setFileName("chunked.txt");
        document.setFileType("text/plain");
        document.setFilePath(txtFile.toString());

        Mockito.when(documentRepository.findById(42L)).thenReturn(Optional.of(document));

        processingService.processDocument(42L);

        verify(documentChunkRepository).deleteByDocumentId(42L);

        var captor = forClass(java.util.List.class);
        verify(documentChunkRepository).saveAll(captor.capture());
        List<DocumentChunk> savedChunks = captor.getValue();

        assertTrue(savedChunks.size() > 1);
        assertEquals(0, savedChunks.get(0).getChunkIndex());
        assertEquals(1, savedChunks.get(1).getChunkIndex());
        assertTrue(savedChunks.stream().allMatch(chunk -> chunk.getDocumentId() != null && chunk.getDocumentId().equals(42L)));
        assertEquals(savedChunks.size(), savedChunks.stream().filter(chunk -> chunk.getDocumentId() != null && chunk.getDocumentId().equals(42L)).count());
    }

    @Test
    void shouldRetrieveDocumentChunksInChunkIndexOrder() {
        DocumentChunkRepository documentChunkRepository = Mockito.mock(DocumentChunkRepository.class);
        ProcessingService processingService = new ProcessingServiceImpl(
                Mockito.mock(DocumentRepository.class),
                new TxtTextExtractor(),
                new PdfTextExtractor(),
                new DocxTextExtractor(),
                new DefaultTextChunker(),
                documentChunkRepository
        );
        List<DocumentChunk> chunks = List.of(
                new DocumentChunk(42L, 0, "First"),
                new DocumentChunk(42L, 1, "Second")
        );

        Mockito.when(documentChunkRepository.findByDocumentIdOrderByChunkIndexAsc(42L)).thenReturn(chunks);

        assertEquals(chunks, processingService.getDocumentChunks(42L));
        verify(documentChunkRepository).findByDocumentIdOrderByChunkIndexAsc(42L);
    }

    @Test
    void shouldReturnEmptyListWhenDocumentHasNoChunks() {
        DocumentChunkRepository documentChunkRepository = Mockito.mock(DocumentChunkRepository.class);
        ProcessingService processingService = new ProcessingServiceImpl(
                Mockito.mock(DocumentRepository.class),
                new TxtTextExtractor(),
                new PdfTextExtractor(),
                new DocxTextExtractor(),
                new DefaultTextChunker(),
                documentChunkRepository
        );

        Mockito.when(documentChunkRepository.findByDocumentIdOrderByChunkIndexAsc(99L)).thenReturn(List.of());

        assertTrue(processingService.getDocumentChunks(99L).isEmpty());
    }

    @Test
    void shouldCreateDeterministicEmbeddingForEachChunk() {
        EmbeddingService embeddingService = new DeterministicEmbeddingService();
        List<String> chunks = List.of("First chunk", "Second chunk");

        List<List<Double>> firstResult = embeddingService.embed(chunks);
        List<List<Double>> secondResult = embeddingService.embed(chunks);

        assertEquals(2, firstResult.size());
        assertEquals(8, firstResult.get(0).size());
        assertEquals(firstResult, secondResult);
        assertTrue(firstResult.stream().allMatch(vector -> vector.stream().allMatch(value -> value >= -1.0 && value <= 1.0)));
        assertTrue(embeddingService.embed(List.of()).isEmpty());
    }

    @Test
    void shouldPassGeneratedChunksToEmbeddingServiceDuringProcessing() throws IOException {
        Path txtFile = tempDir.resolve("embedded.txt");
        Files.writeString(txtFile, "Text to embed.");

        DocumentRepository documentRepository = Mockito.mock(DocumentRepository.class);
        DocumentChunkRepository documentChunkRepository = Mockito.mock(DocumentChunkRepository.class);
        EmbeddingService embeddingService = Mockito.mock(EmbeddingService.class);
        ProcessingService processingService = new ProcessingServiceImpl(
                documentRepository,
                new TxtTextExtractor(),
                new PdfTextExtractor(),
                new DocxTextExtractor(),
                new DefaultTextChunker(),
                documentChunkRepository,
                embeddingService
        );
        Document document = new Document();
        document.setId(43L);
        document.setFileName("embedded.txt");
        document.setFileType("text/plain");
        document.setFilePath(txtFile.toString());

        Mockito.when(documentRepository.findById(43L)).thenReturn(Optional.of(document));
        Mockito.when(embeddingService.embed(List.of("Text to embed.")))
            .thenReturn(List.of(List.of(0.1, 0.2)));

        processingService.processDocument(43L);

        verify(embeddingService, times(1)).embed(List.of("Text to embed."));
    }

    @Test
    void shouldPersistEmbeddingsLinkedToSavedChunksAndReplaceThemOnReprocess() throws IOException {
        Path txtFile = tempDir.resolve("persisted-embeddings.txt");
        Files.writeString(txtFile, "Text to persist.");

        DocumentRepository documentRepository = Mockito.mock(DocumentRepository.class);
        DocumentChunkRepository documentChunkRepository = Mockito.mock(DocumentChunkRepository.class);
        ChunkEmbeddingRepository chunkEmbeddingRepository = Mockito.mock(ChunkEmbeddingRepository.class);
        EmbeddingService embeddingService = new DeterministicEmbeddingService();
        ProcessingService processingService = new ProcessingServiceImpl(
                documentRepository,
                new TxtTextExtractor(),
                new PdfTextExtractor(),
                new DocxTextExtractor(),
                new DefaultTextChunker(),
                documentChunkRepository,
                embeddingService,
                chunkEmbeddingRepository
        );
        Document document = new Document();
        document.setId(44L);
        document.setFileName("persisted-embeddings.txt");
        document.setFileType("text/plain");
        document.setFilePath(txtFile.toString());
        DocumentChunk existingChunk = new DocumentChunk(44L, 0, "Old text");
        existingChunk.setId(100L);
        DocumentChunk savedChunk = new DocumentChunk(44L, 0, "Text to persist.");
        savedChunk.setId(101L);

        Mockito.when(documentRepository.findById(44L)).thenReturn(Optional.of(document));
        Mockito.when(documentChunkRepository.saveAll(Mockito.anyList())).thenReturn(List.of(savedChunk));

        processingService.processDocument(44L);

        verify(chunkEmbeddingRepository).deleteByDocumentId(44L);
        var embeddingCaptor = forClass(java.util.List.class);
        verify(chunkEmbeddingRepository).saveAll(embeddingCaptor.capture());
        List<ChunkEmbedding> savedEmbeddings = embeddingCaptor.getValue();
        assertEquals(1, savedEmbeddings.size());
        assertEquals(101L, savedEmbeddings.get(0).getChunkId());
        assertEquals("[-0.6549019607843137,-0.9529411764705882,-0.050980392156862786,-0.28627450980392155,0.7490196078431373,-0.388235294117647,-0.5607843137254902,-0.0980392156862745]", savedEmbeddings.get(0).getEmbedding());
    }

    @Test
    void shouldExtractTextFromTxtDocumentFile() throws IOException {
        Path txtFile = tempDir.resolve("notes.txt");
        Files.writeString(txtFile, "Alpha\nBeta\nGamma");

        DocumentRepository documentRepository = Mockito.mock(DocumentRepository.class);
        ProcessingService processingService = new ProcessingServiceImpl(
                documentRepository,
                new TxtTextExtractor(),
                new PdfTextExtractor(),
                new DocxTextExtractor()
        );

        Document document = new Document();
        document.setId(7L);
        document.setFileName("notes.txt");
        document.setFileType("text/plain");
        document.setFilePath(txtFile.toString());

        Mockito.when(documentRepository.findById(7L)).thenReturn(Optional.of(document));

        String extractedText = processingService.processDocument(7L);

        assertEquals("Alpha\nBeta\nGamma", extractedText);
        verify(documentRepository).save(argThat(savedDocument -> "Alpha\nBeta\nGamma".equals(savedDocument.getExtractedText())));
    }

    @Test
    void shouldExtractTextFromPdfDocumentFile() throws IOException {
        Path pdfFile = tempDir.resolve("report.pdf");
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Hello PDF Extractor");
                contentStream.endText();
            }

            document.save(pdfFile.toFile());
        }

        DocumentRepository documentRepository = Mockito.mock(DocumentRepository.class);
        ProcessingService processingService = new ProcessingServiceImpl(
                documentRepository,
                new TxtTextExtractor(),
                new PdfTextExtractor(),
                new DocxTextExtractor()
        );

        Document document = new Document();
        document.setId(9L);
        document.setFileName("report.pdf");
        document.setFileType("application/pdf");
        document.setFilePath(pdfFile.toString());

        Mockito.when(documentRepository.findById(9L)).thenReturn(Optional.of(document));

        String extractedText = processingService.processDocument(9L);

        assertEquals("Hello PDF Extractor", extractedText.trim());
    }

    @Test
    void shouldExtractTextFromDocxDocumentFile() throws IOException {
        Path docxFile = tempDir.resolve("report.docx");
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.createRun().setText("Hello DOCX Extractor");
            document.write(Files.newOutputStream(docxFile));
        }

        DocumentRepository documentRepository = Mockito.mock(DocumentRepository.class);
        ProcessingService processingService = new ProcessingServiceImpl(
                documentRepository,
                new TxtTextExtractor(),
                new PdfTextExtractor(),
                new DocxTextExtractor()
        );

        Document document = new Document();
        document.setId(11L);
        document.setFileName("report.docx");
        document.setFileType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        document.setFilePath(docxFile.toString());

        Mockito.when(documentRepository.findById(11L)).thenReturn(Optional.of(document));

        String extractedText = processingService.processDocument(11L);

        assertEquals("Hello DOCX Extractor", extractedText.trim());
    }

    @Test
    void shouldReturnMessageWhenDocumentIdIsMissing() {
        ProcessingService processingService = new ProcessingServiceImpl(
                Mockito.mock(DocumentRepository.class),
                new TxtTextExtractor(),
                new PdfTextExtractor(),
                new DocxTextExtractor()
        );

        String status = processingService.processDocument(null);

        assertEquals("Document ID is required for processing.", status);
    }
}
