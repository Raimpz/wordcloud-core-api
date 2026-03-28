CREATE TABLE documents (
    id VARCHAR(255) PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE word_counts (
    id SERIAL PRIMARY KEY,
    document_id VARCHAR(255) NOT NULL,
    word VARCHAR(255) NOT NULL,
    count INTEGER NOT NULL,
    CONSTRAINT fk_document FOREIGN KEY(document_id) REFERENCES documents(id),
    CONSTRAINT uq_document_word UNIQUE (document_id, word)
);