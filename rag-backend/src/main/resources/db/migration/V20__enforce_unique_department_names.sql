ALTER TABLE departments
    ALTER COLUMN name SET NOT NULL;

CREATE UNIQUE INDEX uq_departments_name_lower
    ON departments (LOWER(name));
