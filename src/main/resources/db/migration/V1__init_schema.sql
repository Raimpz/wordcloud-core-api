CREATE TABLE documents (
    id VARCHAR(255) PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_chunks INTEGER NOT NULL DEFAULT 0,
    processed_chunks INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE word_counts (
    id SERIAL PRIMARY KEY,
    document_id VARCHAR(255) NOT NULL,
    word VARCHAR(50) NOT NULL,
    count INTEGER NOT NULL,
    CONSTRAINT fk_document FOREIGN KEY(document_id) REFERENCES documents(id),
    CONSTRAINT uq_document_word UNIQUE (document_id, word)
);

CREATE INDEX idx_word_counts_document_count ON word_counts (document_id, count);
