package com.wordcloud.core.controller;

import com.wordcloud.core.entity.Document;
import com.wordcloud.core.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String documentId = documentService.processAndSaveDocument(file);

            return ResponseEntity.ok(documentId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid file: only .txt files are allowed");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to upload file");
        }
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<?> getStatus(@PathVariable("id") String documentId) {
        try {
            Document doc = documentService.getDocumentStatus(documentId);
            return ResponseEntity.ok(Map.of(
                    "id", doc.getId(),
                    "status", doc.getStatus(),
                    "totalChunks", doc.getTotalChunks(),
                    "processedChunks", doc.getProcessedChunks()
            ));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<?> getStatistics(@PathVariable("id") String documentId) {
        try {
            return ResponseEntity.ok(documentService.getWordStatistics(documentId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to retrieve statistics");
        }
    }
}
