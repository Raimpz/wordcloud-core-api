package com.wordcloud.core.dto;

public class TextMessagePayload {
    private String documentId;
    private String textChunk;

    public TextMessagePayload() {}

    public TextMessagePayload(String documentId, String textChunk) {
        this.documentId = documentId;
        this.textChunk = textChunk;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setTextChunk(String textChunk) {
        this.textChunk = textChunk;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getTextChunk() {
        return textChunk;
    }
}
