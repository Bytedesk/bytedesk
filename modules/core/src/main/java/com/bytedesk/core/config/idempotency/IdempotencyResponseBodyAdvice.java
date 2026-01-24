package com.bytedesk.core.config.idempotency;

// import java.time.Duration;

// import org.springframework.core.MethodParameter;
// import org.springframework.data.redis.core.StringRedisTemplate;
// import org.springframework.http.MediaType;
// import org.springframework.http.converter.HttpMessageConverter;
// import org.springframework.http.server.ServerHttpRequest;
// import org.springframework.http.server.ServerHttpResponse;
// import org.springframework.http.server.ServletServerHttpRequest;
// import org.springframework.stereotype.Component;
// import org.springframework.util.StringUtils;
// import org.springframework.web.bind.annotation.ControllerAdvice;
// import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

// import com.bytedesk.core.utils.JsonResult;
// import com.fasterxml.jackson.databind.ObjectMapper;

// import jakarta.servlet.http.HttpServletRequest;
// import lombok.RequiredArgsConstructor;

// @ControllerAdvice
// @Component
// @RequiredArgsConstructor
// public class IdempotencyResponseBodyAdvice implements ResponseBodyAdvice<Object> {

//     private final StringRedisTemplate stringRedisTemplate;
//     private final ObjectMapper objectMapper;

//     @Override
//     public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
//         return true;
//     }

//     @Override
//     public Object beforeBodyWrite(
//             Object body,
//             MethodParameter returnType,
//             MediaType selectedContentType,
//             Class<? extends HttpMessageConverter<?>> selectedConverterType,
//             ServerHttpRequest request,
//             ServerHttpResponse response) {

//         HttpServletRequest servletRequest = request instanceof ServletServerHttpRequest ssr ? ssr.getServletRequest() : null;
//         if (servletRequest == null) {
//             return body;
//         }

//         Object redisKeyObj = servletRequest.getAttribute(IdempotencyConstants.ATTR_REDIS_KEY);
//         if (!(redisKeyObj instanceof String redisKey) || !StringUtils.hasText(redisKey)) {
//             return body;
//         }

//         // Only store successful JsonResult responses
//         if (!(body instanceof JsonResult<?> jr)) {
//             return body;
//         }
//         if (jr.getCode() == null || jr.getCode() != 200) {
//             return body;
//         }

//         int ttlSeconds = 600;
//         Object ttlObj = servletRequest.getAttribute(IdempotencyConstants.ATTR_TTL_SECONDS);
//         if (ttlObj instanceof Integer i && i > 0) {
//             ttlSeconds = i;
//         }

//         try {
//             String json = objectMapper.writeValueAsString(jr);
//             stringRedisTemplate.opsForValue().set(redisKey, json, Duration.ofSeconds(ttlSeconds));
//             servletRequest.setAttribute(IdempotencyConstants.ATTR_STORED, Boolean.TRUE);
//         } catch (Exception e) {
//             // If we can't store, allow afterCompletion to delete pending key.
//         }

//         return body;
//     }
// }
