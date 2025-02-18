package com.bytedesk.ticket.message.event;


public enum TicketMessageType {
    ASSIGNED,    // 工单已分配
    UNCLAIMED,   // 工单已退回
    COMPLETED,   // 工单已完成
    ESCALATED    // 工单已升级
} 