/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-31 15:29:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-05 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push.service;

import org.springframework.stereotype.Service;

import com.bytedesk.core.sms.SmsSendResult;
import com.bytedesk.core.sms.SmsSendService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Push短信服务 - 委托给SmsSendService
 * @deprecated 请直接使用 {@link SmsSendService}
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Deprecated
public class PushServiceSms {

    private final SmsSendService smsSendService;

    /**
     * 发送短信
     * @deprecated 请直接使用 {@link SmsSendService#sendSms}
     */
    @Deprecated
    public boolean sendSms(String mobile, String country, String content, HttpServletRequest request) {
        log.warn("PushServiceSms.sendSms is deprecated, please use SmsSendService.sendSms instead");
        return smsSendService.sendSms(mobile, country, content, request);
    }
    
    /**
     * 发送短信并返回详细结果
     * @deprecated 请直接使用 {@link SmsSendService#sendSmsWithResult}
     */
    @Deprecated
    public PushSendResult sendSmsWithResult(String mobile, String country, String content, HttpServletRequest request) {
        log.warn("PushServiceSms.sendSmsWithResult is deprecated, please use SmsSendService.sendSmsWithResult instead");
        SmsSendResult result = smsSendService.sendSmsWithResult(mobile, country, content, request);
        if (result.isSuccess()) {
            return PushSendResult.success();
        }
        return PushSendResult.failure(PushSendResult.SendCodeErrorType.SEND_FAILED, result.getErrorMessage());
    }

    /**
     * 发送验证码
     * @deprecated 请直接使用 {@link SmsSendService#sendValidateCode}
     */
    @Deprecated
    public PushSendResult sendValidateCode(String mobile, String country, String code) {
        log.warn("PushServiceSms.sendValidateCode is deprecated, please use SmsSendService.sendValidateCode instead");
        SmsSendResult result = smsSendService.sendValidateCode(mobile, country, code);
        if (result.isSuccess()) {
            return PushSendResult.success();
        }
        return PushSendResult.failure(PushSendResult.SendCodeErrorType.SEND_FAILED, result.getErrorMessage());
    }
    
}
