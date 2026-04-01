package com.wordcloud.core.service;

import com.wordcloud.core.entity.WordCount;
import com.wordcloud.core.repository.WordCountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WordCountService {

    private final WordCountRepository wordCountRepository;

    public WordCountService(WordCountRepository wordCountRepository) {
        this.wordCountRepository = wordCountRepository;
    }

    @Transactional
    public WordCount updateWord(String documentId, Integer wordId, String newWord) {
        String normalizedWord = newWord.trim().toLowerCase();

        if (normalizedWord.isEmpty() || normalizedWord.length() > 50) {
            throw new IllegalArgumentException("Word must be between 1 and 50 characters");
        }

        WordCount existing = wordCountRepository.findById(wordId).orElseThrow(() -> new RuntimeException("Word entry not found: " + wordId));

        if (!existing.getDocumentId().equals(documentId)) {
            throw new RuntimeException("Word does not belong to document: " + documentId);
        }

        if (existing.getWord().equals(normalizedWord)) {
            return existing;
        }

        boolean duplicate = wordCountRepository.findByDocumentIdAndWord(documentId, normalizedWord)
                .filter(w -> !w.getId().equals(wordId))
                .isPresent();

        if (duplicate) {
            throw new IllegalArgumentException("Word '" + normalizedWord + "' already exists in this document");
        }

        existing.setWord(normalizedWord);
        wordCountRepository.save(existing);

        return existing;
    }

    @Transactional
    public void deleteWord(String documentId, Integer wordId) {
        WordCount existing = wordCountRepository.findById(wordId).orElseThrow(() -> new RuntimeException("Word entry not found: " + wordId));

        if (!existing.getDocumentId().equals(documentId)) {
            throw new RuntimeException("Word does not belong to document: " + documentId);
        }

        wordCountRepository.delete(existing);
    }

}
