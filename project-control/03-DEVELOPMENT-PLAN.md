# Development Plan

## Development Rules

The project will be developed incrementally.

The hierarchy below defines the planned implementation order.

### Status Legend

```text
[DONE] = completed and verified
[IN_PROGRESS] = actively being worked on
[PENDING] = not started yet
[BLOCKED] = waiting on dependency or issue
```

```text
MAIN PART 1: PROJECT FOUNDATION [DONE]
│
├── Task 1.1: Project Planning [DONE]
│   ├── Subtask 1.1.1: Define project objective [DONE]
│   ├── Subtask 1.1.2: Define functional requirements [DONE]
│   ├── Subtask 1.1.3: Define non-functional requirements [DONE]
│   ├── Subtask 1.1.4: Define user roles [DONE]
│   └── Subtask 1.1.5: Define system modules [DONE]
│
├── Task 1.2: Architecture Design [DONE]
│   ├── Subtask 1.2.1: Define frontend architecture [DONE]
│   ├── Subtask 1.2.2: Define backend architecture [DONE]
│   ├── Subtask 1.2.3: Define AI architecture [DONE]
│   ├── Subtask 1.2.4: Define database architecture [DONE]
│   ├── Subtask 1.2.5: Define communication architecture [DONE]
│   └── Subtask 1.2.6: Define deployment architecture [DONE]
│
├── Task 1.3: Git Repository [DONE]
│   ├── Subtask 1.3.1: Create Git repository [DONE]
│   ├── Subtask 1.3.2: Create README [DONE]
│   ├── Subtask 1.3.3: Create .gitignore [DONE]
│   ├── Subtask 1.3.4: Define branch strategy [DONE]
│   └── Subtask 1.3.5: Create initial commit [DONE]
│
└── Task 1.4: Development Environment [DONE]
    ├── Subtask 1.4.1: Install Java [DONE]
    ├── Subtask 1.4.2: Install Node.js [DONE]
    ├── Subtask 1.4.3: Install Docker [DONE]
    ├── Subtask 1.4.4: Configure IDE [DONE]
    ├── Subtask 1.4.5: Configure Git [DONE]
    └── Subtask 1.4.6: Configure environment variables [DONE]


MAIN PART 2: BACKEND FOUNDATION — SPRING BOOT [IN_PROGRESS]
│
├── Task 2.1: Create Spring Boot Application [DONE]
│   ├── Subtask 2.1.1: Create Spring Boot project [DONE]
│   ├── Subtask 2.1.2: Configure Maven [DONE]
│   ├── Subtask 2.1.3: Configure application properties [DONE]
│   ├── Subtask 2.1.4: Create package structure [DONE]
│   └── Subtask 2.1.5: Create health-check API [DONE]
│
├── Task 2.2: Layered Architecture [IN_PROGRESS]
│   ├── Subtask 2.2.1: Create Controller layer [DONE]
│   ├── Subtask 2.2.2: Create Service layer [DONE]
│   ├── Subtask 2.2.3: Create Repository layer [DONE]
│   ├── Subtask 2.2.4: Create Entity layer [DONE]
│   ├── Subtask 2.2.5: Create DTO layer [DONE]
│   └── Subtask 2.2.6: Create Mapper layer [DONE]
│
├── Task 2.3: Global Exception Handling [DONE]
│   ├── Subtask 2.3.1: Create custom exceptions [DONE]
│   ├── Subtask 2.3.2: Create GlobalExceptionHandler [DONE]
│   ├── Subtask 2.3.3: Standardize error response [DONE]
│   └── Subtask 2.3.4: Handle validation errors [DONE]
│
└── Task 2.4: API Standards [DONE]
    ├── Subtask 2.4.1: API response structure [DONE]
    ├── Subtask 2.4.2: HTTP status codes [DONE]
    ├── Subtask 2.4.3: API versioning [DONE]
    └── Subtask 2.4.4: Swagger/OpenAPI [DONE]


MAIN PART 3: POSTGRESQL + HIBERNATE/JPA
│
├── Task 3.1: PostgreSQL Setup
│   ├── Subtask 3.1.1: Install PostgreSQL
│   ├── Subtask 3.1.2: Create database
│   ├── Subtask 3.1.3: Configure datasource
│   └── Subtask 3.1.4: Test database connection
│
├── Task 3.2: User Domain
│   ├── Subtask 3.2.1: Create User entity
│   ├── Subtask 3.2.2: Create User repository
│   ├── Subtask 3.2.3: Create User service
│   ├── Subtask 3.2.4: Create User controller
│   └── Subtask 3.2.5: Test User CRUD
│
├── Task 3.3: Role Domain
│   ├── Subtask 3.3.1: Create Role entity
│   ├── Subtask 3.3.2: Create User-Role relationship
│   ├── Subtask 3.3.3: Create Role repository
│   └── Subtask 3.3.4: Test relationships
│
├── Task 3.4: Project Domain
│   ├── Subtask 3.4.1: Create Project entity
│   ├── Subtask 3.4.2: Create Project repository
│   ├── Subtask 3.4.3: Create Project service
│   ├── Subtask 3.4.4: Create Project controller
│   └── Subtask 3.4.5: Test Project CRUD
│
├── Task 3.5: Task Management Domain
│   ├── Subtask 3.5.1: Create Task entity
│   ├── Subtask 3.5.2: Create Task repository
│   ├── Subtask 3.5.3: Create Task service
│   ├── Subtask 3.5.4: Create Task controller
│   ├── Subtask 3.5.5: Task-user relationship
│   ├── Subtask 3.5.6: Task-project relationship
│   └── Subtask 3.5.7: Test Task CRUD
│
├── Task 3.6: Advanced Hibernate
│   ├── Subtask 3.6.1: One-to-One mapping
│   ├── Subtask 3.6.2: One-to-Many mapping
│   ├── Subtask 3.6.3: Many-to-One mapping
│   ├── Subtask 3.6.4: Many-to-Many mapping
│   ├── Subtask 3.6.5: Lazy loading
│   ├── Subtask 3.6.6: Cascade operations
│   ├── Subtask 3.6.7: Transactions
│   └── Subtask 3.6.8: Pagination and sorting
│
└── Task 3.7: Database Migration
    ├── Subtask 3.7.1: Configure Flyway
    ├── Subtask 3.7.2: Create migration scripts
    ├── Subtask 3.7.3: Version database schema
    └── Subtask 3.7.4: Test migration rollback strategy


MAIN PART 4: SECURITY & AUTHENTICATION
│
├── Task 4.1: Spring Security
│   ├── Subtask 4.1.1: Add Spring Security
│   ├── Subtask 4.1.2: Configure SecurityFilterChain
│   ├── Subtask 4.1.3: Password hashing
│   └── Subtask 4.1.4: Protect APIs
│
├── Task 4.2: JWT Authentication
│   ├── Subtask 4.2.1: JWT generation
│   ├── Subtask 4.2.2: JWT validation
│   ├── Subtask 4.2.3: JWT filter
│   ├── Subtask 4.2.4: Login API
│   ├── Subtask 4.2.5: Refresh token
│   └── Subtask 4.2.6: Logout
│
└── Task 4.3: RBAC
    ├── Subtask 4.3.1: ADMIN role
    ├── Subtask 4.3.2: MANAGER role
    ├── Subtask 4.3.3: EMPLOYEE role
    ├── Subtask 4.3.4: Permission system
    └── Subtask 4.3.5: Method-level authorization


MAIN PART 5: MONGODB
│
├── Task 5.1: MongoDB Setup
│   ├── Subtask 5.1.1: Configure MongoDB
│   ├── Subtask 5.1.2: Connect Spring Boot
│   └── Subtask 5.1.3: Create Mongo repositories
│
├── Task 5.2: Chat Storage
│   ├── Subtask 5.2.1: Create conversation document
│   ├── Subtask 5.2.2: Store user messages
│   ├── Subtask 5.2.3: Store AI responses
│   └── Subtask 5.2.4: Conversation retrieval
│
├── Task 5.3: AI Execution Logs
│   ├── Subtask 5.3.1: Agent execution document
│   ├── Subtask 5.3.2: Store tool calls
│   ├── Subtask 5.3.3: Store execution status
│   └── Subtask 5.3.4: Store execution metrics
│
└── Task 5.4: MongoDB Optimization
    ├── Subtask 5.4.1: Indexes
    ├── Subtask 5.4.2: Query optimization
    └── Subtask 5.4.3: Aggregation pipelines


MAIN PART 6: REDIS
│
├── Task 6.1: Redis Setup
│   ├── Subtask 6.1.1: Install Redis
│   ├── Subtask 6.1.2: Spring Redis configuration
│   └── Subtask 6.1.3: Test connection
│
├── Task 6.2: Caching
│   ├── Subtask 6.2.1: Cache frequently used data
│   ├── Subtask 6.2.2: Cache AI responses
│   ├── Subtask 6.2.3: Cache invalidation
│   └── Subtask 6.2.4: TTL
│
└── Task 6.3: Distributed Features
    ├── Subtask 6.3.1: Distributed lock
    ├── Subtask 6.3.2: Rate limiting
    └── Subtask 6.3.3: Temporary agent state


MAIN PART 7: DOCUMENT MANAGEMENT
│
├── Task 7.1: Document Upload
│   ├── Subtask 7.1.1: PDF upload
│   ├── Subtask 7.1.2: DOCX upload
│   ├── Subtask 7.1.3: TXT upload
│   └── Subtask 7.1.4: File validation
│
├── Task 7.2: Document Storage
│   ├── Subtask 7.2.1: Local storage
│   ├── Subtask 7.2.2: Document metadata
│   ├── Subtask 7.2.3: Document versioning
│   └── Subtask 7.2.4: Document deletion
│
└── Task 7.3: Document Processing
    ├── Subtask 7.3.1: Text extraction
    ├── Subtask 7.3.2: Text cleaning
    ├── Subtask 7.3.3: Document chunking
    └── Subtask 7.3.4: Metadata extraction


MAIN PART 8: RAG PIPELINE
│
├── Task 8.1: Embedding System
│   ├── Subtask 8.1.1: Select embedding model
│   ├── Subtask 8.1.2: Generate embeddings
│   ├── Subtask 8.1.3: Store embeddings
│   └── Subtask 8.1.4: Embedding error handling
│
├── Task 8.2: Vector Database
│   ├── Subtask 8.2.1: Setup vector database
│   ├── Subtask 8.2.2: Create collection
│   ├── Subtask 8.2.3: Store vectors
│   └── Subtask 8.2.4: Delete vectors
│
├── Task 8.3: Semantic Search
│   ├── Subtask 8.3.1: Query embedding
│   ├── Subtask 8.3.2: Similarity search
│   ├── Subtask 8.3.3: Top-K retrieval
│   └── Subtask 8.3.4: Metadata filtering
│
├── Task 8.4: RAG Generation
│   ├── Subtask 8.4.1: Build prompt
│   ├── Subtask 8.4.2: Add retrieved context
│   ├── Subtask 8.4.3: Send context to LLM
│   ├── Subtask 8.4.4: Generate answer
│   └── Subtask 8.4.5: Add source citations
│
└── Task 8.5: RAG Quality
    ├── Subtask 8.5.1: Similarity threshold
    ├── Subtask 8.5.2: Chunk optimization
    ├── Subtask 8.5.3: Context window management
    └── Subtask 8.5.4: Hallucination reduction


MAIN PART 9: AI AGENT SYSTEM
│
├── Task 9.1: LLM Integration
│   ├── Subtask 9.1.1: Configure LLM provider
│   ├── Subtask 9.1.2: Create LLM service
│   ├── Subtask 9.1.3: Prompt management
│   └── Subtask 9.1.4: Token/error handling
│
├── Task 9.2: Agent Core
│   ├── Subtask 9.2.1: Agent request
│   ├── Subtask 9.2.2: Intent detection
│   ├── Subtask 9.2.3: Agent decision system
│   ├── Subtask 9.2.4: Agent response
│   └── Subtask 9.2.5: Agent failure handling
│
├── Task 9.3: Tool System
│   ├── Subtask 9.3.1: Define tool interface
│   ├── Subtask 9.3.2: Document search tool
│   ├── Subtask 9.3.3: Database query tool
│   ├── Subtask 9.3.4: Task creation tool
│   ├── Subtask 9.3.5: Task update tool
│   ├── Subtask 9.3.6: Report generation tool
│   ├── Subtask 9.3.7: Analytics tool
│   └── Subtask 9.3.8: Notification tool
│
├── Task 9.4: Supervisor Agent
│   ├── Subtask 9.4.1: Create supervisor
│   ├── Subtask 9.4.2: Intent classification
│   ├── Subtask 9.4.3: Select specialized agent
│   ├── Subtask 9.4.4: Execute agent
│   └── Subtask 9.4.5: Aggregate results
│
├── Task 9.5: Specialized Agents
│   ├── Subtask 9.5.1: RAG Agent
│   ├── Subtask 9.5.2: Database Agent
│   ├── Subtask 9.5.3: Analytics Agent
│   ├── Subtask 9.5.4: Task Agent
│   ├── Subtask 9.5.5: Report Agent
│   └── Subtask 9.5.6: Notification Agent
│
└── Task 9.6: Agent Safety
    ├── Subtask 9.6.1: Tool authorization
    ├── Subtask 9.6.2: Input validation
    ├── Subtask 9.6.3: Tool execution limits
    ├── Subtask 9.6.4: Prevent unauthorized actions
    └── Subtask 9.6.5: Agent timeout handling


MAIN PART 10: AI MEMORY
│
├── Task 10.1: Conversation Memory
│   ├── Subtask 10.1.1: Store conversation
│   ├── Subtask 10.1.2: Retrieve conversation
│   └── Subtask 10.1.3: Conversation summarization
│
├── Task 10.2: User Memory
│   ├── Subtask 10.2.1: Store preferences
│   ├── Subtask 10.2.2: Retrieve preferences
│   └── Subtask 10.2.3: Update memory
│
└── Task 10.3: Memory Optimization
    ├── Subtask 10.3.1: Short-term memory
    ├── Subtask 10.3.2: Long-term memory
    └── Subtask 10.3.3: Memory cleanup


MAIN PART 11: MULTITHREADING & ASYNC PROCESSING
│
├── Task 11.1: Java Concurrency
│   ├── Subtask 11.1.1: Thread basics
│   ├── Subtask 11.1.2: ExecutorService
│   ├── Subtask 11.1.3: ThreadPoolExecutor
│   └── Subtask 11.1.4: Callable/Future
│
├── Task 11.2: CompletableFuture
│   ├── Subtask 11.2.1: Async document processing
│   ├── Subtask 11.2.2: Parallel embedding generation
│   ├── Subtask 11.2.3: Parallel database operations
│   └── Subtask 11.2.4: Combine asynchronous results
│
├── Task 11.3: Spring Async
│   ├── Subtask 11.3.1: Enable async
│   ├── Subtask 11.3.2: Configure thread pool
│   ├── Subtask 11.3.3: Async service
│   └── Subtask 11.3.4: Async exception handling
│
└── Task 11.4: Concurrency Control
    ├── Subtask 11.4.1: ConcurrentHashMap
    ├── Subtask 11.4.2: Locks
    ├── Subtask 11.4.3: Semaphore
    └── Subtask 11.4.4: Race-condition prevention


MAIN PART 12: MESSAGE QUEUE
│
├── Task 12.1: RabbitMQ Setup
│   ├── Subtask 12.1.1: Install RabbitMQ
│   ├── Subtask 12.1.2: Configure Spring AMQP
│   └── Subtask 12.1.3: Test connection
│
├── Task 12.2: Document Processing Queue
│   ├── Subtask 12.2.1: Create exchange
│   ├── Subtask 12.2.2: Create queue
│   ├── Subtask 12.2.3: Publish document event
│   ├── Subtask 12.2.4: Consume document event
│   └── Subtask 12.2.5: Handle failed messages
│
└── Task 12.3: Event-Driven Architecture
    ├── Subtask 12.3.1: Define domain events
    ├── Subtask 12.3.2: Publish events
    ├── Subtask 12.3.3: Consume events
    └── Subtask 12.3.4: Dead-letter queue


MAIN PART 13: REACT FRONTEND
│
├── Task 13.1: React Setup
│   ├── Subtask 13.1.1: Create React + TypeScript app
│   ├── Subtask 13.1.2: Configure routing
│   ├── Subtask 13.1.3: Configure API client
│   └── Subtask 13.1.4: Environment configuration
│
├── Task 13.2: Authentication UI
│   ├── Subtask 13.2.1: Login page
│   ├── Subtask 13.2.2: Register page
│   ├── Subtask 13.2.3: JWT storage
│   ├── Subtask 13.2.4: Protected routes
│   └── Subtask 13.2.5: Logout
│
├── Task 13.3: Dashboard
│   ├── Subtask 13.3.1: Dashboard layout
│   ├── Subtask 13.3.2: Project statistics
│   ├── Subtask 13.3.3: Task statistics
│   ├── Subtask 13.3.4: AI statistics
│   └── Subtask 13.3.5: Activity feed
│
├── Task 13.4: Project Management UI
│   ├── Subtask 13.4.1: Project list
│   ├── Subtask 13.4.2: Create project
│   ├── Subtask 13.4.3: Project details
│   └── Subtask 13.4.4: Project editing
│
├── Task 13.5: Task Management UI
│   ├── Subtask 13.5.1: Task list
│   ├── Subtask 13.5.2: Create task
│   ├── Subtask 13.5.3: Update task
│   ├── Subtask 13.5.4: Assign task
│   └── Subtask 13.5.5: Task filtering
│
└── Task 13.6: AI Chat UI
    ├── Subtask 13.6.1: Chat interface
    ├── Subtask 13.6.2: Message component
    ├── Subtask 13.6.3: Streaming response
    ├── Subtask 13.6.4: Source citations
    └── Subtask 13.6.5: Conversation history


MAIN PART 14: REAL-TIME SYSTEM
│
├── Task 14.1: WebSocket
│   ├── Subtask 14.1.1: Configure WebSocket
│   ├── Subtask 14.1.2: Create event model
│   └── Subtask 14.1.3: Connect React
│
├── Task 14.2: Agent Execution Tracking
│   ├── Subtask 14.2.1: Agent started event
│   ├── Subtask 14.2.2: Tool execution event
│   ├── Subtask 14.2.3: Agent completed event
│   └── Subtask 14.2.4: Agent failure event
│
└── Task 14.3: Live UI
    ├── Subtask 14.3.1: Execution timeline
    ├── Subtask 14.3.2: Tool status
    └── Subtask 14.3.3: Processing indicator


MAIN PART 15: ANALYTICS & REPORTING
│
├── Task 15.1: Project Analytics
│   ├── Subtask 15.1.1: Task completion metrics
│   ├── Subtask 15.1.2: Overdue task metrics
│   └── Subtask 15.1.3: Project progress
│
├── Task 15.2: AI Analytics
│   ├── Subtask 15.2.1: AI request count
│   ├── Subtask 15.2.2: Agent execution time
│   ├── Subtask 15.2.3: Tool usage
│   └── Subtask 15.2.4: RAG retrieval metrics
│
└── Task 15.3: AI Reports
    ├── Subtask 15.3.1: Generate project report
    ├── Subtask 15.3.2: Generate task report
    ├── Subtask 15.3.3: Generate analytics report
    └── Subtask 15.3.4: Export report


MAIN PART 16: NOTIFICATION SYSTEM
│
├── Task 16.1: Notification Backend
│   ├── Subtask 16.1.1: Notification entity
│   ├── Subtask 16.1.2: Notification service
│   └── Subtask 16.1.3: Notification API
│
├── Task 16.2: Notification Types
│   ├── Subtask 16.2.1: Task notification
│   ├── Subtask 16.2.2: AI notification
│   ├── Subtask 16.2.3: Project notification
│   └── Subtask 16.2.4: System notification
│
└── Task 16.3: Notification Delivery
    ├── Subtask 16.3.1: In-app notification
    ├── Subtask 16.3.2: Email notification
    └── Subtask 16.3.3: WebSocket notification


MAIN PART 17: SCHEDULED AI AUTOMATION
│
├── Task 17.1: Spring Scheduler
│   ├── Subtask 17.1.1: Configure scheduler
│   ├── Subtask 17.1.2: Daily jobs
│   └── Subtask 17.1.3: Weekly jobs
│
├── Task 17.2: AI Project Monitoring
│   ├── Subtask 17.2.1: Find overdue tasks
│   ├── Subtask 17.2.2: Identify blockers
│   ├── Subtask 17.2.3: Analyze project health
│   └── Subtask 17.2.4: Generate summary
│
└── Task 17.3: Automated Actions
    ├── Subtask 17.3.1: Create reminder
    ├── Subtask 17.3.2: Send notification
    └── Subtask 17.3.3: Generate scheduled report


MAIN PART 18: TESTING
│
├── Task 18.1: Unit Testing
│   ├── Subtask 18.1.1: Service tests
│   ├── Subtask 18.1.2: Controller tests
│   ├── Subtask 18.1.3: Repository tests
│   └── Subtask 18.1.4: Agent tests
│
├── Task 18.2: Integration Testing
│   ├── Subtask 18.2.1: Database integration tests
│   ├── Subtask 18.2.2: MongoDB integration tests
│   ├── Subtask 18.2.3: Redis integration tests
│   └── Subtask 18.2.4: RabbitMQ integration tests
│
├── Task 18.3: API Testing
│   ├── Subtask 18.3.1: Authentication APIs
│   ├── Subtask 18.3.2: Project APIs
│   ├── Subtask 18.3.3: Task APIs
│   ├── Subtask 18.3.4: AI APIs
│   └── Subtask 18.3.5: RAG APIs
│
├── Task 18.4: Playwright E2E
│   ├── Subtask 18.4.1: Login test
│   ├── Subtask 18.4.2: Project test
│   ├── Subtask 18.4.3: Task test
│   ├── Subtask 18.4.4: Document upload test
│   └── Subtask 18.4.5: AI chat test
│
└── Task 18.5: Performance Testing
    ├── Subtask 18.5.1: JMeter setup
    ├── Subtask 18.5.2: API load test
    ├── Subtask 18.5.3: Concurrent AI requests
    └── Subtask 18.5.4: Performance analysis


MAIN PART 19: DOCKER & CONTAINERIZATION
│
├── Task 19.1: Backend Docker
│   ├── Subtask 19.1.1: Backend Dockerfile
│   ├── Subtask 19.1.2: Build image
│   └── Subtask 19.1.3: Run container
│
├── Task 19.2: Frontend Docker
│   ├── Subtask 19.2.1: Frontend Dockerfile
│   ├── Subtask 19.2.2: Build image
│   └── Subtask 19.2.3: Run container
│
└── Task 19.3: Docker Compose
    ├── Subtask 19.3.1: PostgreSQL container
    ├── Subtask 19.3.2: MongoDB container
    ├── Subtask 19.3.3: Redis container
    ├── Subtask 19.3.4: RabbitMQ container
    ├── Subtask 19.3.5: Vector DB container
    ├── Subtask 19.3.6: Backend container
    └── Subtask 19.3.7: Frontend container


MAIN PART 20: CI/CD
│
├── Task 20.1: GitHub Workflow
│   ├── Subtask 20.1.1: GitHub Actions setup
│   ├── Subtask 20.1.2: Build pipeline
│   ├── Subtask 20.1.3: Test pipeline
│   └── Subtask 20.1.4: Docker pipeline
│
├── Task 20.2: Jenkins
│   ├── Subtask 20.2.1: Jenkins setup
│   ├── Subtask 20.2.2: Jenkinsfile
│   ├── Subtask 20.2.3: Automated tests
│   ├── Subtask 20.2.4: Docker build
│   └── Subtask 20.2.5: Image push
│
└── Task 20.3: AWS ECR
    ├── Subtask 20.3.1: Create ECR repository
    ├── Subtask 20.3.2: Configure IAM
    ├── Subtask 20.3.3: Push Docker image
    └── Subtask 20.3.4: Version images


MAIN PART 21: AWS DEPLOYMENT
│
├── Task 21.1: AWS Infrastructure
│   ├── Subtask 21.1.1: VPC
│   ├── Subtask 21.1.2: Subnets
│   ├── Subtask 21.1.3: Security Groups
│   └── Subtask 21.1.4: IAM
│
├── Task 21.2: Application Deployment
│   ├── Subtask 21.2.1: EC2
│   ├── Subtask 21.2.2: Backend deployment
│   ├── Subtask 21.2.3: Frontend deployment
│   └── Subtask 21.2.4: Database connectivity
│
└── Task 21.3: AWS Services
    ├── Subtask 21.3.1: S3 document storage
    ├── Subtask 21.3.2: CloudWatch logs
    ├── Subtask 21.3.3: Load Balancer
    └── Subtask 21.3.4: HTTPS


MAIN PART 22: KUBERNETES
│
├── Task 22.1: Kubernetes Basics
│   ├── Subtask 22.1.1: Cluster setup
│   ├── Subtask 22.1.2: Namespace
│   ├── Subtask 22.1.3: ConfigMap
│   └── Subtask 22.1.4: Secrets
│
├── Task 22.2: Application Deployment
│   ├── Subtask 22.2.1: Backend Deployment
│   ├── Subtask 22.2.2: Frontend Deployment
│   ├── Subtask 22.2.3: Backend Service
│   └── Subtask 22.2.4: Frontend Service
│
├── Task 22.3: Production Features
│   ├── Subtask 22.3.1: Ingress
│   ├── Subtask 22.3.2: Health checks
│   ├── Subtask 22.3.3: Horizontal Pod Autoscaler
│   └── Subtask 22.3.4: Rolling deployment
│
└── Task 22.4: EKS
    ├── Subtask 22.4.1: Create EKS cluster
    ├── Subtask 22.4.2: Deploy application
    └── Subtask 22.4.3: Configure scaling


MAIN PART 23: MONITORING & OBSERVABILITY
│
├── Task 23.1: Spring Boot Monitoring
│   ├── Subtask 23.1.1: Actuator
│   ├── Subtask 23.1.2: Health endpoints
│   └── Subtask 23.1.3: Application metrics
│
├── Task 23.2: Prometheus
│   ├── Subtask 23.2.1: Prometheus setup
│   ├── Subtask 23.2.2: Configure scraping
│   └── Subtask 23.2.3: Custom metrics
│
└── Task 23.3: Grafana
    ├── Subtask 23.3.1: Grafana setup
    ├── Subtask 23.3.2: Backend dashboard
    ├── Subtask 23.3.3: AI dashboard
    └── Subtask 23.3.4: Infrastructure dashboard


MAIN PART 24: ADVANCED AI FEATURES
│
├── Task 24.1: AI Project Manager
│   ├── Subtask 24.1.1: Project analysis
│   ├── Subtask 24.1.2: Task analysis
│   ├── Subtask 24.1.3: Identify blockers
│   └── Subtask 24.1.4: Generate recommendations
│
├── Task 24.2: Autonomous Task Management
│   ├── Subtask 24.2.1: Detect overdue tasks
│   ├── Subtask 24.2.2: Suggest task assignment
│   ├── Subtask 24.2.3: Create tasks
│   └── Subtask 24.2.4: Update task status
│
├── Task 24.3: AI Analytics
│   ├── Subtask 24.3.1: Analyze project trends
│   ├── Subtask 24.3.2: Detect anomalies
│   ├── Subtask 24.3.3: Predict project risks
│   └── Subtask 24.3.4: Generate recommendations
│
└── Task 24.4: Multi-Agent Collaboration
    ├── Subtask 24.4.1: Agent communication
    ├── Subtask 24.4.2: Agent task delegation
    ├── Subtask 24.4.3: Agent result aggregation
    └── Subtask 24.4.4: Agent failure recovery


MAIN PART 25: FINAL PRODUCTION HARDENING
│
├── Task 25.1: Security Review
│   ├── Subtask 25.1.1: API security
│   ├── Subtask 25.1.2: JWT security
│   ├── Subtask 25.1.3: Input validation
│   ├── Subtask 25.1.4: SQL injection prevention
│   └── Subtask 25.1.5: AI prompt injection protection
│
├── Task 25.2: Performance Optimization
│   ├── Subtask 25.2.1: Database optimization
│   ├── Subtask 25.2.2: Redis optimization
│   ├── Subtask 25.2.3: RAG optimization
│   ├── Subtask 25.2.4: Thread pool optimization
│   └── Subtask 25.2.5: API optimization
│
├── Task 25.3: Documentation
│   ├── Subtask 25.3.1: Architecture documentation
│   ├── Subtask 25.3.2: API documentation
│   ├── Subtask 25.3.3: Database documentation
│   ├── Subtask 25.3.4: AI/RAG documentation
│   └── Subtask 25.3.5: Deployment documentation
│
└── Task 25.4: Final Release
    ├── Subtask 25.4.1: Full integration testing
    ├── Subtask 25.4.2: Production build
    ├── Subtask 25.4.3: Production deployment
    ├── Subtask 25.4.4: Final README
    ├── Subtask 25.4.5: Architecture diagram
    └── Subtask 25.4.6: Final Git release/tag
```

## Git Checkpoint Rule

Each completed subtask should produce a stable checkpoint.

```text
Implement
   ↓
Test
   ↓
Verify
   ↓
Update Development Status
   ↓
Git Commit
```

Commit examples:

```text
feat(user): add user entity
feat(user): add user repository
feat(user): add user service
feat(user): add user controller
test(user): add user CRUD tests
```

Do not commit unrelated changes together.

Do not move to the next task while the current task is broken.

## Current Development Position

```text
Main Part: 2. Spring Boot Backend

Task: 2.4 API Standards

Subtask: 2.4.4 Swagger/OpenAPI

Status: COMPLETED
```
