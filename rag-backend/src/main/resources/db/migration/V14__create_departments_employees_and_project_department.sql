CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT
);

ALTER TABLE projects
    ADD COLUMN department_id BIGINT,
    ADD CONSTRAINT fk_projects_department
        FOREIGN KEY (department_id) REFERENCES departments(id);

CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    role VARCHAR(255),
    department_id BIGINT,
    project_id BIGINT,
    CONSTRAINT fk_employees_department
        FOREIGN KEY (department_id) REFERENCES departments(id),
    CONSTRAINT fk_employees_project
        FOREIGN KEY (project_id) REFERENCES projects(id)
);