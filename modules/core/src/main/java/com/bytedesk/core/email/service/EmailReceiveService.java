package com.bytedesk.core.email.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * 邮件接收服务接口
 */
public interface EmailReceiveService {

    /**
     * 处理接收到的邮件
     * @param message 邮件消息
     * @throws MessagingException 邮件处理异常
     */
    void processEmail(MimeMessage message) throws MessagingException;
    
}
