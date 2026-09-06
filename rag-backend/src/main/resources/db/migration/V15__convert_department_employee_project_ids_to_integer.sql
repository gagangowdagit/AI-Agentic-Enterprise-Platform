ALTER TABLE employees
    DROP CONSTRAINT fk_employees_department,
    DROP CONSTRAINT fk_employees_project;

ALTER TABLE projects
    DROP CONSTRAINT fk_projects_department;

ALTER TABLE departments
    ALTER COLUMN id TYPE INTEGER USING id::INTEGER;

ALTER TABLE projects
    ALTER COLUMN id TYPE INTEGER USING id::INTEGER,
    ALTER COLUMN department_id TYPE INTEGER USING department_id::INTEGER;

ALTER TABLE employees
    ALTER COLUMN id TYPE INTEGER USING id::INTEGER,
    ALTER COLUMN department_id TYPE INTEGER USING department_id::INTEGER,
    ALTER COLUMN project_id TYPE INTEGER USING project_id::INTEGER;

ALTER SEQUENCE departments_id_seq AS INTEGER;
ALTER SEQUENCE employees_id_seq AS INTEGER;
ALTER SEQUENCE projects_id_seq AS INTEGER;

ALTER TABLE projects
    ADD CONSTRAINT fk_projects_department
        FOREIGN KEY (department_id) REFERENCES departments(id);

ALTER TABLE employees
    ADD CONSTRAINT fk_employees_department
        FOREIGN KEY (department_id) REFERENCES departments(id),
    ADD CONSTRAINT fk_employees_project
        FOREIGN KEY (project_id) REFERENCES projects(id);