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

import org.eclipse.jetty.websocket.core.exception.WebSocketTimeoutException; // jetty
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
// import org.apache.coyote.BadRequestException; // tomcat
import org.springframework.http.ResponseEntity;
import org.springframework.jms.listener.adapter.ListenerExecutionFailedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
// import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @ControllerAdvice
@RestControllerAdvice
public class GlobalControllerAdvice {

    // @Autowired
    // private BytedeskProperties bytedeskProperties;

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
        return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_USER_SIGNUP_FIRST));
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<?> handleEmailNotFoundException(EmailNotFoundException e) {
        return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_EMAIL_SIGNUP_FIRST));
    }

    @ExceptionHandler(MobileNotFoundException.class)
    public ResponseEntity<?> handleMobileNotFoundException(MobileNotFoundException e) {
        return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_MOBILE_SIGNUP_FIRST));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_RESOURCE_NOT_FOUND));
    }

    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<?> handleNotLoginException(NotLoginException e) {
        return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_LOGIN_REQUIRED));
    }

    @ExceptionHandler(UserDisabledException.class)
    public ResponseEntity<?> handleUserDisabledException(UserDisabledException e) {
        return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_USER_DISABLED));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbiddenException(ForbiddenException e) {
        return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_FORBIDDEN_ACCESS));
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
        // // 如果你确定要进行后端跳转，并且你的应用支持这种做法，你可以使用以下方式：
        // String redirectUrl = "/error/404.html";
        // // 使用HttpStatus.SEE_OTHER（303）来表示重定向
        // // 也可以使用HttpStatus.FOUND（302），但303更明确地表示应使用GET方法重定向
        // return
        // ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create(redirectUrl)).build();
    }

    @ExceptionHandler(ExistsException.class)
    public ResponseEntity<?> handleExistsException(ExistsException e) {
        return ResponseEntity.ok().body(JsonResult.error(e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        // 方便测试，打印异常堆栈信息
        e.printStackTrace();
        log.error("not handled exception 1:", e.getMessage());
        return ResponseEntity.ok().body(JsonResult.error(e.getMessage()));
    }

    /**
     * 乐观锁冲突：返回 409，提示客户端重试或刷新
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<?> handleOptimisticLock(ObjectOptimisticLockingFailureException e) {
        log.warn("Optimistic locking failure: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonResult.error("资源已被并发修改，请刷新后重试", 409));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        // 特别处理敏感词异常
        if (e.getMessage() != null && e.getMessage().contains("敏感词")) {
            log.warn("敏感词异常: {}", e.getMessage());
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_SENSITIVE_CONTENT));
        }
        return ResponseEntity.ok().body(JsonResult.error(e.getMessage()));
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
        ex.printStackTrace();
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
        return ResponseEntity.badRequest().body(JsonResult.error(I18Consts.I18N_ENTITY_NOT_FOUND));
    }

    // 添加对异步请求不可用异常的处理
    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public ResponseEntity<?> handleAsyncRequestNotUsableException(AsyncRequestNotUsableException ex) {
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
                .body(JsonResult.error("Connection no longer available"));
    }

    @ExceptionHandler(Exception.class)
    // @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleException(Exception e) {
        // if (bytedeskProperties.getDebug()) {
        log.error("not handled exception 3:", e);
        e.printStackTrace();
        // }
        return ResponseEntity.badRequest().body(JsonResult.error(I18Consts.I18N_INTERNAL_SERVER_ERROR));
    }
}
