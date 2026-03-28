package com.wordcloud.core.service;

import com.wordcloud.core.config.RabbitMQConfig;
import com.wordcloud.core.dto.TextMessagePayload;
import com.wordcloud.core.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileProcessingService {

    private static final Logger log = LoggerFactory.getLogger(FileProcessingService.class);
    private static final int CHUNK_SIZE_64KB = 64 * 1024;

    private final RabbitTemplate rabbitTemplate;
    private final DocumentRepository documentRepository;

    public FileProcessingService(RabbitTemplate rabbitTemplate, DocumentRepository documentRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.documentRepository = documentRepository;
    }

    @Async("fileProcessingExecutor")
    public void processFileAsync(String documentId, Path tempFile) {
        int chunkCount = 0;

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(Files.newInputStream(tempFile),
            StandardCharsets.UTF_8)
        )) {

            char[] readBuffer = new char[CHUNK_SIZE_64KB];
            StringBuilder incompleteWord = new StringBuilder();
            int numberOfCharsRead;

            while ((numberOfCharsRead = reader.read(readBuffer, 0, readBuffer.length)) != -1) {
                String currentTextChunk = incompleteWord + new String(readBuffer, 0, numberOfCharsRead);
                boolean isFullChunk = numberOfCharsRead == readBuffer.length;
                incompleteWord.setLength(0);

                if (isFullChunk) {
                    int lastWordBoundaryIndex = currentTextChunk.length() - 1;

                    while (lastWordBoundaryIndex > 0 && Character.isLetterOrDigit(currentTextChunk.charAt(lastWordBoundaryIndex))) {
                        lastWordBoundaryIndex--;
                    }

                    boolean wordWasSplitAtBoundary = lastWordBoundaryIndex < currentTextChunk.length() - 1;

                    if (wordWasSplitAtBoundary) {
                        incompleteWord.append(currentTextChunk, lastWordBoundaryIndex + 1, currentTextChunk.length());
                        currentTextChunk = currentTextChunk.substring(0, lastWordBoundaryIndex + 1);
                    }
                }

                if (!currentTextChunk.trim().isEmpty()) {
                    TextMessagePayload payload = new TextMessagePayload(documentId, currentTextChunk);
                    rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, payload);

                    chunkCount++;
                }
            }

            if (incompleteWord.length() > 0 && !incompleteWord.toString().trim().isEmpty()) {
                TextMessagePayload payload = new TextMessagePayload(documentId, incompleteWord.toString());
                rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, payload);

                chunkCount++;
            }

            log.info("Document {} queued {} chunks for processing", documentId, chunkCount);

        } catch (Exception e) {
            log.error("Failed to process document {}", documentId, e);
            documentRepository.markAsError(documentId);
        } finally {
            try {
                Files.deleteIfExists(tempFile);
            } catch (Exception ignored) {
            }
        }
    }
}
