package com.wordcloud.core.service;

import com.wordcloud.core.entity.Document;
import com.wordcloud.core.entity.WordCount;
import com.wordcloud.core.repository.DocumentRepository;
import com.wordcloud.core.repository.WordCountRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final WordCountRepository wordCountRepository;
    private final FileProcessingService fileProcessingService;

    public DocumentService(DocumentRepository documentRepository, WordCountRepository wordCountRepository, FileProcessingService fileProcessingService) {
        this.documentRepository = documentRepository;
        this.wordCountRepository = wordCountRepository;
        this.fileProcessingService = fileProcessingService;
    }

    public String processAndSaveDocument(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();

        if (filename == null || !filename.toLowerCase().endsWith(".txt")) {
            throw new IllegalArgumentException("Only .txt files are allowed");
        }

        String uniqueId = UUID.randomUUID().toString();

        Path tempFile = Files.createTempFile("wordcloud-", ".txt");
        file.transferTo(tempFile.toFile());

        Document doc = new Document();
        doc.setId(uniqueId);
        doc.setFileName(filename);
        doc.setStatus("PENDING");
        doc.setCreatedAt(LocalDateTime.now());

        documentRepository.save(doc);

        fileProcessingService.processFileAsync(uniqueId, tempFile);

        return uniqueId;
    }

    public Document getDocumentStatus(String documentId) {
        return documentRepository.findById(documentId).orElseThrow(
            () -> new RuntimeException("Document not found: " + documentId)
        );
    }

    public List<WordCount> getWordStatistics(String documentId) {
        return wordCountRepository.findByDocumentId(documentId);
    }

}
