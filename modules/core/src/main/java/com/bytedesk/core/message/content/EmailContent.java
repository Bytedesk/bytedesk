/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-12 16:39:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-12 18:52:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message.content;

import java.util.List;
import java.util.ArrayList;

import com.bytedesk.core.base.BaseContent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 邮件消息内容类
 * 用于存储邮件的核心信息，包括主题、内容、附件等
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class EmailContent extends BaseContent {
    
    private static final long serialVersionUID = 1L;
    
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
     * 邮件附件列表
     */
    @Builder.Default
    private List<EmailAttachment> attachments = new ArrayList<>();
    
    /**
     * 邮件标签/说明
     */
    private String label;
    
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
        @Builder.Default
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
}
