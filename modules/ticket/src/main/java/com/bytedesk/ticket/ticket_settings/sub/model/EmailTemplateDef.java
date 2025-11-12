package com.bytedesk.ticket.ticket_settings.sub.model;

import lombok.*;

import java.io.Serializable;

/**
 * 邮件模板定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplateDef implements Serializable {
    private String event;        // 事件标识，如 ticket_created
    private String subject;      // 邮件主题
    private String body;         // 邮件正文(可包含变量占位)
    @Builder.Default
    private boolean enabled = true; // 是否启用
}
