# AI Development Instructions

## 1. Project Context

This repository contains the AI Agentic Enterprise Platform.

The project is being developed incrementally using a predefined development plan, with Git used for version control and rollback.

The repository is the primary source of truth for the project.

---

## 2. Mandatory Files to Read

Before making any code changes, the AI assistant must read the relevant files inside:

`project-control/`

At minimum:

1. `01-PROJECT-OVERVIEW.md`
2. `02-TECH-STACK.md`
3. `03-DEVELOPMENT-PLAN.md`
4. `04-REQUIREMENTS.md`
5. `05-ARCHITECTURE.md`
6. `10-DEVELOPMENT-STATUS.md`
7. `11-DECISIONS.md`

---

## 3. Development Rules

The AI assistant must:

- Follow the defined project architecture.
- Follow the approved technology stack.
- Follow the development hierarchy.
- Work only on the current task unless explicitly instructed otherwise.
- Complete the current task before moving to the next task.
- Avoid modifying unrelated modules.
- Avoid introducing unnecessary technologies.
- Avoid rewriting working code without a valid reason.
- Preserve existing functionality when adding new features.
- Prefer clean, modular, maintainable code.
- Follow established coding conventions.
- Add appropriate error handling.
- Add tests where applicable.

---

## 4. Development Workflow

For every development task:

1. Read the project-control files.
2. Check `10-DEVELOPMENT-STATUS.md`.
3. Identify the current task.
4. Understand the existing implementation.
5. Implement only the required changes.
6. Run relevant tests.
7. Fix any issues.
8. Verify that existing functionality still works.
9. Update `10-DEVELOPMENT-STATUS.md`.
10. Record important architectural decisions in `11-DECISIONS.md`.
11. Update `12-CHANGELOG.md` when appropriate.
12. Create a Git commit after the task is successfully completed.

---

## 5. Git Rules

Git is used to maintain stable checkpoints.

A task should not be marked as completed until:

- Implementation is complete.
- Relevant tests pass.
- Existing functionality is verified.
- The code is in a stable state.

Use meaningful commit messages.

Example:

`feat(user): add user entity`

`feat(user): add user repository`

`test(user): add user service tests`

Do not combine unrelated changes into a single commit.

---

## 6. Architecture Rules

Do not change the project architecture or technology stack without explicit approval.

If a change is required:

1. Explain why the change is necessary.
2. Identify the impact.
3. Record the decision in `11-DECISIONS.md`.
4. Proceed only after approval when the change is architectural or affects the approved stack.

---

## 7. Security Rules

Security must be considered during development.

The AI assistant must:

- Never hardcode passwords.
- Never hardcode API keys.
- Never commit secrets.
- Use environment variables for sensitive configuration.
- Validate user input.
- Apply proper authorization.
- Protect APIs appropriately.
- Follow secure coding practices.
- Consider AI-specific security risks such as prompt injection and unauthorized tool execution.

---

## 8. AI Agent Rules

AI agents must not perform unrestricted actions.

Agent tools must have:

- Defined inputs.
- Defined outputs.
- Validation.
- Authorization where required.
- Error handling.
- Appropriate execution limits.

The agent must not bypass application security or business rules.

---

## 9. Database Rules

Database changes must be deliberate and documented.

The AI assistant must:

- Follow the approved database architecture.
- Avoid unnecessary schema changes.
- Maintain relationships correctly.
- Consider transactions where required.
- Consider indexing and query performance.
- Preserve existing data when modifying schemas.

---

## 10. Testing Rules

Testing is part of development, not an optional final step.

Depending on the feature, use appropriate testing such as:

- Unit testing
- Integration testing
- API testing
- End-to-end testing
- Performance testing

A feature should not be considered complete if relevant tests are failing.

---

## 11. Documentation Rules

Important architectural or technical decisions must be documented.

Documentation should be updated when:

- Architecture changes.
- Technology choices change.
- APIs change significantly.
- Database structure changes significantly.
- AI/RAG architecture changes.
- Development workflow changes.

---

## 12. Scope Control

Do not implement future features simply because they are mentioned in the overall project plan.

The project is intentionally developed incrementally.

Always prioritize:

`Current Task → Current Subtask → Current Super Subtask`

Only move forward after the current scope is stable.

---

## 13. Recovery Principle

The project must remain recoverable even if:

- A ChatGPT conversation is lost.
- A Claude Code session is lost.
- An AI coding session fails.
- Development is interrupted.
- A feature introduces a regression.

The Git repository and `project-control/` files must contain enough information to continue development from the last stable checkpoint.

---

## 14. Source of Truth

The following priority should be followed when information conflicts:

1. Approved project decisions
2. Current project requirements
3. Current architecture
4. Technology stack
5. Development plan
6. Development status
7. AI assistant suggestions

The AI assistant must not silently override an existing project decision.

---

## 15. General Principle

Build the project incrementally.

Keep the code clean.

Keep the architecture consistent.

Keep changes isolated.

Test before committing.

Document important decisions.

Maintain stable Git checkpoints.

Never sacrifice project stability just to implement a feature quickly.