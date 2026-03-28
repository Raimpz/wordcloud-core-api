package com.wordcloud.core.repository;

import com.wordcloud.core.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE documents SET status = 'ERROR' WHERE id = :documentId", nativeQuery = true)
    void markAsError(@Param("documentId") String documentId);
}
