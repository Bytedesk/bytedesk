package com.bytedesk.ticket.agi.report.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "bytedesk.report.mail")
public class ReportMailConfig {
    
    private String from;
    private List<String> defaultRecipients;
    private List<String> adminRecipients;
    private String subjectPrefix = "[ByteDesk Report]";
    private boolean enabled = true;
    private RetryConfig retry = new RetryConfig();
    
    @Data
    public static class RetryConfig {
        private boolean enabled = true;
        private int maxAttempts = 3;
        private long initialInterval = 1000;
        private long maxInterval = 10000;
        private double multiplier = 2.0;
    }
} 