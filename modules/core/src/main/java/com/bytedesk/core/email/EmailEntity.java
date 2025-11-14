/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-26 12:43:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.email;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({EmailEntityListener.class})
@Table(name = "bytedesk_core_email")
public class EmailEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String name;

    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    // 邮件服务提供商，如 QQ/GMAIL/网易 等
    @Builder.Default
    @Column(name = "email_provider")
    private String provider = EmailProviderEnum.QQ.name();

    // 用途类型：在线客服接待/工单客服 等
    @Builder.Default
    @Column(name = "email_type")
    private String type = EmailTypeEnum.TICKET.name();

    // 邮箱协议类型：IMAP/POP3/SMTP/EXCHANGE
    @Builder.Default
    @Column(name = "email_protocol")
    private String protocol = EmailProtocolEnum.IMAP.name();

    // 邮箱地址
    private String emailAddress;

    // 邮箱密码或授权码
    private String emailPassword;

    // SMTP服务器地址
    private String smtpHost;

    // SMTP服务器端口
    @Builder.Default
    private Integer smtpPort = 587;

    // 是否启用SSL
    @Builder.Default
    private Boolean smtpSslEnabled = true;

    // 是否启用TLS
    @Builder.Default
    private Boolean smtpTlsEnabled = true;

    // IMAP服务器地址
    private String imapHost;

    // IMAP服务器端口
    @Builder.Default
    private Integer imapPort = 993;

    // IMAP是否启用SSL
    @Builder.Default
    private Boolean imapSslEnabled = true;

    // POP3服务器地址
    private String pop3Host;

    // POP3服务器端口
    @Builder.Default
    private Integer pop3Port = 995;

    // POP3是否启用SSL
    @Builder.Default
    private Boolean pop3SslEnabled = true;

    // Exchange配置
    // Exchange服务器地址
    private String exchangeHost;

    // Exchange服务器端口
    @Builder.Default
    private Integer exchangePort = 993;

    // Exchange是否启用SSL
    @Builder.Default
    private Boolean exchangeSslEnabled = true;

    // 发件人显示名称
    private String senderName;
    
    // 邮件显示名称（用于发送邮件时的显示名）
    private String displayName;

    // 邮件同步间隔（分钟）
    @Builder.Default
    private Integer syncInterval = 5;

    // 是否自动同步邮件
    @Builder.Default
    private Boolean autoSyncEnabled = true;

    // 是否自动回复
    @Builder.Default
    private Boolean autoReplyEnabled = false;

    // 自动回复内容
    @Column(length = 1000)
    private String autoReplyContent;

    // 最后同步时间
    private ZonedDateTime lastSyncTime;

    // 连接状态：连接成功/连接失败
    @Builder.Default
    private String connectionStatus = EmailConnectionStatusEnum.DISCONNECTED.name();

    // 连接错误信息
    @Column(length = 500)
    private String connectionError;

    // 是否启用，状态：启用/禁用
    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    @Builder.Default
    @Column(name = "is_debug")
    private Boolean debug = true;

    // 关联的工作组ID
    private String workgroupUid;
}
