package com.bytedesk.core.email.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bytedesk.core.email.service.EmailReceiveService;
import com.bytedesk.core.entity.Ticket;
import com.bytedesk.core.repository.TicketRepository;
import com.bytedesk.core.service.TicketService;
import com.bytedesk.core.util.UUIDUtil;

import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * 邮件接收服务实现
 */
@Service
public class EmailReceiveServiceImpl implements EmailReceiveService {
    
    private final Logger logger = LoggerFactory.getLogger(EmailReceiveServiceImpl.class);
    
    @Value("${bytedesk.mail.receiver.handler:console}")
    private String mailHandler;
    
    @Autowired(required = false)
    private TicketService ticketService;

    @Override
    public void processEmail(MimeMessage message) throws MessagingException {
        try {
            String subject = message.getSubject();
            String from = getFromAddress(message);
            String content = getContent(message);
            Date receivedDate = message.getReceivedDate();
            
            logger.info("收到邮件: 主题='{}', 来自='{}', 时间='{}'", subject, from, receivedDate);
            
            // 根据配置的处理器类型进行处理
            switch (mailHandler) {
                case "console":
                    // 仅打印到控制台
                    logger.info("邮件内容:\n{}", content);
                    break;
                    
                case "ticket":
                    // 创建工单
                    if (ticketService != null) {
                        createTicketFromEmail(subject, from, content, receivedDate);
                    } else {
                        logger.warn("未注入TicketService，无法创建工单");
                    }
                    break;
                    
                default:
                    logger.info("未知的邮件处理器类型: {}", mailHandler);
                    break;
            }
        } catch (Exception e) {
            logger.error("处理邮件时发生错误", e);
        }
    }
    
    /**
     * 获取发件人地址
     */
    private String getFromAddress(MimeMessage message) throws MessagingException {
        Address[] addresses = message.getFrom();
        if (addresses != null && addresses.length > 0) {
            if (addresses[0] instanceof InternetAddress) {
                InternetAddress ia = (InternetAddress) addresses[0];
                return ia.getAddress();
            } else {
                return addresses[0].toString();
            }
        }
        return "unknown@example.com";
    }
    
    /**
     * 提取邮件内容
     */
    private String getContent(MimeMessage message) throws MessagingException, IOException {
        Object content = message.getContent();
        
        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            StringBuilder sb = new StringBuilder();
            
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                
                if (disposition == null || !disposition.equalsIgnoreCase("attachment")) {
                    Object partContent = bodyPart.getContent();
                    if (partContent instanceof String) {
                        sb.append(partContent);
                    }
                }
            }
            
            return sb.toString();
        }
        
        return "无法解析邮件内容";
    }
    
    /**
     * 将邮件转为工单
     */
    private void createTicketFromEmail(String subject, String fromEmail, String content, Date date) {
        try {
            // 这里需要根据实际的TicketService接口来实现
            // 简单示例：
            if (ticketService != null) {
                logger.info("从邮件创建工单: {} - {}", subject, fromEmail);
                // 调用工单服务创建工单
                ticketService.createFromEmail(subject, fromEmail, content, date);
            }
        } catch (Exception e) {
            logger.error("从邮件创建工单失败", e);
        }
    }
}
