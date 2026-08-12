/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 09:31:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-12 15:42:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.exception;

import java.io.IOException;
import java.util.Locale;

import org.eclipse.jetty.websocket.core.exception.WebSocketTimeoutException; // jetty
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.jms.listener.adapter.ListenerExecutionFailedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.upload.UploadSecurityConfig;
import com.bytedesk.core.upload.storage.UploadStorageException;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.core.utils.JsonResult;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 请求体不可读（常见于 JSON 不合法、被截断、Content-Type 与实际不符、或空 body 但声明了 JSON）
     * 这类问题通常是客户端请求构造/网络中断导致，属于 4xx，不应打印 error 级别堆栈刷屏。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                log.warn("HttpMessageNotReadable: method={} uri={} contentType={} contentLength={} msg={}",
                        request.getMethod(),
                        request.getRequestURI(),
                        request.getContentType(),
                        request.getContentLengthLong(),
                        ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());
            } else {
                log.warn("HttpMessageNotReadable: {}", ex.getMessage());
            }
        } catch (Exception ignore) {
            log.warn("HttpMessageNotReadable (failed to log request context): {}", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(I18Consts.I18N_INVALID_REQUEST_BODY, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(UploadStorageException.class)
    public ResponseEntity<?> handleUploadStorageException(UploadStorageException e) {
        // 上传失败通常属于客户端输入/文件问题（类型/大小/内容校验等），返回可读提示
        return ResponseEntity.ok().body(JsonResult.error(e.getMessage(), e.getCode() == null ? 400 : e.getCode()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        String message = resolveUploadSizeExceededMessage();
        log.warn("Upload exceeded max size: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(message, HttpStatus.CONTENT_TOO_LARGE.value()));
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<?> handleMultipartException(MultipartException e) {
        Throwable rootCause = getRootCause(e);
        if (rootCause instanceof MaxUploadSizeExceededException) {
            return handleMaxUploadSizeExceededException((MaxUploadSizeExceededException) rootCause);
        }

        String message = StringUtils.hasText(e.getMessage()) ? e.getMessage() : "文件上传请求无效";
        log.warn("Multipart request failed: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(message, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(UsernameExistsException.class)
    public ResponseEntity<?> handleUsernameExistsException(UsernameExistsException e) {
        return ResponseEntity.ok().body(JsonResult.error(e.getMessage()));
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<?> handleEmailExistsException(EmailExistsException e) {
        return ResponseEntity.ok().body(JsonResult.error(e.getMessage()));
    }

    @ExceptionHandler(MobileExistsException.class)
    public ResponseEntity<?> handleMobileExistsException(MobileExistsException e) {
        return ResponseEntity.ok().body(JsonResult.error(e.getMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException e) {
        // 登录/鉴权失败：返回 401，前端统一弹出提示并可引导去登录
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(I18Consts.I18N_USER_SIGNUP_FIRST, HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<?> handleEmailNotFoundException(EmailNotFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(I18Consts.I18N_EMAIL_SIGNUP_FIRST, HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(MobileNotFoundException.class)
    public ResponseEntity<?> handleMobileNotFoundException(MobileNotFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(I18Consts.I18N_MOBILE_SIGNUP_FIRST, HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(I18Consts.I18N_RESOURCE_NOT_FOUND, 404));
    }

    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<?> handleNotLoginException(NotLoginException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(I18Consts.I18N_LOGIN_REQUIRED, HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(UserDisabledException.class)
    public ResponseEntity<?> handleUserDisabledException(UserDisabledException e) {
        // 账号被禁用：返回 403（与 token 过期的 401 区分开）
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(I18Consts.I18N_USER_DISABLED, HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbiddenException(ForbiddenException e) {
        String message = StringUtils.hasText(e.getMessage()) ? e.getMessage() : I18Consts.I18N_FORBIDDEN_ACCESS;
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
            .body(JsonResult.error(message, HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<?> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_USER_BLOCKED));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException e) {
        if (e.getMessage().contains("/vip/")) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(JsonResult.error(I18Consts.I18N_VIP_REST_API, 405, false));
        }
        //
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(JsonResult.error(e.getMessage(), 404));
    }

    @ExceptionHandler(ExistsException.class)
    public ResponseEntity<?> handleExistsException(ExistsException e) {
        return ResponseEntity.ok().body(JsonResult.error(e.getMessage()));
    }

    @ExceptionHandler(OrgMaxMembersExceededException.class)
    public ResponseEntity<?> handleOrgMaxMembersExceededException(OrgMaxMembersExceededException e) {
        log.warn("Organization member limit exceeded: orgUid={}, orgName={}, current={}, max={}",
                e.getOrgUid(),
                e.getOrgName(),
                e.getCurrentDistinctUsers(),
                e.getMaxMembers());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(e.getMessage(), HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(AgentCapacityExceededException.class)
    public ResponseEntity<?> handleAgentCapacityExceededException(AgentCapacityExceededException e) {
        String resolvedMessage = resolveRuntimeMessage(e.getMessage());
        log.warn("Agent capacity exceeded: {}", e.getMessage());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(resolvedMessage, HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(BusinessConflictException.class)
    public ResponseEntity<?> handleBusinessConflictException(BusinessConflictException e) {
        String resolvedMessage = resolveRuntimeMessage(e.getMessage());
        log.warn("Business conflict: {}", e.getMessage());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(resolvedMessage, HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(IpInWhitelistException.class)
    public ResponseEntity<?> handleIpInWhitelistException(IpInWhitelistException e) {
        // IP 在白名单中，不能加入黑名单：属于可预期的业务冲突，warn 级别不打印堆栈
        String resolvedMessage = resolveRuntimeMessage(e.getMessage());
        log.warn("IP {} is in whitelist, cannot be added to blacklist", e.getIp());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(resolvedMessage, HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(LicenseException.class)
    public ResponseEntity<?> handleLicenseException(LicenseException e) {
        // License 签名/密钥相关的错误属于环境配置问题，warn 级别，不打印堆栈
        log.warn("License error: {}", e.getMessage());
        return ResponseEntity.ok().body(JsonResult.error(e.getMessage()));
    }

    /**
     * 黑名单 IP 拦截：属于已知的访问控制业务异常，仅以 WARN 记录摘要，
     * 不打印完整堆栈，避免每次拦截都刷屏 error 日志。
     */
    @ExceptionHandler(BlackIpException.class)
    public ResponseEntity<?> handleBlackIpException(BlackIpException e) {
        log.warn("Black IP blocked: {}", e.getMessage());
        String resolvedMessage = resolveRuntimeMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(resolvedMessage, HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        if (isClientDisconnectException(e)) {
            log.debug("Client disconnected before runtime error response could be written: {}", e.getMessage());
            return ResponseEntity.noContent().build();
        }

        String rawMessage = e.getMessage();
        String resolvedMessage = resolveRuntimeMessage(rawMessage);

        // 统一 not found：避免刷 error 堆栈，返回明确 404 code
        if (rawMessage != null && rawMessage.startsWith("Entity not found for UID:")) {
            log.debug("Not found: {}", rawMessage);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(JsonResult.error(I18Consts.I18N_RESOURCE_NOT_FOUND, 404));
        }

        // WeChat access token 刷新失败（如 IP 不在白名单），属于已知外部服务问题
        // 类在 channels/wechat 模块，此处通过类名匹配避免跨模块依赖
        if ("com.bytedesk.wechat.app.exception.WeChatAccessTokenRefreshException"
                .equals(e.getClass().getName())) {
            log.warn("WeChat access token refresh failed: {}", rawMessage);
            return ResponseEntity.ok().body(JsonResult.error(rawMessage));
        }

        // WeChat API 调用返回的业务错误（errcode 非 0），已在源头以 WARN 记录
        // 类在 channels/wechat 模块，此处通过类名匹配避免跨模块依赖
        if ("com.bytedesk.wechat.mp.exception.WeChatApiException"
                .equals(e.getClass().getName())) {
            log.debug("WeChat API error caught by GlobalExceptionHandler fallback: {}", rawMessage);
            return ResponseEntity.ok().body(JsonResult.error(rawMessage));
        }

        // 重复创建类业务冲突，返回更明确的错误码和可读文案
        if (I18Consts.I18N_AGENT_EXISTS.equals(rawMessage)) {
            log.warn("Business conflict: {}", rawMessage);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(JsonResult.error(resolvedMessage, HttpStatus.CONFLICT.value()));
        }

        // 无可用测试分机号，属于已知业务异常
        // 类在 enterprise/call 模块，此处通过类名匹配避免跨模块依赖
        if ("com.bytedesk.call.extension.exception.NoAvailableExtensionException"
                .equals(e.getClass().getName())) {
            log.warn("No available extension: {}", rawMessage);
            return ResponseEntity.ok().body(JsonResult.error(resolvedMessage));
        }

        // 对于已知的业务异常类型，使用debug级别而不是error级别
        if (e instanceof org.springframework.security.access.AccessDeniedException) {
            log.debug("Access denied: {}", rawMessage);
        } else if (rawMessage != null && rawMessage.contains("already exists")) {
            log.debug("Duplicate entry exception: {}", rawMessage);
        } else {
            // 其他未显式处理的运行时异常
            log.error("not handled exception", e);
        }
        return ResponseEntity.ok().body(JsonResult.error(resolvedMessage));
    }

    private String resolveRuntimeMessage(String message) {
        String normalizedMessage = normalizeBusinessMessageKey(message);
        if (normalizedMessage == null || normalizedMessage.isBlank()) {
            return I18Consts.I18N_INTERNAL_SERVER_ERROR;
        }

        if (normalizedMessage.startsWith(I18Consts.I18N_PREFIX)) {
            String translated = tryTranslateI18nKey(normalizedMessage);
            if (!normalizedMessage.equals(translated)) {
                return translated;
            }
        }

        return normalizedMessage;
    }

    private String resolveUploadSizeExceededMessage() {
        try {
            UploadSecurityConfig uploadSecurityConfig = ApplicationContextHolder.getBean(UploadSecurityConfig.class);
            if (uploadSecurityConfig != null) {
                return "文件过大，最大支持" + uploadSecurityConfig.getMaxFileSizeDescription();
            }
        } catch (Exception ex) {
            log.debug("Resolve upload max size config failed: {}", ex.getMessage());
        }
        return "文件过大，请压缩后重试";
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    private String normalizeBusinessMessageKey(String message) {
        if (message == null || message.isBlank()) {
            return message;
        }
        if (message.startsWith(I18Consts.I18N_PREFIX)) {
            return message;
        }
        if (message.contains("orgUid should not be null")) {
            return I18Consts.I18N_ORG_UID_REQUIRED;
        }
        if (message.contains("No permission to access data of other organizations")) {
            return I18Consts.I18N_ORGANIZATION_ACCESS_DENIED;
        }
        return message;
    }

    private Integer resolveBusinessStatusCode(String message) {
        String normalizedMessage = normalizeBusinessMessageKey(message);
        if (I18Consts.I18N_ORGANIZATION_ACCESS_DENIED.equals(normalizedMessage)) {
            return HttpStatus.FORBIDDEN.value();
        }
        if (I18Consts.I18N_ORG_UID_REQUIRED.equals(normalizedMessage)) {
            return HttpStatus.BAD_REQUEST.value();
        }
        return null;
    }

    private String tryTranslateI18nKey(String key) {
        try {
            if (!ApplicationContextHolder.isInitialized()) {
                return key;
            }
            String messageKey = key;
            Object[] args = null;
            int separatorIndex = key.indexOf(I18Consts.I18N_ARG_SEPARATOR);
            if (separatorIndex >= 0) {
                messageKey = key.substring(0, separatorIndex);
                String rawArgs = key.substring(separatorIndex + I18Consts.I18N_ARG_SEPARATOR.length());
                args = rawArgs.isEmpty() ? new Object[0] : rawArgs.split("\\|", -1);
            }
            MessageSource messageSource = ApplicationContextHolder.getBean(MessageSource.class);
            return messageSource.getMessage(messageKey, args, key, LocaleContextHolder.getLocale());
        } catch (Exception ex) {
            log.debug("Failed to translate i18n key: {}", key);
            return key;
        }
    }

    /**
     * 乐观锁冲突：返回 409，提示客户端重试或刷新
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<?> handleOptimisticLock(ObjectOptimisticLockingFailureException e) {
        log.warn("Optimistic locking failure: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(I18Consts.I18N_RESOURCE_CONCURRENTLY_MODIFIED, 409));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        // 特别处理敏感词异常
        if (e.getMessage() != null && e.getMessage().contains("敏感词")) {
            log.warn("敏感词异常: {}", e.getMessage());
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_SENSITIVE_CONTENT));
        }
        String normalizedMessage = normalizeBusinessMessageKey(e.getMessage());
        String resolvedMessage = resolveRuntimeMessage(normalizedMessage);
        Integer statusCode = resolveBusinessStatusCode(normalizedMessage);
        if (statusCode != null) {
            log.warn("IllegalArgumentException: {}", normalizedMessage);
            return ResponseEntity.status(statusCode)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(JsonResult.error(resolvedMessage, statusCode));
        }
        return ResponseEntity.ok().body(JsonResult.error(resolvedMessage));
    }

    /**
     * 数据库约束违反：字段超长、唯一键冲突等，返回可读提示而不是原始 SQL 堆栈。
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        Throwable root = e.getMostSpecificCause();
        String rootMsg = root != null ? root.getMessage() : e.getMessage();
        log.warn("DataIntegrityViolation: {}", rootMsg);
        if (rootMsg != null) {
            // 字段超长：Data truncation: Data too long for column 'xxx'
            if (rootMsg.contains("Data too long for column")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(I18Consts.I18N_INPUT_TOO_LONG, HttpStatus.BAD_REQUEST.value()));
            }
            // 唯一键冲突：Duplicate entry 'xxx' for key 'yyy'
            if (rootMsg.contains("Duplicate entry")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(I18Consts.I18N_DATA_ALREADY_EXISTS, HttpStatus.CONFLICT.value()));
            }
            // 外键约束：Cannot add or update a child row / Cannot delete or update a parent row
            if (rootMsg.contains("foreign key constraint") || rootMsg.contains("a foreign key constraint")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(I18Consts.I18N_DATA_RELATION_CONSTRAINT_VIOLATED, HttpStatus.BAD_REQUEST.value()));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
            .body(JsonResult.error(I18Consts.I18N_DATA_SAVE_FAILED, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<?> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException e) {
        Throwable cause = e.getCause();
        // 如果底层是 IllegalArgumentException，则委托给专门的 handler 处理
        // 典型场景：BaseSpecification.getBasicPredicates 中的组织访问校验抛出的 IllegalArgumentException
        // 被 Spring Data JPA 包装为 InvalidDataAccessApiUsageException
        if (cause instanceof IllegalArgumentException) {
            return handleIllegalArgumentException((IllegalArgumentException) cause);
        }
        String rawMessage = cause != null ? cause.getMessage() : e.getMessage();
        String normalizedMessage = normalizeBusinessMessageKey(rawMessage);
        String resolvedMessage = resolveRuntimeMessage(normalizedMessage);
        Integer statusCode = resolveBusinessStatusCode(normalizedMessage);

        if (statusCode != null) {
            log.warn("InvalidDataAccessApiUsageException: {}", normalizedMessage);
            return ResponseEntity.status(statusCode)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(JsonResult.error(resolvedMessage, statusCode));
        }
        if (normalizedMessage != null && normalizedMessage.startsWith(I18Consts.I18N_PREFIX)) {
            log.warn("InvalidDataAccessApiUsageException: {}", normalizedMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(JsonResult.error(resolvedMessage, HttpStatus.BAD_REQUEST.value()));
        }
        log.error("InvalidDataAccessApiUsageException", e);
        return ResponseEntity.badRequest().body(JsonResult.error(resolvedMessage));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<?> handleResourceAccessException(ResourceAccessException e) {
        // 典型场景：第三方 OAuth/HTTP 接口瞬时网络抖动，按 503 返回并降级日志级别
        log.warn("Upstream resource access failed: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(I18Consts.I18N_EXTERNAL_SERVICE_TEMPORARILY_UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE.value()));
    }

    /**
     * 不支持/未实现的操作：返回 501，避免落入 RuntimeException 兜底刷 error 堆栈。
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<?> handleUnsupportedOperationException(UnsupportedOperationException e) {
        String message = e.getMessage();
        if (message == null || message.isBlank()) {
            message = I18Consts.I18N_OPERATION_NOT_SUPPORTED;
        }
        log.warn("UnsupportedOperationException: {}", message);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(message, HttpStatus.NOT_IMPLEMENTED.value()));
    }

    // 添加对ListenerExecutionFailedException的处理
    @ExceptionHandler(ListenerExecutionFailedException.class)
    public ResponseEntity<?> handleListenerExecutionFailedException(ListenerExecutionFailedException e) {
        log.error("JMS监听器执行失败: {}", e.getMessage());
        // 检查是否是敏感词导致的异常
        if (e.getCause() instanceof IllegalArgumentException &&
                e.getCause().getMessage() != null &&
                e.getCause().getMessage().contains("敏感词")) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_SENSITIVE_CONTENT));
        }
        return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_MESSAGE_PROCESSING_FAILED));
    }

    // 添加自定义TabooException处理
    // 添加自定义TabooException处理
    @ExceptionHandler(TabooException.class)
    public ResponseEntity<?> handleTabooException(TabooException e) {
        log.warn("敏感词异常: {}", e.getMessage());

        // 获取当前请求
        // HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
        //         .getRequest();
        // String acceptHeader = request.getHeader("Accept");

        // 检查是否是SSE请求，针对SSE请求返回text/event-stream类型
        // if (acceptHeader != null && acceptHeader.contains(MediaType.TEXT_EVENT_STREAM_VALUE)) {
        //     // 对于SSE请求，使用text/event-stream媒体类型
        //     String sseErrorData = "data: {\"error\":true,\"message\":\"" + I18Consts.I18N_SENSITIVE_CONTENT + "\"}\n\n";
        //     return ResponseEntity
        //             .status(HttpStatus.OK)
        //             .contentType(MediaType.TEXT_EVENT_STREAM)
        //             .body(sseErrorData);
        // }

        // 对于普通请求，使用JSON响应
        return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_SENSITIVE_CONTENT));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_USERNAME_OR_PASSWORD_INCORRECT));
    }

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        log.error("not handled exception 2:", ex);
        return ResponseEntity.badRequest().body(JsonResult.error(I18Consts.I18N_NULL_POINTER_EXCEPTION));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.badRequest().body(JsonResult.error(I18Consts.I18N_RESPONSE_STATUS_EXCEPTION));
    }

    @ExceptionHandler(WebSocketTimeoutException.class)
    public ResponseEntity<?> handleWebSocketTimeoutException(WebSocketTimeoutException ex) {
        return ResponseEntity.badRequest().body(JsonResult.error(I18Consts.I18N_WEBSOCKET_TIMEOUT_EXCEPTION));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.badRequest().body(JsonResult.error(I18Consts.I18N_HTTP_METHOD_NOT_SUPPORTED, 400));
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    public ResponseEntity<?> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(JsonResult.error(I18Consts.I18N_AUTHORIZATION_DENIED, 403));
    }

    @ExceptionHandler(value = RequestRejectedException.class)
    public ResponseEntity<?> handleRequestRejectedException(RequestRejectedException ex) {
        return ResponseEntity.badRequest().body(JsonResult.error(I18Consts.I18N_REQUEST_REJECTED));
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<?> handleMEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.badRequest().body(JsonResult.error(I18Consts.I18N_ENTITY_NOT_FOUND, 400));
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<?> handleHttpMessageNotWritableException(HttpMessageNotWritableException ex) {
        if (isClientDisconnectException(ex)) {
            log.debug("Client disconnected while writing response: {}", ex.getMessage());
            return ResponseEntity.noContent().build();
        }

        log.error("HttpMessageNotWritableException while writing response", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error(I18Consts.I18N_INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    // 添加对异步请求不可用异常的处理
    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public ResponseEntity<?> handleAsyncRequestNotUsableException(AsyncRequestNotUsableException ex) {
        if (isClientDisconnectException(ex)) {
            log.debug("Async request is no longer usable because the client disconnected: {}", ex.getMessage());
            return ResponseEntity.noContent().build();
        }

        log.warn("AsyncRequestNotUsableException: SSE connection is no longer usable - {}", ex.getMessage());
        
        // 获取当前请求上下文
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String acceptHeader = request.getHeader("Accept");
                
                // 检查是否是SSE请求
                if (acceptHeader != null && acceptHeader.contains(MediaType.TEXT_EVENT_STREAM_VALUE)) {
                    // 对于SSE请求，返回空响应或者适当的错误信息
                    log.debug("SSE connection closed by client, not sending response");
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
                }
            }
        } catch (Exception e) {
            log.debug("Unable to get request context: {}", e.getMessage());
        }
        
        // 对于其他情况，返回正常的错误响应
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(JsonResult.error(I18Consts.I18N_CONNECTION_NO_LONGER_AVAILABLE));
    }

    @ExceptionHandler(Exception.class)
    // @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleException(Exception e) {
        if (isClientDisconnectException(e)) {
            log.debug("Client disconnected before error response could be written: {}", e.getMessage());
            return ResponseEntity.noContent().build();
        }

        // if (bytedeskProperties.getDebug()) {
        log.error("not handled exception 3:", e);
        // }
        return ResponseEntity.badRequest().body(JsonResult.error(I18Consts.I18N_INTERNAL_SERVER_ERROR));
    }

    private boolean isClientDisconnectException(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof AsyncRequestNotUsableException) {
                return true;
            }

            String className = current.getClass().getName();
            if (className.contains("DisconnectedClient") || className.endsWith("EofException")) {
                return true;
            }

            if (current instanceof IOException) {
                String message = current.getMessage();
                if (message == null) {
                    return true;
                }

                String normalized = message.toLowerCase(Locale.ROOT);
                if (normalized.contains("broken pipe")
                        || normalized.contains("connection reset")
                        || normalized.contains("forcibly closed")
                        || normalized.contains("response not usable after response errors")) {
                    return true;
                }
            }

            current = current.getCause();
        }
        return false;
    }
}
