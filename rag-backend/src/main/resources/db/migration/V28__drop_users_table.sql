ALTER TABLE notifications DROP CONSTRAINT IF EXISTS notifications_user_id_fkey;
ALTER TABLE tasks DROP CONSTRAINT IF EXISTS tasks_assigned_user_id_fkey;

ALTER TABLE notifications DROP COLUMN IF EXISTS user_id;
ALTER TABLE tasks DROP COLUMN IF EXISTS assigned_user_id;

DROP TABLE IF EXISTS users CASCADE;
