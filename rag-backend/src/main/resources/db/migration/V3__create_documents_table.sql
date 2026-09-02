CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    project_id VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100),
    file_size BIGINT,
    file_path VARCHAR(500),
    uploaded_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_documents_project FOREIGN KEY (project_id) REFERENCES projects(id)
);

CREATE INDEX idx_documents_project_id ON documents(project_id);
