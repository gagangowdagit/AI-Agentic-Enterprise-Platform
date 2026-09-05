ALTER TABLE documents DROP CONSTRAINT IF EXISTS fk_documents_project;
ALTER TABLE memories DROP CONSTRAINT IF EXISTS memories_project_id_fkey;
ALTER TABLE tasks DROP CONSTRAINT IF EXISTS tasks_project_id_fkey;

CREATE TEMPORARY TABLE project_id_mapping AS
SELECT id AS old_id,
       ROW_NUMBER() OVER (ORDER BY id)::BIGINT AS new_id
FROM projects;

UPDATE documents d
SET project_id = m.new_id::VARCHAR
FROM project_id_mapping m
WHERE d.project_id = m.old_id;

UPDATE memories mry
SET project_id = m.new_id::VARCHAR
FROM project_id_mapping m
WHERE mry.project_id = m.old_id;

UPDATE tasks t
SET project_id = m.new_id::VARCHAR
FROM project_id_mapping m
WHERE t.project_id = m.old_id;

UPDATE agent_executions ae
SET project_id = m.new_id::VARCHAR
FROM project_id_mapping m
WHERE ae.project_id = m.old_id;

UPDATE projects p
SET id = 'legacy-' || m.new_id::VARCHAR
FROM project_id_mapping m
WHERE p.id = m.old_id;

UPDATE projects p
SET id = m.new_id::VARCHAR
FROM project_id_mapping m
WHERE p.id = 'legacy-' || m.new_id::VARCHAR;

CREATE SEQUENCE projects_id_seq;

ALTER TABLE projects
    ALTER COLUMN id TYPE BIGINT USING id::BIGINT;

SELECT setval('projects_id_seq', COALESCE((SELECT MAX(id) FROM projects), 1), true);
ALTER TABLE projects ALTER COLUMN id SET DEFAULT nextval('projects_id_seq');

ALTER TABLE projects
    ADD CONSTRAINT projects_id_positive CHECK (id > 0);
