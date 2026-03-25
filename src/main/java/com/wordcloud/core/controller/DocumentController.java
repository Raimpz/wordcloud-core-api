package com.wordcloud.core.controller;

import com.wordcloud.core.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to upload file");
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
