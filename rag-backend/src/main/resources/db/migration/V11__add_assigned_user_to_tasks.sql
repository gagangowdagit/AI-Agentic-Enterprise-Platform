ALTER TABLE tasks ADD COLUMN assigned_user_id BIGINT REFERENCES users(id);

CREATE INDEX idx_tasks_assigned_user_id ON tasks(assigned_user_id);