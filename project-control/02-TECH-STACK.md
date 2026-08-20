# Technology Stack

## 1. Frontend

* React
* TypeScript
* Vite
* Tailwind CSS
* REST API
* WebSocket
* Playwright
* React Testing Library

---

## 2. Backend

* Java
* Spring Boot
* Spring MVC
* Spring Security
* Spring Data JPA
* Hibernate
* Jakarta Validation
* OpenAPI / Swagger
* Maven

---

## 3. Databases

### PostgreSQL

Primary relational database for:

* Users
* Roles
* Projects
* Tasks
* Permissions
* Notifications
* Other transactional data

### MongoDB

For flexible AI-related data:

* Conversations
* Chat history
* Agent executions
* AI reports
* AI metadata

### Redis

For:

* Caching
* Temporary state
* Rate limiting
* Distributed locks

### Qdrant

Vector database for:

* Document embeddings
* Semantic search
* RAG retrieval

---

## 4. AI

* Spring AI
* Configurable LLM provider
* Embedding models
* Tool calling
* Structured AI responses
* Streaming responses where supported

### AI Capabilities

* RAG
* Agent orchestration
* Multi-agent system
* AI memory
* Tool calling
* AI analytics
* AI reports

---

## 5. Concurrency & Async Processing

Java/Spring features:

* ExecutorService
* ThreadPoolExecutor
* Callable
* Future
* CompletableFuture
* ConcurrentHashMap
* Locks
* Semaphore
* Spring `@Async`
* ThreadPoolTaskExecutor

---

## 6. Messaging & Scheduling

### RabbitMQ

For:

* Background jobs
* Document processing
* AI jobs
* Notifications
* Domain events

### Spring Scheduler

For:

* Scheduled reports
* Overdue task checks
* Project monitoring
* Automated workflows

---

## 7. File Storage

### Development

Local file storage.

### Production

AWS S3.

---

## 8. Testing

* JUnit 5
* Mockito
* Spring Boot Test
* Testcontainers
* REST Assured
* Postman
* Playwright
* React Testing Library
* Apache JMeter

---

## 9. DevOps

* Git
* GitHub
* Docker
* Docker Compose
* Jenkins
* GitHub Actions
* AWS ECR

---

## 10. Cloud

### AWS

Planned services:

* EC2
* ECR
* S3
* RDS
* IAM
* VPC
* Application Load Balancer
* CloudWatch
* EKS

---

## 11. Kubernetes

* Kubernetes
* Amazon EKS
* Deployments
* Services
* ConfigMaps
* Secrets
* Ingress
* Health checks
* Horizontal Pod Autoscaler
* Rolling deployments

---

## 12. Monitoring

* Spring Boot Actuator
* Prometheus
* Grafana
* AWS CloudWatch

Monitor:

* API performance
* Errors
* Database performance
* AI execution
* RAG performance
* Resource usage

---

## 13. Architecture Principles

The project should follow:

* SOLID
* Clean Code
* Separation of Concerns
* Dependency Injection
* Loose Coupling
* High Cohesion
* Security
* Testability
* Observability
* Scalability

---

## 14. Technology Change Rule

The listed technologies are the initial approved stack.

A significant technology change must be documented in `11-DECISIONS.md` and reflected in this file.
