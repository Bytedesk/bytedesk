/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-31 15:30:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-05 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push.service;

import org.springframework.stereotype.Service;

import com.bytedesk.core.email.EmailSendResult;
import com.bytedesk.core.email.EmailSendService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Push邮件服务 - 委托给EmailSendService
 * @deprecated 请直接使用 {@link EmailSendService}
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Deprecated
public class PushServiceEmail {

    private final EmailSendService emailSendService;

    /**
     * 发送邮件
     * @deprecated 请直接使用 {@link EmailSendService#sendEmail}
     */
    @Deprecated
    public boolean sendEmail(String email, String content, HttpServletRequest request) {
        log.warn("PushServiceEmail.sendEmail is deprecated, please use EmailSendService.sendEmail instead");
        return emailSendService.sendEmail(email, content, request);
    }
    
    /**
     * 发送邮件并返回详细结果
     * @deprecated 请直接使用 {@link EmailSendService#sendEmailWithResult}
     */
    @Deprecated
    public PushSendResult sendEmailWithResult(String email, String content, HttpServletRequest request) {
        log.warn("PushServiceEmail.sendEmailWithResult is deprecated, please use EmailSendService.sendEmailWithResult instead");
        EmailSendResult result = emailSendService.sendEmailWithResult(email, content, request);
        if (result.isSuccess()) {
            return PushSendResult.success();
        }
        return PushSendResult.failure(PushSendResult.SendCodeErrorType.SEND_FAILED, result.getErrorMessage());
    }

    /**
     * 通过阿里云邮件推送SDK发送
     * @deprecated 请直接使用 {@link EmailSendService#sendAliyunValidateCode}
     */
    @Deprecated
    public boolean sendAliyunValidateCode(String email, String code) {
        log.warn("PushServiceEmail.sendAliyunValidateCode is deprecated, please use EmailSendService.sendAliyunValidateCode instead");
        return emailSendService.sendAliyunValidateCode(email, code);
    }

    /**
     * 发送验证码邮件
     * @deprecated 请直接使用 {@link EmailSendService#sendJavaMailValidateCode}
     */
    @Deprecated
    public boolean sendJavaMailValidateCode(String email, String code) {
        log.warn("PushServiceEmail.sendJavaMailValidateCode is deprecated, please use EmailSendService.sendJavaMailValidateCode instead");
        return emailSendService.sendJavaMailValidateCode(email, code);
    }

    /**
     * 通过JavaMail发送
     * @deprecated 请直接使用 {@link EmailSendService#sendJavaMail}
     */
    @Deprecated
    public boolean sendJavaMail(String email, String subject, String content) {
        log.warn("PushServiceEmail.sendJavaMail is deprecated, please use EmailSendService.sendJavaMail instead");
        return emailSendService.sendJavaMail(email, subject, content);
    }

}
