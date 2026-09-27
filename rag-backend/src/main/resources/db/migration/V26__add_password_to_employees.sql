ALTER TABLE employees
    ADD COLUMN password VARCHAR(255);

UPDATE employees
SET password = 'admin123'
WHERE password IS NULL OR password = '';
