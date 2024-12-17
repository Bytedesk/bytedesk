/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 09:31:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-17 16:19:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.exception;

import org.eclipse.jetty.websocket.core.exception.WebSocketTimeoutException; // jetty
import org.springframework.http.HttpStatus;
// import org.apache.coyote.BadRequestException; // tomcat
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
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
        return ResponseEntity.ok().body(JsonResult.error("User not found, please signup first"));
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<?> handleEmailNotFoundException(EmailNotFoundException e) {
        return ResponseEntity.ok().body(JsonResult.error("Email not found, please signup first"));
    }

    @ExceptionHandler(MobileNotFoundException.class)
    public ResponseEntity<?> handleMobileNotFoundException(MobileNotFoundException e) {
        return ResponseEntity.ok().body(JsonResult.error("Mobile not found, please signup first"));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.ok().body(JsonResult.error("Resource not found, usually is uuids not exist"));
    }

    @ExceptionHandler(UserDisabledException.class)
    public ResponseEntity<?> handleUserDisabledException(UserDisabledException e) {
        return ResponseEntity.ok().body(JsonResult.error("User disabled, please contact admin"));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbiddenException(ForbiddenException e) {
        return ResponseEntity.ok().body(JsonResult.error("Forbidden to access this resource"));
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<?> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        return ResponseEntity.ok().body(JsonResult.error("User blocked, please contact admin"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException e) {
        // 
        if (e.getMessage().contains("/vip/") 
            || e.getMessage().contains("/wechat/")) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(JsonResult.error(I18Consts.I18N_VIP_REST_API, 405, false));
        }
        // 
        return ResponseEntity.ok().body(JsonResult.error(e.getMessage(),404));
        // // 如果你确定要进行后端跳转，并且你的应用支持这种做法，你可以使用以下方式：
        // String redirectUrl = "/error/404.html";
        // // 使用HttpStatus.SEE_OTHER（303）来表示重定向
        // // 也可以使用HttpStatus.FOUND（302），但303更明确地表示应使用GET方法重定向
        // return ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create(redirectUrl)).build();
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        // 方便测试，打印异常堆栈信息
        e.printStackTrace();
        log.error("not handled exception:", e.getMessage());
        // e.printStackTrace();
        return ResponseEntity.ok().body(JsonResult.error(e.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_USERNAME_OR_PASSWORD_INCORRECT));
    }

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        log.error("not handled exception:", ex);
        // ex.printStackTrace();
        return ResponseEntity.badRequest().body(JsonResult.error("Null Pointer Exception"));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.badRequest().body(JsonResult.error("Response Status Exception"));
    }

    // org.eclipse.jetty.websocket.api.exceptions.WebSocketTimeoutException: Connection Idle Timeout
    // java.util.concurrent.TimeoutException: Idle timeout expired: 30004/30000 ms
    @ExceptionHandler(WebSocketTimeoutException.class)
    public ResponseEntity<?> handleWebSocketTimeoutException(WebSocketTimeoutException ex) {
        return ResponseEntity.badRequest().body(JsonResult.error("TODO: jetty Websocket Timeout Exception"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.badRequest().body(JsonResult.error("Http Request Method Not Supported Exception", 400));
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    public ResponseEntity<?> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(JsonResult.error("Authorization Denied Exception", 403));
    }

    @ExceptionHandler(value = RequestRejectedException.class)
    public ResponseEntity<?> handleRequestRejectedException(RequestRejectedException ex) {
        return ResponseEntity.badRequest().body(JsonResult.error("Request Rejected Exception"));
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<?> handleMEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.badRequest().body(JsonResult.error("EntityNotFoundException"));
    }

    @ExceptionHandler(Exception.class)
    // @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleException(Exception e) {
        // if (bytedeskProperties.getDebug()) {
        log.error("not handled exception:", e);
        // }
        return ResponseEntity.badRequest().body(JsonResult.error("Internal Server Error"));
    }
}
