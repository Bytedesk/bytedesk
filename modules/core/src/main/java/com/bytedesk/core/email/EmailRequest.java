/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-16 16:13:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.email;

import com.bytedesk.core.base.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;


    private String name;

    private String description;

    // 邮件服务提供商（QQ/GMAIL/NETEASE等）
    private String provider;

    // 用途类型：在线客服接待/工单客服 等
    // private String type;

    private String protocol;

    // 邮箱地址
    private String emailAddress;

    // 邮箱密码或授权码
    private String emailPassword;

    // SMTP服务器地址
    private String smtpHost;

    // SMTP服务器端口
    private Integer smtpPort;

    // 是否启用SSL
    private Boolean smtpSslEnabled;

    // 是否启用TLS
    private Boolean smtpTlsEnabled;

    // IMAP服务器地址
    private String imapHost;

    // IMAP服务器端口
    private Integer imapPort;

    // IMAP是否启用SSL
    private Boolean imapSslEnabled;

    // POP3服务器地址
    private String pop3Host;

    // POP3服务器端口
    private Integer pop3Port;

    // POP3是否启用SSL
    private Boolean pop3SslEnabled;

    // Exchange服务器地址
    private String exchangeHost;

    // Exchange服务器端口
    private Integer exchangePort;

    // Exchange是否启用SSL
    private Boolean exchangeSslEnabled;

    // 发件人显示名称
    private String senderName;

    // 邮件同步间隔（分钟）
    private Integer syncInterval;

    // 是否自动同步邮件
    private Boolean autoSyncEnabled;

    // 是否自动回复
    private Boolean autoReplyEnabled;

    // 自动回复内容
    private String autoReplyContent;

    // 是否启用，状态：启用/禁用
    private Boolean enabled;

    // 是否调试，状态：调试/生产
    private Boolean debug;

    // 关联的工作组ID
    private String workgroupUid;
}
