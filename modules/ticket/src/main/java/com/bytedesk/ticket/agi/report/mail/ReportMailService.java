package com.bytedesk.ticket.agi.report.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.bytedesk.ticket.agi.report.TicketReportDTO;

import lombok.extern.slf4j.Slf4j;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ReportMailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    @Autowired
    private ReportMailConfig mailConfig;
    
    @Retryable(
        value = {MessagingException.class},
        maxAttemptsExpression = "#{@reportMailConfig.retry.maxAttempts}",
        backoff = @Backoff(
            delayExpression = "#{@reportMailConfig.retry.initialInterval}",
            maxDelayExpression = "#{@reportMailConfig.retry.maxInterval}",
            multiplierExpression = "#{@reportMailConfig.retry.multiplier}"
        )
    )
    public void sendReport(TicketReportDTO report, byte[] reportFile, String format, String[] recipients) {
        if (!mailConfig.isEnabled()) {
            log.info("Mail sending is disabled");
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // 设置发件人和收件人
            helper.setFrom(mailConfig.getFrom());
            List<String> allRecipients = getAllRecipients(recipients);
            helper.setTo(allRecipients.toArray(new String[0]));
            
            // 设置主题
            String subject = String.format("%s %s", 
                mailConfig.getSubjectPrefix(), report.getTitle());
            helper.setSubject(subject);
            
            // 设置内容
            String content = generateEmailContent(report);
            helper.setText(content, true);
            
            // 添加附件
            String filename = generateFilename(report, format);
            helper.addAttachment(filename, new ByteArrayResource(reportFile));
            
            log.info("Sending report email to {} recipients", allRecipients.size());
            mailSender.send(message);
            log.info("Report email sent successfully");
            
        } catch (MessagingException e) {
            log.error("Failed to send report email", e);
            throw new RuntimeException("Failed to send report email", e);
        }
    }
    
    private List<String> getAllRecipients(String[] additionalRecipients) {
        List<String> recipients = new java.util.ArrayList<>();
        
        // 添加默认收件人
        if (mailConfig.getDefaultRecipients() != null) {
            recipients.addAll(mailConfig.getDefaultRecipients());
        }
        
        // 添加额外指定的收件人
        if (additionalRecipients != null) {
            recipients.addAll(Arrays.asList(additionalRecipients));
        }
        
        // 添加管理员收件人
        if (mailConfig.getAdminRecipients() != null) {
            recipients.addAll(mailConfig.getAdminRecipients());
        }
        
        return recipients.stream().distinct().toList();
    }
    
    private String generateEmailContent(TicketReportDTO report) {
        Context context = new Context();
        context.setVariables(Map.of(
            "report", report,
            "basicStats", Map.of(
                "Total Tickets", report.getTotalTickets(),
                "New Tickets", report.getNewTickets(),
                "Resolved Tickets", report.getResolvedTickets(),
                "Closed Tickets", report.getClosedTickets()
            ),
            "responseStats", Map.of(
                "Average First Response Time", String.format("%.2f minutes", report.getAvgFirstResponseTime()),
                "Average Resolution Time", String.format("%.2f minutes", report.getAvgResolutionTime()),
                "SLA Compliance Rate", String.format("%.2f%%", report.getSlaComplianceRate())
            ),
            "config", mailConfig
        ));
        
        return templateEngine.process("mail/report", context);
    }
    
    private String generateFilename(TicketReportDTO report, String format) {
        return String.format("bytedesk_ticket_report_%s_%s.%s",
            report.getType().name().toLowerCase(),
            report.getStartTime().toLocalDate(),
            format.toLowerCase());
    }
} 