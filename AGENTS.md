# AGENTS.md

This file provides guidance to AI coding agents (Codex, GitHub Copilot, etc.) when working with code in this repository.

## Project Overview

Bytedesk is an AI-powered omnichannel customer service platform with team cooperation capabilities, providing enterprise instant messaging, customer service, knowledge base, ticket management, and AI agent functionality.

**This repository is backend-only**: a Java 21 + Spring Boot 4.1.0 multi-module Maven monorepo. Frontend applications live in separate repositories (see `frontend/readme.md`):

- Web (visitor widget + admin): https://github.com/Bytedesk/bytedesk-web
- Desktop (Electron): https://github.com/Bytedesk/bytedesk-desktop
- Mobile: https://github.com/Bytedesk/bytedesk-mobile

## Architecture

### Backend Architecture (Java 21 + Spring Boot 4.1.0)

- **Entry Point**: `starter/src/main/java/com/bytedesk/starter/StarterApplication.java`
- **Core Stack**: Spring Boot 4.1.0, Spring Data JPA, Spring Security (OAuth2/OIDC + LDAP), WebFlux alongside Spring MVC; Jetty servlet container (Tomcat excluded)
- **Concurrency**: Java 21 virtual threads enabled (`spring.threads.virtual.enabled=true`)
- **Messaging**: Netty-based WebSocket/MQTT/STOMP stack in `modules/core/src/main/java/com/bytedesk/core/socket/` (HTTP port `9003`, WebSocket port `9885`); broker selected via `bytedesk.mq.type` (default `artemis`; `rabbitmq`/`kafka`/`rocketmq` also supported)
- **Database**: MySQL (primary), Redis (caching + message queuing), H2 (testing); PostgreSQL/Oracle/KingBase also supported, selected via `bytedesk.datasource.active`
- **Migrations**: Liquibase, master change log at `starter/src/main/resources/db/changelog/master.xml`
- **Workflow Engines**: Flowable 8.0.0 (ticket workflows), COLA state machine 5.0.0 (core workflows; Spring StateMachine 4.0.0 also on the classpath during migration to COLA)
- **AI**: Spring AI 2.0.0 based integration with 20+ providers, MCP client/server, and Agent Skills (see AI Integration below)
- **Multi-module Maven** structure; root aggregator modules: `channels`, `modules`, `plugins`, `starter`

### Module Structure

**Root Maven modules** (`pom.xml`): `channels`, `modules`, `plugins`, `starter`

- `channels/` - Aggregator only; all channel modules (wechat, whatsapp, email, dingtalk, ...) are currently commented out in `channels/pom.xml`
- `plugins/` - Optional plugins; only `kanban` is active (`appoint`, `backup`, `calendar`, `elearning`, `note`, `pdf`, `socialhub`, `form` are commented out)
- `starter/` - Spring Boot application aggregating the active modules

**Backend Modules** (`modules/`):

- `core/` - Core functionality and utilities (protobuf/gRPC codegen, WebSocket/MQTT/STOMP stack, COLA state machine, RBAC, quartz, push, workflow)
- `team/` - Team IM and organizational structure
- `service/` - Customer service and routing
- `kbase/` - Knowledge base management
- `ai/` - AI integration (Spring AI 2.0.0): 20+ LLM providers (anthropic, azure, baidu, bedrock, custom, dashscope, deepseek, dmr, gitee, google, groq, huggingface, minimax, mistralai, moonshot, nvidia, ollama, openai, openrouter, perplexity, siliconflow, tencent, volcengine, zhipuai), robot/robot_thread, MCP client & server, agent skills, tool/tool_call/tool_audit, RAG
- `ticket/` - Ticket management, SLA, Flowable 8 workflows
- `call/` - Call center functionality (FreeSWITCH)
- `meet/` - Meeting/conferencing
- `webrtc/` - WebRTC video support
- `voc/` - Voice of customer feedback
- `forum/` - Forum functionality
- `social/` - Social features
- `bi/` - Business intelligence (built, but currently NOT aggregated into starter)
- `crm/` - CRM functionality
- `disk/` - File storage
- `mall/` - Mall/e-commerce
- `marketing/` - Marketing tools
- `opinion/` - Opinion/feedback
- `remote/` - Remote support
- `training/` - Training management
- `cli/` - Command line interface

Note: `modules/shop/` contains only stale build output (`target/`) and is NOT a module in `modules/pom.xml`; do not use it.

**Plugins** (`plugins/`):

- `kanban/` - Kanban board functionality (built, but NOT aggregated into starter)

## Development Commands

### Backend (Maven)

Prefer the repository wrapper (`./starter/mvnw`) instead of assuming a global Maven; JDK 21 is required.

```bash
# Build entire project
./starter/mvnw -f pom.xml clean install -DskipTests

# Build a specific module (with its dependencies)
./starter/mvnw -f pom.xml -pl modules/core -am -DskipTests install

# Run application (default profile: noai; JASYPT_ENCRYPTOR_PASSWORD optional, see Environment Pitfalls)
./starter/mvnw -f starter/pom.xml spring-boot:run

# Generate Javadoc
./starter/mvnw javadoc:javadoc
```

### Docker Deployment

```bash
cd deploy/docker

# Usage: ./start.sh <db> <mq> <scenario> <target> [observability]
#   db: mysql | postgresql | oracle | kingbase9 (aliases: pg, kingbase)
#   mq: artemis | rabbitmq
#   scenario: standard | noai | call | webrtc | call-webrtc
#   target: all | middleware
#   observability (5th arg): obs | observability | true

# Start without AI (default active profile of the app is also noai)
./start.sh mysql artemis noai all

# Standard scenario
./start.sh mysql artemis standard all

# Only middleware (DB/MQ/etc.), no app container
./start.sh mysql artemis standard middleware

# With observability stack (Prometheus/Grafana)
./start.sh mysql artemis standard all obs
```

Notes:

- `call` and `call-webrtc` scenarios only support `mysql`/`postgresql` backends
- Compose assets: `compose-base.yaml`, `compose-db-*.yaml`, `compose-mq-{artemis,rabbitmq}.yaml`, `compose-scenario-{standard,noai,call,webrtc}.yaml`, `compose-app-*.yaml`, `compose-observability.yaml`
- `deploy/docker/one/` contains all-in-one compose files for quick single-node deployment
- `stop.sh` for teardown, `watchdog.sh` for monitoring

## Key Technologies and Patterns

### Backend Patterns

- **JPA Entities**: Domain models with Spring Data JPA repositories
- **REST Controllers**: Spring MVC controllers with OpenAPI/Swagger (springdoc) documentation
- **WebSocket Controllers**: Real-time messaging via Netty
- **Service Layer**: Business logic in service classes
- **Security**: Spring Security with OAuth2/OIDC + LDAP support; optional Jasypt property encryption for `ENC(...)` values
- **Workflows**: Flowable 8 (ticket processes), COLA state machine (core workflows)
- **Caching**: Redis + Caffeine (local cache)
- **Message Queue**: `bytedesk.mq.type` selects the broker (default `artemis`; `rabbitmq`/`kafka`/`rocketmq` supported); ActiveMQ Artemis / RabbitMQ available via Docker compose; embedded Artemis server dependency included
- **Templates**: FreeMarker is primary; Thymeleaf restricted to `error/*`, `excel/*`, `file/*` views

### Plugin Patterns

- **Plugins**: Optional capabilities layered on `modules/`; only `kanban` currently active and it is NOT aggregated into starter
- **Channels**: All channel modules are disabled (commented out in `channels/pom.xml`); re-enable by un-commenting the desired module

## Important Notes

### Database Support

The database is selected via `bytedesk.datasource.active` (default `mysql`):

- MySQL (primary production)
- PostgreSQL
- H2 (development/testing)
- Oracle
- KingBase

Datasource files: `starter/src/main/resources/properties/<profile>/datasource/50-datasource-*.properties` (common + per-DB).

Default login: `admin@email.com` / `admin`

### AI Integration

Built on Spring AI 2.0.0 with 20+ providers under `modules/ai/.../providers/`: Ollama (local), ZhipuAI (default provider of the `open` profile), DeepSeek, DashScope, OpenAI, Anthropic, Azure, Google, Bedrock, Moonshot, Minimax, SiliconFlow, Volcengine, Tencent, Baidu, Gitee, Groq, Mistral AI, NVIDIA, Perplexity, OpenRouter, HuggingFace, DMR, plus a `custom` provider.

- The `open` profile sets ZhipuAI as default for chat/embedding/vision/audio/rerank/multimodal models (`properties/open/70-ai-batch-liquibase.properties`)
- Elasticsearch vector store (custom `VectorStoreConfig`); Spring AI JDBC ChatMemory tables managed by Liquibase (`spring.ai.chat.memory.repository.jdbc.initialize-schema=never`)
- **MCP** (`properties/<profile>/75-mcp.properties`): MCP client and server are disabled by default (`spring.ai.mcp.client.enabled=false`, `spring.ai.mcp.server.enabled=false`); when enabled, bearer-token auth applies and only read-only tools are exposed by default
- **Agent Skills** (`properties/<profile>/76-skills.properties`): shell / filesystem / web-fetch skill tools; disabled in the `noai` profile, enabled in `open`; skill definitions live under `starter/src/main/resources/skills/`

### Module Dependencies

- All modules depend on `modules/core`
- Starter aggregates: `core`, `team`, `service`, `kbase`, `ai`, `crm`, `marketing`, `ticket`, `call`, `meet`, `webrtc`, `voc`, `forum`, `social`, `remote`, `training`, `cli`
- Not currently aggregated into starter: `bi` (commented out in `starter/pom.xml`), `disk`, `mall`, `opinion`, and the `kanban` plugin
- All channel modules are disabled (commented out in `channels/pom.xml`)

### Configuration Files

- Main: `starter/src/main/resources/application.properties` — sets `spring.profiles.active=noai` by default
- Profiles: `noai` (default, AI disabled) and `open` (AI enabled, ZhipuAI default). `application-noai.properties` / `application-open.properties` import modular configs from `starter/src/main/resources/properties/{noai,open}/`
- Modular configs are numbered for ordering: `30-core-business`, `31-call-freeswitch`, `32-webrtc`, `33-cache-redis`, `40-oauth-ldap`, `41-logging`, `51-jpa-web-actuator`, `60-mq-mail-quartz`, `70-ai-batch-liquibase`, `75-mcp`, `76-skills`, `80-flowable`, `90-thirdparty-cloud`, `99-docker-compose`, plus `datasource/50-*`
- Docker: `deploy/docker/compose-*.yaml`

### Environment Pitfalls

- Default local HTTP port is `9003`, WebSocket port is `9885`; check port conflicts before concluding startup failure
- Local dev commonly requires MySQL + Redis (Docker compose in `deploy/docker/` is the usual path); Spring Boot docker-compose lifecycle is disabled (`spring.docker.compose.enabled=false`) — use `deploy/docker/start.sh` / `stop.sh` instead
- `JASYPT_ENCRYPTOR_PASSWORD` is only required if you actually use `ENC(...)` encrypted properties; shipped configs contain only commented examples, so startup works without it
- `bytedesk.licenseKey` may need a trial license for some features (see comments in `application-noai.properties`)
- `modules/core` includes protobuf/gRPC generation (CI installs protoc 23.x); if related classes are missing, rebuild core with `-pl modules/core -am`
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

- Main JAR: `starter/target/bytedesk-starter.jar` (finalName fixed in `starter/pom.xml`)
- Generated Javadocs: `starter/src/main/resources/static/javadocs/`
- Swagger/OpenAPI: Available at `/swagger-ui.html` when running
- Docker image: built from `starter/Dockerfile` (eclipse-temurin:21-jdk base), published to `jackning/bytedesk` on Docker Hub

## CI/CD

- `.github/workflows/bytedesk.yml`: triggered on `v*` tag pushes; JDK 21 (temurin) + protoc 23.x, Maven build, creates GitHub release
- `.github/modernize/`: modernization workflow assets

## Development Workflow

1. **Backend Development**: Work in specific modules under `modules/`, test via starter app
2. **Plugins**: Add optional capabilities in `plugins/` (only `kanban` active)
3. **Channels**: Currently disabled; re-enable by un-commenting modules in `channels/pom.xml`
4. **Full Stack Testing**: Run backend on port 9003; frontends (separate repos) connect via REST/WebSocket (9885)
5. **Docker Testing**: Use `deploy/docker/start.sh` for full environment testing

## License

Business Source License 1.1 - Can be used for commercial purposes but prohibits resale, SaaS hosting, or deployment for illegal businesses without permission.
