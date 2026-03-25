package com.wordcloud.core.repository;

import com.wordcloud.core.entity.WordCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordCountRepository extends JpaRepository<WordCount, Integer> {
    List<WordCount> findByDocumentId(String documentId);
}
