package com.bytedesk.core.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bytedesk.core.entity.Ticket;
import com.bytedesk.core.repository.TicketRepository;
import com.bytedesk.core.service.TicketService;
import com.bytedesk.core.util.UUIDUtil;

// 假设这是已有的类，我们在此添加邮件相关方法实现

@Service
public class TicketServiceImpl implements TicketService {

    private final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);
    
    @Autowired
    private TicketRepository ticketRepository;
    
    // ...existing code...
    
    @Override
    public String createFromEmail(String subject, String fromEmail, String content, Date date) {
        logger.info("从邮件创建工单: {}", subject);
        
        try {
            Ticket ticket = new Ticket();
            ticket.setUid(UUIDUtil.getUUID());
            ticket.setSubject(subject);
            ticket.setContent(content);
            ticket.setEmail(fromEmail);
            ticket.setSource("email");
            ticket.setStatus("open");
            ticket.setCreatedAt(date);
            
            ticketRepository.save(ticket);
            
            logger.info("成功创建邮件工单: {}", ticket.getUid());
            return ticket.getUid();
        } catch (Exception e) {
            logger.error("从邮件创建工单失败", e);
            return null;
        }
    }
}