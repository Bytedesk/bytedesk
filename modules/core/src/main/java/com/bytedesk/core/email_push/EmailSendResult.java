/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-23 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-23 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 */
package com.bytedesk.core.email_push;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 邮件发送结果
 */
@Data
@AllArgsConstructor
public class EmailSendResult {

    private boolean success;

    private String errorMessage;

    public static EmailSendResult success() {
        return new EmailSendResult(true, null);
    }

    public static EmailSendResult failure(String errorMessage) {
        return new EmailSendResult(false, errorMessage);
    }
}
