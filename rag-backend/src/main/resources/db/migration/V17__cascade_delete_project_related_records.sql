ALTER TABLE documents
    ALTER COLUMN project_id TYPE INTEGER USING project_id::INTEGER;

ALTER TABLE tasks
    ALTER COLUMN project_id TYPE INTEGER USING project_id::INTEGER;

ALTER TABLE memories
    ALTER COLUMN project_id TYPE INTEGER USING project_id::INTEGER;

ALTER TABLE agent_executions
    ALTER COLUMN project_id TYPE INTEGER USING project_id::INTEGER;

ALTER TABLE documents
    DROP CONSTRAINT IF EXISTS fk_documents_project,
    ADD CONSTRAINT fk_documents_project
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;

ALTER TABLE tasks
    DROP CONSTRAINT IF EXISTS tasks_project_id_fkey,
    ADD CONSTRAINT tasks_project_id_fkey
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;

ALTER TABLE memories
    DROP CONSTRAINT IF EXISTS memories_project_id_fkey,
    ADD CONSTRAINT memories_project_id_fkey
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;

ALTER TABLE employees
    DROP CONSTRAINT IF EXISTS fk_employees_project,
    ADD CONSTRAINT fk_employees_project
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;

ALTER TABLE agent_executions
    DROP CONSTRAINT IF EXISTS agent_executions_project_id_fkey,
    ADD CONSTRAINT agent_executions_project_id_fkey
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;
