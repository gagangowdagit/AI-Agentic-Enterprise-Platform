CREATE TABLE chunk_embeddings (
    id BIGSERIAL PRIMARY KEY,
    chunk_id BIGINT NOT NULL,
    embedding TEXT NOT NULL,
    CONSTRAINT fk_chunk_embeddings_chunk FOREIGN KEY (chunk_id) REFERENCES document_chunks(id)
);