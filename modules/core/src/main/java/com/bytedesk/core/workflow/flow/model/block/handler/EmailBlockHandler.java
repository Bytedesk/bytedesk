package com.bytedesk.core.workflow.flow.model.block.handler;

import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.Attachment;
import com.bytedesk.core.workflow.flow.model.block.model.options.EmailBlockOptions;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
public class EmailBlockHandler implements BlockHandler {
    private final ObjectMapper objectMapper;
    
    public EmailBlockHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getType() {
        return BlockType.EMAIL.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        EmailBlockOptions options = objectMapper.convertValue(block.getOptions(), EmailBlockOptions.class);
        Map<String, Object> result = new HashMap<>(context);
        
        try {
            JavaMailSender mailSender = createMailSender(options);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(options.getFrom());
            helper.setTo(options.getTo());
            helper.setSubject(processTemplate(options.getSubject(), context));
            helper.setText(processTemplate(options.getBody(), context), true);
            
            if (options.getAttachments() != null) {
                for (Attachment attachment : options.getAttachments()) {
                    addAttachment(helper, attachment);
                }
            }
            
            mailSender.send(message);
            result.put("success", true);
            result.put("message", "Email sent successfully");
            
        } catch (Exception e) {
            log.error("Failed to send email", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            EmailBlockOptions options = objectMapper.convertValue(block.getOptions(), EmailBlockOptions.class);
            return options.getFrom() != null && 
                   options.getTo() != null && 
                   options.getSubject() != null &&
                   options.getBody() != null &&
                   options.getProvider() != null;
        } catch (Exception e) {
            return false;
        }
    }

    private JavaMailSender createMailSender(EmailBlockOptions options) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Map<String, String> credentials = options.getCredentials();
        
        switch (options.getProvider().getType().toUpperCase()) {
            case "SMTP":
                configureSMTP(mailSender, credentials);
                break;
            case "SENDGRID":
                configureSendGrid(mailSender, credentials);
                break;
            default:
                throw new IllegalArgumentException("Unsupported email provider: " + options.getProvider().getType());
        }
        
        return mailSender;
    }

    private void configureSMTP(JavaMailSenderImpl mailSender, Map<String, String> credentials) {
        mailSender.setHost(credentials.get("host"));
        mailSender.setPort(Integer.parseInt(credentials.get("port")));
        mailSender.setUsername(credentials.get("username"));
        mailSender.setPassword(credentials.get("password"));
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
    }

    private void configureSendGrid(JavaMailSenderImpl mailSender, Map<String, String> credentials) {
        mailSender.setHost("smtp.sendgrid.net");
        mailSender.setPort(587);
        mailSender.setUsername("apikey");
        mailSender.setPassword(credentials.get("apiKey"));
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
    }

    private void addAttachment(MimeMessageHelper helper, Attachment attachment) throws Exception {
        if (attachment.getContent() != null) {
            byte[] content = Base64.getDecoder().decode(attachment.getContent());
            helper.addAttachment(
                attachment.getFilename(),
                new ByteArrayResource(content),
                attachment.getType()
            );
        } else if (attachment.getUrl() != null) {
            log.warn("URL attachments not implemented yet");
        }
    }

    private String processTemplate(String template, Map<String, Object> context) {
        if (template == null) return null;
        
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            if (template.contains(placeholder)) {
                template = template.replace(placeholder, String.valueOf(entry.getValue()));
            }
        }
        return template;
    }
} 
