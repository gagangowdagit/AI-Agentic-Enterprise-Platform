SELECT setval(pg_get_serial_sequence('departments', 'id'), COALESCE((SELECT MAX(id) + 1 FROM departments), 1), false);
SELECT setval(pg_get_serial_sequence('projects', 'id'), COALESCE((SELECT MAX(id) + 1 FROM projects), 1), false);
SELECT setval(pg_get_serial_sequence('employees', 'id'), COALESCE((SELECT MAX(id) + 1 FROM employees), 1), false);
SELECT setval(pg_get_serial_sequence('documents', 'id'), COALESCE((SELECT MAX(id) + 1 FROM documents), 1), false);
SELECT setval(pg_get_serial_sequence('meetings', 'id'), COALESCE((SELECT MAX(id) + 1 FROM meetings), 1), false);
SELECT setval(pg_get_serial_sequence('notifications', 'id'), COALESCE((SELECT MAX(id) + 1 FROM notifications), 1), false);
SELECT setval(pg_get_serial_sequence('users', 'id'), COALESCE((SELECT MAX(id) + 1 FROM users), 1), false);
