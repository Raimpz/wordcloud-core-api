package com.wordcloud.core.service;

import com.wordcloud.core.entity.Document;
import com.wordcloud.core.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public String processAndSaveDocument(MultipartFile file) {
        String uniqueId = UUID.randomUUID().toString();

        Document doc = new Document();
        doc.setId(uniqueId);
        doc.setFileName(file.getOriginalFilename());
        doc.setStatus("PENDING");
        doc.setCreatedAt(LocalDateTime.now());

        documentRepository.save(doc);

        return uniqueId;
    }
}
