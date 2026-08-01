/**
 * config package for Spring Boot runtime wiring, security setup, tracing, health checks, and property binding.
 * config 包，负责Spring Boot 运行装配、安全配置、链路追踪、健康检查与属性绑定。
 *
 * <p>The package contains WebMvc, WebSecurity, Jackson, scheduler, async executor, and retry configuration,
 * along with health indicators, event publishers, interceptors, and filters-related subpackages.
 * 该包包含 WebMvc、WebSecurity、Jackson、调度、异步执行器、重试等配置，以及健康检查、事件发布器、拦截器和过滤器相关子包。
 *
 * @author bytedesk.com
 */
@NonNullApi
package com.bytedesk.core.config;

import org.springframework.lang.NonNullApi;