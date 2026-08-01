# config

该包负责 Spring Boot 运行装配、安全配置、链路追踪、健康检查与属性绑定。

## 实现要点

- 核心配置类包括 WebMvcConfig、WebSecurityConfig、JacksonConfig、SchedulerConfig、AsyncExecutorConfig、RetryConfig 和 CorsConfig。
- BytedeskEventPublisher、GenericApplicationEvent 与 HttpRestService 提供共享事件发布和 HTTP 侧运行支撑。
- TraceIdInterceptor、SSEAuthenticationEntryPoint、CoreHealthIndicator、XSSFilterConfig 负责链路追踪、安全入口、健康检查与请求过滤。
- filters、idempotency、metrics、properties 子包承载更细分的配置能力。
