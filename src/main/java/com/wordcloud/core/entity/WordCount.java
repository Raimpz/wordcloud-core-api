package com.wordcloud.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "word_counts")
public class WordCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "document_id")
    private String documentId;

    private String word;
    private Integer count;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getId() {
        return id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getWord() {
        return word;
    }

    public Integer getCount() {
        return count;
    }
}
