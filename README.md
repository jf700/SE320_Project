# Digital Therapy Assistant

AI-guided Cognitive Behavioral Therapy (CBT) platform for workplace burnout recovery, built with Spring Boot and Spring AI.

## Prerequisites

- **Java 17+**
- **Maven 3.8+**
- **Anthropic API Key** (for AI features)

## Build and Run

### 1. Clean and Compile

```bash
mvn clean
mvn compile
```

### 2. Run Tests

```bash
mvn test
```

Test coverage reports are generated at `target/site/jacoco/index.html`.

### 3. Run the Application

```bash
mvn spring-boot:run
```

### 4. Environment Variables

Set the following environment variables before running:

| Variable | Description | Required |
|---|---|---|
| `ANTHROPIC_API_KEY` | Anthropic Claude API key for AI features | Yes |

Example:

```bash
export ANTHROPIC_API_KEY=your-api-key-here
mvn spring-boot:run
```

## Accessing the Application

Once running on `localhost:8080`:

| Resource | URL |
|---|---|
| **Swagger UI** (API Documentation) | http://localhost:8080/swagger-ui/index.html |
| **H2 Database Console** | http://localhost:8080/h2-console |
| **CLI Interface** | Starts automatically in the terminal |

### H2 Console Connection

- **JDBC URL**: `jdbc:h2:file:./data/digitaltherapy_db`
- **Username**: `sa`
- **Password**: *(empty)*

## Project Structure

```
src/main/java/com/digitaltherapy/
  cli/                    # Command-line interface (Command Pattern)
  config/                 # Spring configuration (Security, JPA, AI)
  controller/             # REST API controllers
  dto/                    # Request/Response DTOs
  entity/                 # JPA entities
  exception/              # Global exception handling
  repository/             # Spring Data JPA repositories
  security/               # JWT authentication filter and provider
  service/                # Business logic services
    impl/                 # Service implementations
    rag/                  # RAG context builder, knowledge base loader, crisis detector
```

## Technology Stack

- **Spring Boot 3.4.1** - Application framework
- **Spring Security** - JWT-based authentication
- **Spring Data JPA** - Database persistence
- **H2 Database** - File-based persistent storage
- **Spring AI (Anthropic Claude)** - AI therapeutic responses and analysis
- **SimpleVectorStore** - Vector similarity search for RAG
- **SpringDoc OpenAPI** - API documentation
- **JaCoCo** - Code coverage reporting

## Architecture

C4 architecture diagrams are located in `docs/architecture/`:

- **Context Diagram** - System context with users and external systems
- **Container Diagram** - High-level technical building blocks
- **Component Diagram** - Internal application components
- **Code Diagrams** - Class diagram and sequence diagrams

Entity-Relationship diagram is located in `docs/erd/`.
