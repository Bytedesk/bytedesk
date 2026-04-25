# config

This package wires Spring Boot runtime behavior, security, tracing, health checks, and property binding.

## Implementation Notes

- Core configuration classes include WebMvcConfig, WebSecurityConfig, JacksonConfig, SchedulerConfig, AsyncExecutorConfig, RetryConfig, and CorsConfig.
- BytedeskEventPublisher, GenericApplicationEvent, and HttpRestService provide shared event publishing and HTTP-side runtime support.
- TraceIdInterceptor, SSEAuthenticationEntryPoint, CoreHealthIndicator, and XSSFilterConfig cover tracing, security entry, health inspection, and request filtering.
- Subpackages group specialized concerns under filters, idempotency, metrics, and properties.
