package com.bytedesk.core.push.sms_push;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Optional external SMS_PUSH sender extension point.
 *
 * Core SMS_PUSH flow can delegate to implementations when configured.
 */
public interface SmsPushExternalSender {

    /**
    * Unique sender key.
     */
    String getSenderKey();

    /**
     * Send validate code SMS_PUSH through external channel.
     */
    SmsSendResult sendValidateCode(String mobile, String country, String code, HttpServletRequest request);
}
