package com.bytedesk.ticket.comment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "bytedesk.ticket.comment")
public class CommentConfig {
    private int maxLength = 5000;  // 评论最大长度
    private int maxAttachments = 5;  // 单条评论最大附件数
    private long maxAttachmentSize = 10 * 1024 * 1024;  // 单个附件最大大小(10MB)
    private String[] allowedAttachmentTypes = {  // 允许的附件类型
        "image/jpeg", "image/png", "image/gif",
        "application/pdf", "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    };
} 