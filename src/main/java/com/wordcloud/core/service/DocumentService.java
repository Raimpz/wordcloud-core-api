package com.wordcloud.core.service;

import com.wordcloud.core.config.RabbitMQConfig;
import com.wordcloud.core.dto.TextMessagePayload;
import com.wordcloud.core.entity.Document;
import com.wordcloud.core.entity.WordCount;
import com.wordcloud.core.repository.DocumentRepository;
import com.wordcloud.core.repository.WordCountRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final RabbitTemplate rabbitTemplate;
    private final WordCountRepository wordCountRepository;

    public DocumentService(DocumentRepository documentRepository, RabbitTemplate rabbitTemplate, WordCountRepository wordCountRepository) {
        this.documentRepository = documentRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.wordCountRepository = wordCountRepository;
    }

    public String processAndSaveDocument(MultipartFile file) throws Exception {
        String uniqueId = UUID.randomUUID().toString();

        Document doc = new Document();
        doc.setId(uniqueId);
        doc.setFileName(file.getOriginalFilename());
        doc.setStatus("PENDING");
        doc.setCreatedAt(LocalDateTime.now());

        documentRepository.save(doc);

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)))
        {
            String line;

            while ((line = reader.readLine()) != null) {
                boolean isLineEmpty = line.trim().isEmpty();

                if (!isLineEmpty) {
                    TextMessagePayload payload = new TextMessagePayload(uniqueId, line);

                    rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, payload);
                }
            }
        } catch (Exception e) {
            doc.setStatus("ERROR");

            documentRepository.save(doc);

            throw new Exception("Failed to process file", e);
        }

        return uniqueId;
    }

    public List<WordCount> getWordStatistics(String documentId) {
        return wordCountRepository.findByDocumentId(documentId);
    }

}
