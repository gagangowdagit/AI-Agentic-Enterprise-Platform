ALTER TABLE projects
    ADD COLUMN description TEXT,
    ADD COLUMN start_date DATE,
    ADD COLUMN end_date DATE,
    ADD COLUMN priority VARCHAR(10);

UPDATE projects
SET priority = 'MEDIUM'
WHERE priority IS NULL;

ALTER TABLE projects
    ALTER COLUMN priority SET DEFAULT 'MEDIUM',
    ALTER COLUMN priority SET NOT NULL,
    ADD CONSTRAINT projects_priority_check CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH'));
