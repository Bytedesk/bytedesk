/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 09:31:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-26 15:06:12
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

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.bytedesk.core.utils.JsonResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {
    
    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<?> handleEmailExistsException(EmailExistsException e) {
        return ResponseEntity.ok().body(JsonResult.error("Email already exists, please login or use another one"));
    }

    @ExceptionHandler(MobileExistsException.class)
    public ResponseEntity<?> handleMobileExistsException(MobileExistsException e) {
        return ResponseEntity.ok().body(JsonResult.error("Mobile already exists, please login or use another one"));
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
        return ResponseEntity.ok().body(JsonResult.error(
                "Api Resource not found, or It's a vip api, you should contact 270580156@qq.com or visit http://www.weiyuai.cn",
                404));
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.ok().body(JsonResult.error("Username or password is incorrect"));
    }

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<Object> handleNullPointerException(NullPointerException ex) {
        return ResponseEntity.badRequest().body(JsonResult.error("Null Pointer Exception"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("not handled exception:", e);
        return ResponseEntity.badRequest().body(JsonResult.error("Internal Server Error"));
    }
}
