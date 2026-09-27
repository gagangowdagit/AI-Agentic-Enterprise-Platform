ALTER TABLE users ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'EMPLOYEE';

UPDATE users
SET role = CASE email
    WHEN 'avery.collins@northstar.io' THEN 'DIRECTOR'
    WHEN 'priya.nair@northstar.io' THEN 'CEO'
    WHEN 'daniel.brooks@northstar.io' THEN 'MANAGER'
    WHEN 'sofia.ramirez@northstar.io' THEN 'EMPLOYEE'
    ELSE 'EMPLOYEE'
END
WHERE role IS NULL OR role = '';
