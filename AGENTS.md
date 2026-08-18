# AGENTS.md

This file provides guidance to Codex (Codex.ai/code) when working with code in this repository.

## Project Overview

Bytedesk is an AI-powered omnichannel customer service platform with team cooperation capabilities. It's a monorepo built with Maven (backend) and Turbo/pnpm (frontend), providing enterprise instant messaging, customer service, knowledge base, ticket management, and AI agent functionality.

## Architecture

### Backend Architecture (Java 21 + Spring Boot 4.1.0)

- **Entry Point**: `starter/src/main/java/com/bytedesk/starter/StarterApplication.java`
- **Core Stack**: Spring Boot 4.1.0, Spring Data JPA, Spring Security, WebFlux; Jetty servlet container (Tomcat excluded)
- **Concurrency**: Java 21 virtual threads enabled (`spring.threads.virtual.enabled=true`)
- **Messaging**: Netty-based WebSocket/MQTT stack for real-time communication (HTTP port `9003`, WebSocket port `9885`)
- **Database**: MySQL (primary), Redis (caching + message queuing), H2 (testing); PostgreSQL/Oracle/KingBase also supported
- **Migrations**: Liquibase, master change log at `starter/src/main/resources/db/changelog/master.xml`
- **Workflow Engines**: Flowable 8.0.0 + Spring State Machine (ticket workflows), COLA state machine (core)
- **AI**: Spring AI 2.0.0 based integration (Ollama, DeepSeek, ZhipuAI, DashScope)
- **Multi-module Maven** structure; root aggregator modules: `channels`, `control`, `modules`, `plugins`, `projects`, `enterprise`, `starter`

### Frontend Architecture (TypeScript + React)

- **Monorepo**: Turborepo with pnpm workspace (`frontend/apps/*`, `frontend/packages/*`)
- **UI Framework**: React + Ant Design (admin), React + Ant Design Mobile (visitor)
- **State Management**: Zustand stores
- **Build System**: Vite with Turbo for caching
- **Desktop**: `frontend/apps/desktop` is an Electron app (Vite + Electron, Cypress e2e)

### Module Structure

**Backend Modules** (`modules/`):

- `core/` - Core functionality and utilities (protobuf/gRPC codegen, WebSocket/MQTT stack, COLA state machine)
- `team/` - Team IM and organizational structure
- `service/` - Customer service and routing
- `kbase/` - Knowledge base management
- `ai/` - AI agent integration (Spring AI 2.0.0: Ollama, DeepSeek, ZhipuAI, DashScope)
- `ticket/` - Ticket management, SLA, Flowable 8 workflows
- `call/` - Call center functionality
- `meet/` - Meeting/conferencing
- `webrtc/` - WebRTC video support
- `voc/` - Voice of customer feedback
- `forum/` - Forum functionality
- `social/` - Social features
- `bi/` - Business intelligence
- `crm/` - CRM functionality
- `disk/` - File storage
- `mall/` - Mall/e-commerce
- `marketing/` - Marketing tools
- `opinion/` - Opinion/feedback
- `remote/` - Remote support
- `training/` - Training management
- `cli/` - Command line interface

**Plugins** (`plugins/`):

- `custom/` - Custom plugin template
- `customer-support/` - Customer support plugin
- `kanban/` - Kanban board functionality

## Development Commands

### Backend (Maven)

Prefer the repository wrapper (`./starter/mvnw`) instead of assuming a global Maven; JDK 21 is required.

```bash
# Build entire project
./starter/mvnw -f pom.xml clean install -DskipTests

# Build a specific module (with its dependencies)
./starter/mvnw -f pom.xml -pl modules/core -am -DskipTests install

# Run application (default profile: local; requires JASYPT_ENCRYPTOR_PASSWORD)
JASYPT_ENCRYPTOR_PASSWORD=<value> ./starter/mvnw -f starter/pom.xml spring-boot:run

# Generate Javadoc
./starter/mvnw javadoc:javadoc
```

### Docker Deployment

```bash
cd deploy/docker

# Start without AI
./start.sh mysql artemis noai all

# Start with ZhipuAI (default)
./start.sh mysql artemis standard all

# Start with Ollama
./start.sh mysql artemis standard all
```

Compose assets: `compose-scenario-{standard,noai,call,webrtc}.yaml`, MQ options (`compose-mq-artemis.yaml`, `compose-mq-rabbitmq.yaml`), DB options (mysql, postgresql, oracle, kingbase).

## Key Technologies and Patterns

### Backend Patterns

- **JPA Entities**: Domain models with Spring Data JPA repositories
- **REST Controllers**: Spring MVC controllers with OpenAPI/Swagger (springdoc) documentation
- **WebSocket Controllers**: Real-time messaging via Netty
- **Service Layer**: Business logic in service classes
- **Security**: Spring Security with OAuth2/OIDC support; Jasypt property encryption (`JASYPT_ENCRYPTOR_PASSWORD` for `ENC(...)` values)
- **Workflows**: Flowable 8 (ticket processes), Spring State Machine + COLA state machine (core workflows)
- **Caching**: Redis + Caffeine (local cache)
- **Message Queue**: Redis + Spring Integration for async processing; optional ActiveMQ Artemis / RabbitMQ via Docker compose
- **Templates**: FreeMarker is primary; Thymeleaf restricted to `error/*`, `excel/*`, `file/*` views

### Frontend Patterns

- **Component Architecture**: React functional components with hooks
- **State Management**: Zustand for global state
- **Routing**: React Router for navigation
- **API Communication**: REST + WebSocket connections
- **Monorepo**: Shared packages in `frontend/packages/`

### Plugin & Project Patterns

- **Plugins**: Optional capabilities layered on `modules/`; starter aggregates `custom` and `kanban`

## Important Notes

### Database Support

The application supports multiple databases:

- MySQL (primary production)
- PostgreSQL
- H2 (development/testing)
- Oracle
- KingBase

Default login: `admin@email.com` / `admin`

### AI Integration

Built on Spring AI 2.0.0 with multiple providers:

- Ollama (local)
- ZhipuAI
- DeepSeek
- DashScope
- Elasticsearch vector store (custom `VectorStoreConfig`); Spring AI JDBC ChatMemory tables managed by Liquibase

### Module Dependencies

- All modules depend on `modules/core`
- Starter aggregates most `modules/`, all `channels/`, plugins (`custom`, `kanban`), enterprise modules (`core`, `ai`, `kbase`, `service`, `ticket`, `training`, `call`, `webrtc`, `cli`), `projects/liangshibao`, and `control`
- Channel modules are optional - remove dependency if not needed
- Enterprise modules require licensing

### Configuration Files

- Backend: `starter/src/main/resources/application*.properties` (profiles: `local` default, `prod`, `open`, `noai`, `kingbase`); modular configs imported from `starter/src/main/resources/properties/<profile>/`
- Frontend: Individual app configs in `frontend/apps/*/`
- Docker: `deploy/docker/compose-*.yaml`

### Environment Pitfalls

- `JASYPT_ENCRYPTOR_PASSWORD` is required for local startup
- Default local HTTP port is `9003`, WebSocket port `9885`; check port conflicts before concluding startup failure
- Local dev commonly requires MySQL + Redis (Docker compose in `deploy/docker/` is the usual path)
- `modules/core` includes protobuf/gRPC generation; if related classes are missing, rebuild core with `-pl modules/core -am`
- Liquibase migrations live under `starter/src/main/resources/db/changelog/`; do not introduce Flyway
- Prefer minimal scoped changes; do not refactor unrelated modules in the same task

## Testing

### Backend Tests

```bash
# Run all tests
./starter/mvnw -f pom.xml test

# Run tests for specific module
./starter/mvnw -f pom.xml -pl modules/core test

# Run single test class
./starter/mvnw test -Dtest=YourTestClass

# Run single test method
./starter/mvnw test -Dtest=YourTestClass#testMethod
```

## Build Artifacts

### Backend

- Main JAR: `starter/target/bytedesk-starter.jar`
- Generated Javadocs: `starter/src/main/resources/static/javadocs/`
- Swagger/OpenAPI: Available at `/swagger-ui.html` when running

### Frontend

- Build outputs: `frontend/apps/*/dist/`
- Static files served from backend's `starter/src/main/resources/static/`

## Development Workflow

1. **Backend Development**: Work in specific modules under `modules/`, test via starter app
2. **Frontend Development**: Work in apps under `frontend/apps/`, use `pnpm dev` for hot reload
3. **Plugins/Projects**: Add optional capabilities in `plugins/`, customer-specific apps in `projects/`
4. **Full Stack Testing**: Run backend on port 9003, frontend connects via REST/WebSocket (9885)
5. **Docker Testing**: Use docker-compose for full environment testing

## License

Business Source License 1.1 - Can be used for commercial purposes but prohibits resale, SaaS hosting, or deployment for illegal businesses without permission.
