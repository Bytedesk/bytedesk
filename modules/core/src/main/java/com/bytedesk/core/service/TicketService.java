package com.bytedesk.core.service;

import java.util.Date;

// 假设这是已有的文件，我们在此添加邮件相关方法

public interface TicketService {

    // ...existing code...
    
    /**
     * 从接收到的邮件创建工单
     * 
     * @param subject 邮件主题
     * @param fromEmail 发件人邮箱
     * @param content 邮件内容
     * @param date 接收日期
     * @return 创建的工单ID
     */
    String createFromEmail(String subject, String fromEmail, String content, Date date);
}
