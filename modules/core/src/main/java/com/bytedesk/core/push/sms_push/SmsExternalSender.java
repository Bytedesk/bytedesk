package com.bytedesk.core.push.sms_push;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Optional external SMS sender extension point.
 *
 * Core SMS flow can delegate to implementations when configured.
 */
public interface SmsExternalSender {

    /**
    * Unique sender key.
     */
    String getSenderKey();

    /**
     * Send validate code SMS through external channel.
     */
    SmsSendResult sendValidateCode(String mobile, String country, String code, HttpServletRequest request);
}
