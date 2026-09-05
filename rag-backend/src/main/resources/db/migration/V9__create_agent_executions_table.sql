CREATE TABLE agent_executions (
    execution_id VARCHAR(36) PRIMARY KEY,
    project_id VARCHAR(50),
    status VARCHAR(30) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    current_step VARCHAR(255)
);

CREATE INDEX idx_agent_executions_project_id
    ON agent_executions (project_id);