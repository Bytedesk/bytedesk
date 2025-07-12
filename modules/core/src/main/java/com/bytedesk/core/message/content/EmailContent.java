/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-12 16:39:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-12 16:43:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message.content;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;

import com.bytedesk.core.base.BaseContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 邮件消息内容类
 * 用于存储邮件的完整信息，包括发件人、收件人、主题、内容、附件等
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class EmailContent extends BaseContent {
    
    /**
     * 邮件主题
     */
    private String subject;
    
    /**
     * 邮件正文内容（支持HTML格式）
     */
    private String content;
    
    /**
     * 邮件纯文本内容（不含HTML标签）
     */
    private String textContent;
    
    /**
     * 发件人邮箱地址
     */
    private String fromEmail;
    
    /**
     * 发件人显示名称
     */
    private String fromName;
    
    /**
     * 收件人邮箱地址列表
     */
    private List<String> toEmails = new ArrayList<>();
    
    /**
     * 抄送邮箱地址列表
     */
    private List<String> ccEmails = new ArrayList<>();
    
    /**
     * 密送邮箱地址列表
     */
    private List<String> bccEmails = new ArrayList<>();
    
    /**
     * 邮件附件列表
     */
    private List<EmailAttachment> attachments = new ArrayList<>();
    
    /**
     * 邮件优先级
     * 可选值：HIGH, NORMAL, LOW
     */
    private String priority = "NORMAL";
    
    /**
     * 邮件状态
     * 可选值：DRAFT, SENDING, SENT, FAILED, READ, REPLIED, FORWARDED
     */
    private String status = "DRAFT";
    
    /**
     * 邮件发送时间
     */
    private ZonedDateTime sentAt;
    
    /**
     * 邮件接收时间
     */
    private ZonedDateTime receivedAt;
    
    /**
     * 邮件阅读时间
     */
    private ZonedDateTime readAt;
    
    /**
     * 邮件回复时间
     */
    private ZonedDateTime repliedAt;
    
    /**
     * 邮件转发时间
     */
    private ZonedDateTime forwardedAt;
    
    /**
     * 邮件标签/分类
     */
    private String label;
    
    /**
     * 邮件标签列表
     */
    private List<String> tags = new ArrayList<>();
    
    /**
     * 邮件重要性标记
     * 可选值：IMPORTANT, NORMAL, LOW
     */
    private String importance = "NORMAL";
    
    /**
     * 是否已读
     */
    private Boolean isRead = false;
    
    /**
     * 是否已回复
     */
    private Boolean isReplied = false;
    
    /**
     * 是否已转发
     */
    private Boolean isForwarded = false;
    
    /**
     * 是否已删除
     */
    private Boolean isDeleted = false;
    
    /**
     * 是否已归档
     */
    private Boolean isArchived = false;
    
    /**
     * 邮件大小（字节）
     */
    private String size;
    
    /**
     * 邮件唯一标识符（Message-ID）
     */
    private String messageId;
    
    /**
     * 回复邮件ID
     */
    private String inReplyTo;
    
    /**
     * 引用邮件ID列表
     */
    private List<String> references = new ArrayList<>();
    
    /**
     * 邮件线程ID
     */
    private String threadId;
    
    /**
     * 邮件文件夹/分类
     */
    private String folder;
    
    /**
     * 邮件额外信息（JSON格式）
     */
    private String extra;
    
    /**
     * 邮件附件类
     */
    @Getter
    @Setter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmailAttachment {
        
        /**
         * 附件文件名
         */
        private String filename;
        
        /**
         * 附件显示名称
         */
        private String name;
        
        /**
         * 附件MIME类型
         */
        private String mimeType;
        
        /**
         * 附件大小（字节）
         */
        private String size;
        
        /**
         * 附件URL
         */
        private String url;
        
        /**
         * 附件哈希值（SHA256）
         */
        private String hash;
        
        /**
         * 附件描述
         */
        private String description;
        
        /**
         * 附件内容ID（用于内联附件）
         */
        private String contentId;
        
        /**
         * 是否内联附件
         */
        private Boolean isInline = false;
    }
    
    /**
     * 从JSON字符串反序列化为EmailContent对象
     * @param json JSON字符串
     * @return EmailContent对象，如果解析失败返回null
     */
    public static EmailContent fromJson(String json) {
        return BaseContent.fromJson(json, EmailContent.class);
    }
    
    /**
     * 添加收件人
     * @param email 邮箱地址
     */
    public void addToEmail(String email) {
        if (toEmails == null) {
            toEmails = new ArrayList<>();
        }
        toEmails.add(email);
    }
    
    /**
     * 添加抄送
     * @param email 邮箱地址
     */
    public void addCcEmail(String email) {
        if (ccEmails == null) {
            ccEmails = new ArrayList<>();
        }
        ccEmails.add(email);
    }
    
    /**
     * 添加密送
     * @param email 邮箱地址
     */
    public void addBccEmail(String email) {
        if (bccEmails == null) {
            bccEmails = new ArrayList<>();
        }
        bccEmails.add(email);
    }
    
    /**
     * 添加附件
     * @param attachment 附件对象
     */
    public void addAttachment(EmailAttachment attachment) {
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        attachments.add(attachment);
    }
    
    /**
     * 添加标签
     * @param tag 标签
     */
    public void addTag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(tag);
    }
    
    /**
     * 添加引用邮件ID
     * @param reference 引用邮件ID
     */
    public void addReference(String reference) {
        if (references == null) {
            references = new ArrayList<>();
        }
        references.add(reference);
    }
    
    /**
     * 获取所有收件人邮箱地址（包括收件人、抄送、密送）
     * @return 所有邮箱地址列表
     */
    public List<String> getAllRecipients() {
        List<String> allRecipients = new ArrayList<>();
        if (toEmails != null) {
            allRecipients.addAll(toEmails);
        }
        if (ccEmails != null) {
            allRecipients.addAll(ccEmails);
        }
        if (bccEmails != null) {
            allRecipients.addAll(bccEmails);
        }
        return allRecipients;
    }
    
    /**
     * 获取附件总大小
     * @return 附件总大小（字节）
     */
    public long getAttachmentsTotalSize() {
        if (attachments == null || attachments.isEmpty()) {
            return 0;
        }
        return attachments.stream()
                .mapToLong(attachment -> {
                    try {
                        return Long.parseLong(attachment.getSize());
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .sum();
    }
    
    /**
     * 检查是否有附件
     * @return 是否有附件
     */
    public boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }
    
    /**
     * 检查是否为高优先级邮件
     * @return 是否为高优先级
     */
    public boolean isHighPriority() {
        return "HIGH".equals(priority);
    }
    
    /**
     * 检查是否为重要邮件
     * @return 是否为重要邮件
     */
    public boolean isImportant() {
        return "IMPORTANT".equals(importance);
    }
}
