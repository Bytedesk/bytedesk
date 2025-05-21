package com.bytedesk.voc.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "bytedesk.voc")
public class VocConfig {
    
    // 反馈相关配置
    private Integer maxFeedbackLength = 5000;  // 反馈内容最大长度
    private Integer maxReplyLength = 1000;     // 回复内容最大长度
    private Integer maxRepliesPerFeedback = 100; // 每个反馈最大回复数
    
    // 通知相关配置
    private Boolean emailNotification = true;  // 是否开启邮件通知
    private String notificationEmail;          // 通知邮箱
    private List<String> adminEmails;         // 管理员邮箱列表
    
    // 审核相关配置
    private Boolean requireApproval = false;   // 是否需要审核
    private Boolean filterSensitiveWords = true; // 是否过滤敏感词
    private String sensitiveWordsFile;         // 敏感词文件路径
} 