ALTER TABLE chunk_embeddings
    DROP CONSTRAINT IF EXISTS fk_chunk_embeddings_chunk,
    ADD CONSTRAINT fk_chunk_embeddings_chunk
        FOREIGN KEY (chunk_id) REFERENCES document_chunks(id) ON DELETE CASCADE;
