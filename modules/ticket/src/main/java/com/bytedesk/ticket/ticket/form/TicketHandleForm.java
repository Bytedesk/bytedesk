package com.bytedesk.ticket.ticket.form;

import lombok.Data;

@Data
public class TicketHandleForm {
    private String comment;      // 处理意见
    private String result;       // 处理结果
    private String priority;     // 优先级
    private String category;     // 分类
    private String assignee;     // 处理人
} 