ALTER TABLE meetings
    ADD COLUMN IF NOT EXISTS transcript_text TEXT,
    ADD COLUMN IF NOT EXISTS transcript_file_name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS transcript_uploaded_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS ai_summary TEXT,
    ADD COLUMN IF NOT EXISTS ai_action_items TEXT,
    ADD COLUMN IF NOT EXISTS ai_decisions TEXT;
