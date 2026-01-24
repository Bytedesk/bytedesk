package com.bytedesk.core.config.idempotency;

// import java.io.IOException;
// import java.nio.charset.StandardCharsets;
// import java.security.MessageDigest;
// import java.time.Duration;
// import java.util.HexFormat;

// import org.springframework.data.redis.core.StringRedisTemplate;
// import org.springframework.http.MediaType;
// import org.springframework.stereotype.Component;
// import org.springframework.util.StringUtils;
// import org.springframework.web.method.HandlerMethod;
// import org.springframework.web.servlet.HandlerInterceptor;

// import com.bytedesk.core.annotation.Idempotent;
// import com.bytedesk.core.rbac.auth.AuthService;
// import com.bytedesk.core.rbac.user.UserEntity;

// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;

// @Component
// @RequiredArgsConstructor
// public class IdempotencyInterceptor implements HandlerInterceptor {

//     private final StringRedisTemplate stringRedisTemplate;
//     private final AuthService authService;

//     @Override
//     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//         Idempotent ann = resolveAnnotation(handler);
//         if (ann == null) {
//             return true;
//         }

//         String key = resolveClientKey(request);
//         if (!StringUtils.hasText(key)) {
//             if (ann.requireKey()) {
//                 writeJson(response, HttpServletResponse.SC_BAD_REQUEST,
//                         "{\"code\":400,\"message\":\"Missing Idempotency-Key\",\"data\":false}");
//                 return false;
//             }
//             return true;
//         }

//         UserEntity user = safeGetUser();
//         String orgUid = user != null && StringUtils.hasText(user.getOrgUid()) ? user.getOrgUid() : "_";
//         String userUid = user != null && StringUtils.hasText(user.getUid()) ? user.getUid() : "_";

//         int ttlSeconds = Math.max(1, ann.ttlSeconds());
//         String redisKey = buildRedisKey(orgUid, userUid, request.getMethod(), request.getRequestURI(), key);

//         request.setAttribute(IdempotencyConstants.ATTR_REDIS_KEY, redisKey);
//         request.setAttribute(IdempotencyConstants.ATTR_TTL_SECONDS, ttlSeconds);

//         String existing = stringRedisTemplate.opsForValue().get(redisKey);
//         if (StringUtils.hasText(existing) && !IdempotencyConstants.VALUE_PENDING.equals(existing)) {
//             // Cache hit: return previous response directly
//             writeJson(response, HttpServletResponse.SC_OK, existing);
//             return false;
//         }

//         Boolean locked = stringRedisTemplate.opsForValue()
//                 .setIfAbsent(redisKey, IdempotencyConstants.VALUE_PENDING, Duration.ofSeconds(ttlSeconds));

//         if (Boolean.TRUE.equals(locked)) {
//             return true;
//         }

//         // Someone else is processing. Wait briefly for a cached response.
//         String waited = waitForCached(redisKey, 10, 50);
//         if (StringUtils.hasText(waited) && !IdempotencyConstants.VALUE_PENDING.equals(waited)) {
//             writeJson(response, HttpServletResponse.SC_OK, waited);
//             return false;
//         }

//         writeJson(response, 409, "{\"code\":409,\"message\":\"Duplicate request in progress\",\"data\":false}");
//         return false;
//     }

//     @Override
//     public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//         Object redisKeyObj = request.getAttribute(IdempotencyConstants.ATTR_REDIS_KEY);
//         if (!(redisKeyObj instanceof String redisKey) || !StringUtils.hasText(redisKey)) {
//             return;
//         }

//         Object stored = request.getAttribute(IdempotencyConstants.ATTR_STORED);
//         boolean storedOk = Boolean.TRUE.equals(stored);

//         // If we didn't store a successful response, remove the pending marker to allow retries.
//         if (!storedOk) {
//             String val = stringRedisTemplate.opsForValue().get(redisKey);
//             if (IdempotencyConstants.VALUE_PENDING.equals(val)) {
//                 stringRedisTemplate.delete(redisKey);
//             }
//         }
//     }

//     private Idempotent resolveAnnotation(Object handler) {
//         if (!(handler instanceof HandlerMethod hm)) {
//             return null;
//         }
//         Idempotent onMethod = hm.getMethodAnnotation(Idempotent.class);
//         if (onMethod != null) {
//             return onMethod;
//         }
//         return hm.getBeanType().getAnnotation(Idempotent.class);
//     }

//     private String resolveClientKey(HttpServletRequest request) {
//         String key = request.getHeader(IdempotencyConstants.HEADER_IDEMPOTENCY_KEY);
//         if (StringUtils.hasText(key)) {
//             return key.trim();
//         }
//         key = request.getHeader(IdempotencyConstants.HEADER_REQUEST_ID);
//         if (StringUtils.hasText(key)) {
//             return key.trim();
//         }
//         return null;
//     }

//     private UserEntity safeGetUser() {
//         try {
//             return authService.getUser();
//         } catch (Exception e) {
//             return null;
//         }
//     }

//     private String buildRedisKey(String orgUid, String userUid, String method, String path, String clientKey) {
//         String raw = String.join("|", orgUid, userUid, nullToEmpty(method), nullToEmpty(path), nullToEmpty(clientKey));
//         String digest = sha256Hex(raw);
//         return IdempotencyConstants.redisKeyPrefix() + digest;
//     }

//     private String sha256Hex(String s) {
//         try {
//             MessageDigest md = MessageDigest.getInstance("SHA-256");
//             byte[] digest = md.digest(s.getBytes(StandardCharsets.UTF_8));
//             return HexFormat.of().formatHex(digest);
//         } catch (Exception e) {
//             return String.valueOf(s.hashCode());
//         }
//     }

//     private String nullToEmpty(String s) {
//         return s == null ? "" : s;
//     }

//     private String waitForCached(String redisKey, int loops, long sleepMs) {
//         for (int i = 0; i < loops; i++) {
//             try {
//                 Thread.sleep(sleepMs);
//             } catch (InterruptedException ie) {
//                 Thread.currentThread().interrupt();
//                 return null;
//             }
//             String val = stringRedisTemplate.opsForValue().get(redisKey);
//             if (!StringUtils.hasText(val) || IdempotencyConstants.VALUE_PENDING.equals(val)) {
//                 continue;
//             }
//             return val;
//         }
//         return null;
//     }

//     private void writeJson(HttpServletResponse response, int status, String json) throws IOException {
//         response.setStatus(status);
//         response.setCharacterEncoding(StandardCharsets.UTF_8.name());
//         response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//         response.getWriter().write(json);
//     }
// }
