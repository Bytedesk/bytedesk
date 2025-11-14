/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-12 15:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-12 15:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.email;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseExtra;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;

/**
 * 邮件统一额外信息存储类
 * 用于解析 VisitorRequest.extra 和 ThreadEntity.extra 字段中的信息，特别是当 client/channel 为 EMAIL 时
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EmailExtra extends BaseExtra {
    
    private static final long serialVersionUID = 1L;

    // ==================== 邮件基础字段 ====================
    /**
     * 邮件配置ID
     */
    private String emailConfigUid;
    
    /**
     * 邮件地址
     */
    private String emailAddress;
    
    /**
     * 邮件协议类型
     */
    private String protocol;
    
    /**
     * 邮件消息ID
     */
    private String messageId;
    
    /**
     * 邮件主题
     */
    private String subject;
    
    /**
     * 发件人邮箱地址
     */
    private String fromAddress;
    
    /**
     * 发件人姓名
     */
    private String fromName;
    
    /**
     * 收件人邮箱地址
     */
    private String toAddresses;
    
    /**
     * 抄送邮箱地址
     */
    private String ccAddresses;
    
    /**
     * 密送邮箱地址
     */
    private String bccAddresses;
    
    /**
     * 邮件内容（纯文本）
     */
    private String contentText;
    
    /**
     * 邮件内容（HTML）
     */
    private String contentHtml;
    
    /**
     * 邮件发送/接收时间
     */
    private String emailDate;
    
    /**
     * 是否有附件
     */
    private Boolean hasAttachments;
    
    /**
     * 附件信息
     */
    private String attachments;
    
    /**
     * 邮件大小（字节）
     */
    private Long emailSize;
    
    /**
     * 邮件状态
     */
    private String status;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 回复的原邮件ID
     */
    private String replyToMessageId;
    
    /**
     * 转发的原邮件ID
     */
    private String forwardFromMessageId;
    
    /**
     * 邮件线程ID
     */
    private String threadId;
    
    /**
     * 将对象转换为JSON字符串
     * @return JSON字符串
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }
    
    /**
     * 从JSON字符串解析对象
     * @param jsonString JSON字符串
     * @return EmailExtra对象
     */
    public static EmailExtra fromJson(String jsonString) {
        return JSON.parseObject(jsonString, EmailExtra.class);
    }
} 