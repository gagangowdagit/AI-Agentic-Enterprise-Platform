ALTER TABLE document_chunks
    DROP CONSTRAINT IF EXISTS fk_document_chunks_document,
    ADD CONSTRAINT fk_document_chunks_document
        FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE;
