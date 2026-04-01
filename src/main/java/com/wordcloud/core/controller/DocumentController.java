package com.wordcloud.core.controller;

import com.wordcloud.core.dto.UpdateWordRequest;
import com.wordcloud.core.entity.Document;
import com.wordcloud.core.service.DocumentService;
import com.wordcloud.core.service.WordCountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private static final int MAX_WORD_LENGTH = 50;
    private static final int MAX_MIN_COUNT = 500;

    private final DocumentService documentService;
    private final WordCountService wordCountService;

    public DocumentController(DocumentService documentService, WordCountService wordCountService) {
        this.documentService = documentService;
        this.wordCountService = wordCountService;
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
    public ResponseEntity<?> getStatistics(
            @PathVariable("id") String documentId,
            @RequestParam(value = "minCount", defaultValue = "1") int minCount) {
        try {
            int clampedMinCount = Math.min(Math.max(minCount, 1), MAX_MIN_COUNT);
            return ResponseEntity.ok(documentService.getWordStatistics(documentId, clampedMinCount));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to retrieve statistics");
        }
    }

    @PutMapping("/{docId}/words/{wordId}")
    public ResponseEntity<?> updateWord(
            @PathVariable("docId") String documentId,
            @PathVariable("wordId") Integer wordId,
            @RequestBody UpdateWordRequest request) {
        try {
            String newWord = request.getWord();
            String trimmedWord = newWord.trim();

            if (newWord == null || trimmedWord.isEmpty()) {
                return ResponseEntity.badRequest().body("Word must not be empty");
            }

            boolean containsMultipleWords = trimmedWord.split("\\s+").length > 1;
            boolean containsOnlyNumbers = trimmedWord.matches("[0-9]+");
            boolean containsPunctuation = trimmedWord.contains(".") || trimmedWord.contains(",");
            boolean exceedsMaxLength = trimmedWord.length() > MAX_WORD_LENGTH;

            if (containsMultipleWords) {
                return ResponseEntity.badRequest().body("Only single words are allowed for update");
            }

            if (containsOnlyNumbers) {
                return ResponseEntity.badRequest().body("Only numbers are not allowed");
            }

            if (containsPunctuation) {
                return ResponseEntity.badRequest().body("Word must not contain punctuation or comma");
            }

            if (exceedsMaxLength) {
                return ResponseEntity.badRequest().body("Word must not exceed " + MAX_WORD_LENGTH + " characters");
            }

            return ResponseEntity.ok(wordCountService.updateWord(documentId, wordId, newWord));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update word");
        }
    }

    @DeleteMapping("/{docId}/words/{wordId}")
    public ResponseEntity<?> deleteWord(
            @PathVariable("docId") String documentId,
            @PathVariable("wordId") Integer wordId) {
        try {
            wordCountService.deleteWord(documentId, wordId);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete word");
        }
    }
}
