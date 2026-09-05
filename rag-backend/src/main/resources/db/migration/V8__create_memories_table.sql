CREATE TABLE memories (
    id VARCHAR(36) PRIMARY KEY,
    project_id VARCHAR(50) NOT NULL REFERENCES projects(id),
    content TEXT NOT NULL,
    memory_type VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_memories_project_id_created_at
    ON memories (project_id, created_at);