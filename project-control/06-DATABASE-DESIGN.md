# Database Design

## 1. Database Strategy

The system will use multiple data stores, with each database serving a specific purpose.

```text
PostgreSQL → Transactional Data
MongoDB    → AI / Document Data
Redis      → Cache / Temporary Data
Qdrant     → Vector Data
```

---

## 2. PostgreSQL

PostgreSQL will store structured relational data.

### Core Tables

```text
users
roles
permissions
user_roles
role_permissions

projects
project_members

tasks
task_comments

notifications
audit_logs
```

### Main Relationships

```text
User
 ├── Roles
 ├── Projects
 ├── Tasks
 └── Notifications

Project
 ├── Members
 └── Tasks

Task
 └── Comments
```

Hibernate/JPA will manage PostgreSQL entities.

---

## 3. MongoDB

MongoDB will store flexible AI-related data.

### Collections

```text
conversations
agent_executions
ai_reports
document_metadata
```

### Conversation

Stores:

* User
* Conversation ID
* Messages
* Timestamps
* Metadata

### Agent Execution

Stores:

* Agent ID
* User
* Request
* Execution status
* Tool calls
* Execution time
* Errors
* Result metadata

---

## 4. Redis

Redis will be used for temporary and frequently accessed data.

Examples:

```text
cache:user
cache:project
cache:ai-response
rate-limit:user
agent:state
distributed-lock
```

TTL should be configured where appropriate.

---

## 5. Qdrant

Qdrant will store document embeddings.

Each vector should contain metadata such as:

```text
documentId
documentName
chunkId
pageNumber
ownerId
projectId
```

This metadata will support filtered retrieval and source citations.

---

## 6. Data Rules

* PostgreSQL is the primary source for transactional data.
* MongoDB is not a replacement for relational transactional data.
* Redis data must be considered temporary/cacheable.
* Qdrant stores searchable vector representations, not the original documents.
* Original files will be stored separately.
* Database credentials must never be committed to Git.
* Database schema changes must be version controlled using Flyway.

---

## 7. Database Design Principles

* Use appropriate primary keys and foreign keys.
* Add indexes for frequently queried fields.
* Use transactions for critical operations.
* Avoid unnecessary relationships.
* Use pagination for large datasets.
* Prevent duplicate records where appropriate.
* Keep database responsibilities clearly separated.
