package com.wordcloud.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    private String id;
    private String fileName;
    private String status;
    private LocalDateTime createdAt;

    @Column(name = "total_chunks")
    private Integer totalChunks = 0;

    @Column(name = "processed_chunks")
    private Integer processedChunks = 0;

    public void setId(String id) {
        this.id = id;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setTotalChunks(Integer totalChunks) {
        this.totalChunks = totalChunks;
    }

    public void setProcessedChunks(Integer processedChunks) {
        this.processedChunks = processedChunks;
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Integer getTotalChunks() {
        return totalChunks;
    }

    public Integer getProcessedChunks() {
        return processedChunks;
    }
}
