/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-05 10:00:00
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
package com.bytedesk.core.sms;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 短信发送结果
 */
@Data
@AllArgsConstructor
public class SmsSendResult {
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 错误类型
     */
    private SendCodeErrorType errorType;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 成功结果
     */
    public static SmsSendResult success() {
        return new SmsSendResult(true, null, null);
    }
    
    /**
     * 失败结果
     */
    public static SmsSendResult failure(SendCodeErrorType errorType, String errorMessage) {
        return new SmsSendResult(false, errorType, errorMessage);
    }
    
    /**
     * 短信发送错误类型枚举
     */
    public enum SendCodeErrorType {
        /** 发送过于频繁 */
        TOO_FREQUENT,
        /** 验证码已发送 */
        ALREADY_SENT,
        /** 发送失败 */
        SEND_FAILED
    }
}
