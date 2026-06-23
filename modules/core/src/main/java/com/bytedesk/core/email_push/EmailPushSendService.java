package com.bytedesk.core.email_push;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.bytedesk.core.email_provider.EmailProviderEntity;
import com.bytedesk.core.push.PushStatusEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 邮件发送服务，将邮件发送操作从 TicketNotificationService 中抽离，
 * 类似 SmsPushSendService 管理短信发送。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailPushSendService {

    private final EmailPushRepository emailPushRepository;

    /**
     * 使用指定邮件供应商发送 HTML 邮件，并记录发送历史到 EmailPushEntity。
     */
    public EmailSendResult sendEmail(EmailProviderEntity emailConfig, String to, String subject, String htmlContent, String orgUid) {
        try {
            JavaMailSenderImpl mailSender = createMailSender(emailConfig);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailConfig.getEmailAddress(), emailConfig.getDisplayName());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("email sent: to={}, subject={}", to, subject);

            saveEmailPushRecord(to, subject, htmlContent, true, null, orgUid);
            return EmailSendResult.success();
        } catch (Exception e) {
            log.warn("email send failed: to={}, subject={}, error={}", to, subject, e.getMessage());
            saveEmailPushRecord(to, subject, htmlContent, false, e.getMessage(), orgUid);
            return EmailSendResult.failure(e.getMessage());
        }
    }

    /**
     * 发送工单通知邮件（封装主题 + HTML 构建）。
     *
     * @param emailConfig         邮件供应商配置
     * @param to                  收件人邮箱
     * @param ticketNumber        工单编号
     * @param ticketTitle         工单标题
     * @param reporterName        报告人称呼
     * @param eventType           事件类型（TICKET_CREATED / TICKET_STATUS_CHANGED）
     * @param currentStatusLabel  当前状态中文标签
     * @param previousStatusLabel 原状态中文标签（仅状态变更时有效）
     */
    public void sendTicketEmail(EmailProviderEntity emailConfig, String to,
            String ticketNumber, String ticketTitle, String reporterName,
            String eventType, String currentStatusLabel, String previousStatusLabel, String orgUid) {
        String subject = buildEmailSubject(ticketNumber, eventType);
        String html = buildEmailHtml(ticketNumber, ticketTitle, reporterName,
                eventType, currentStatusLabel, previousStatusLabel);
        sendEmail(emailConfig, to, subject, html, orgUid);
    }

    // ============ 邮件内容构建（从 TicketNotificationService 迁入） ============

    static String buildEmailSubject(String ticketNumber, String eventType) {
        if ("TICKET_CREATED".equals(eventType)) {
            return "工单提交成功 - " + ticketNumber;
        }
        return "工单状态更新 - " + ticketNumber;
    }

    static String buildEmailHtml(String ticketNumber, String ticketTitle, String reporterName,
            String eventType, String currentStatusLabel, String previousStatusLabel) {
        String currentTime = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .format(java.time.ZonedDateTime.now());

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><body style=\"font-family: Arial, sans-serif; padding: 20px; max-width: 600px;\">");

        if ("TICKET_CREATED".equals(eventType)) {
            html.append("<h2 style=\"color: #333;\">工单提交成功</h2>");
            html.append("<p>尊敬的 ").append(reporterName != null ? reporterName : "访客").append("，您好！</p>");
            html.append("<p>感谢您的提交，我们的客服团队将尽快处理您的问题。</p>");
        } else {
            html.append("<h2 style=\"color: #333;\">工单状态更新</h2>");
            html.append("<p>尊敬的 ").append(reporterName != null ? reporterName : "访客").append("，您好！</p>");
            html.append("<p>您的工单状态已更新：</p>");
        }

        html.append("<table style=\"border-collapse: collapse; width: 100%; margin-top: 15px;\">");
        html.append("<tr><td style=\"padding: 10px; border: 1px solid #ddd; background: #f5f5f5; width: 100px;\">工单编号</td>");
        html.append("<td style=\"padding: 10px; border: 1px solid #ddd;\">").append(ticketNumber).append("</td></tr>");
        html.append("<tr><td style=\"padding: 10px; border: 1px solid #ddd; background: #f5f5f5;\">工单标题</td>");
        html.append("<td style=\"padding: 10px; border: 1px solid #ddd;\">").append(ticketTitle != null ? ticketTitle : "无").append("</td></tr>");
        html.append("<tr><td style=\"padding: 10px; border: 1px solid #ddd; background: #f5f5f5;\">当前状态</td>");
        html.append("<td style=\"padding: 10px; border: 1px solid #ddd; color: #1890ff;\">").append(currentStatusLabel).append("</td></tr>");

        if (previousStatusLabel != null && !previousStatusLabel.isEmpty() && !"TICKET_CREATED".equals(eventType)) {
            html.append("<tr><td style=\"padding: 10px; border: 1px solid #ddd; background: #f5f5f5;\">原状态</td>");
            html.append("<td style=\"padding: 10px; border: 1px solid #ddd;\">").append(previousStatusLabel).append("</td></tr>");
        }

        html.append("<tr><td style=\"padding: 10px; border: 1px solid #ddd; background: #f5f5f5;\">更新时间</td>");
        html.append("<td style=\"padding: 10px; border: 1px solid #ddd;\">").append(currentTime).append("</td></tr>");
        html.append("</table>");

        html.append("<p style=\"margin-top: 20px; color: #999; font-size: 12px;\">如有疑问，请联系客服。此邮件由系统自动发送，请勿回复。</p>");
        html.append("</body></html>");

        return html.toString();
    }

    // ============ 工单客服回复邮件 ============

    /**
     * 发送工单客服回复通知邮件（访客有新回复时触发）。
     *
     * @param emailConfig    邮件供应商配置
     * @param to             收件人邮箱（访客）
     * @param ticketNumber   工单编号
     * @param ticketTitle    工单标题
     * @param reporterName   报告人称呼
     * @param agentName      回复客服名称
     */
    public void sendTicketMessageEmail(EmailProviderEntity emailConfig, String to,
            String ticketNumber, String ticketTitle, String reporterName,
            String agentName, String orgUid) {
        String subject = "工单新回复 - " + ticketNumber;
        String html = buildTicketMessageEmailHtml(ticketNumber, ticketTitle, reporterName, agentName);
        sendEmail(emailConfig, to, subject, html, orgUid);
    }

    static String buildTicketMessageEmailHtml(String ticketNumber, String ticketTitle, String reporterName,
            String agentName) {
        String currentTime = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .format(java.time.ZonedDateTime.now());

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><body style=\"font-family: Arial, sans-serif; padding: 20px; max-width: 600px;\">");
        html.append("<h2 style=\"color: #333;\">工单新回复</h2>");
        html.append("<p>尊敬的 ").append(reporterName != null ? reporterName : "访客").append("，您好！</p>");
        html.append("<p>您的工单收到一条来自客服 <strong>").append(agentName != null ? agentName : "客服").append("</strong> 的新回复，请登录系统查看详情。</p>");

        html.append("<table style=\"border-collapse: collapse; width: 100%; margin-top: 15px;\">");
        html.append("<tr><td style=\"padding: 10px; border: 1px solid #ddd; background: #f5f5f5; width: 100px;\">工单编号</td>");
        html.append("<td style=\"padding: 10px; border: 1px solid #ddd;\">").append(ticketNumber).append("</td></tr>");
        html.append("<tr><td style=\"padding: 10px; border: 1px solid #ddd; background: #f5f5f5;\">工单标题</td>");
        html.append("<td style=\"padding: 10px; border: 1px solid #ddd;\">").append(ticketTitle != null ? ticketTitle : "无").append("</td></tr>");
        html.append("<tr><td style=\"padding: 10px; border: 1px solid #ddd; background: #f5f5f5;\">回复客服</td>");
        html.append("<td style=\"padding: 10px; border: 1px solid #ddd;\">").append(agentName != null ? agentName : "客服").append("</td></tr>");
        html.append("<tr><td style=\"padding: 10px; border: 1px solid #ddd; background: #f5f5f5;\">通知时间</td>");
        html.append("<td style=\"padding: 10px; border: 1px solid #ddd;\">").append(currentTime).append("</td></tr>");
        html.append("</table>");

        html.append("<p style=\"margin-top: 20px; color: #999; font-size: 12px;\">请登录系统查看完整对话。此邮件由系统自动发送，请勿回复。</p>");
        html.append("</body></html>");

        return html.toString();
    }

    // ============ 邮件发送底层 ============

    private JavaMailSenderImpl createMailSender(EmailProviderEntity emailConfig) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailConfig.getSmtpHost());
        mailSender.setPort(emailConfig.getSmtpPort());
        mailSender.setUsername(emailConfig.getEmailAddress());
        mailSender.setPassword(emailConfig.getEmailPassword());
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        if (emailConfig.getSmtpSslEnabled()) {
            if (emailConfig.getSmtpPort() == 465) {
                props.put("mail.smtp.ssl.enable", "true");
            } else {
                props.put("mail.smtp.starttls.enable", "true");
            }
        }
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        return mailSender;
    }

    private void saveEmailPushRecord(String recipientEmail, String subject, String htmlContent,
            boolean success, String errorMessage, String orgUid) {
        try {
            EmailPushEntity record = EmailPushEntity.builder()
                    .uid(UidUtils.getInstance().getUid())
                    .type("TICKET_NOTIFICATION")
                    .sender("SYSTEM")
                    .content(subject != null ? subject : "")
                    .receiver(recipientEmail)
                    .status(success ? PushStatusEnum.SUCCESS.name() : PushStatusEnum.ERROR.name())
                    .sendSuccess(success)
                    .sendMessage(errorMessage)
                    .build();
            record.setOrgUid(orgUid);
            record.setCreatedAt(BdDateUtils.now());
            record.setUpdatedAt(BdDateUtils.now());
            emailPushRepository.save(record);
            log.debug("EmailPushEntity record saved: uid={}, email={}, orgUid={}, success={}",
                    record.getUid(), recipientEmail, orgUid, success);
        } catch (Exception e) {
            log.warn("Failed to save EmailPushEntity record for email {}: {}", recipientEmail, e.getMessage());
        }
    }
}
