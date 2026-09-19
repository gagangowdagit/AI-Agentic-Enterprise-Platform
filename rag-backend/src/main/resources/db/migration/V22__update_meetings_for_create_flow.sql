ALTER TABLE meetings
    ADD COLUMN IF NOT EXISTS agenda TEXT,
    ADD COLUMN IF NOT EXISTS created_by VARCHAR(255),
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

CREATE TABLE IF NOT EXISTS meeting_participants (
    meeting_id BIGINT NOT NULL,
    employee_id INTEGER NOT NULL,
    PRIMARY KEY (meeting_id, employee_id),
    CONSTRAINT fk_meeting_participants_meeting FOREIGN KEY (meeting_id) REFERENCES meetings(id) ON DELETE CASCADE,
    CONSTRAINT fk_meeting_participants_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);
