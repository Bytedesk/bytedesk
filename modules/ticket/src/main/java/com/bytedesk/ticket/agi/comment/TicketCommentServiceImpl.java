/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 12:39:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 16:06:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.ticket.ticket.TicketService;
import com.bytedesk.ticket.agi.attachment.TicketAttachmentService;
import com.bytedesk.ticket.agi.notification.TicketNotificationService;
import com.bytedesk.ticket.comment.exception.CommentNotFoundException;

import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Service
public class TicketCommentServiceImpl implements TicketCommentService {

    @Autowired
    private TicketCommentRepository commentRepository;
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private TicketAttachmentService attachmentService;
    
    @Autowired
    private TicketNotificationService ticketNotificationService;

    @Override
    public List<TicketCommentEntity> getTicketComments(Long ticketId) {
        // 验证工单是否存在
        ticketService.getTicket(ticketId);
        return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
    }

    @Override
    public TicketCommentEntity getComment(Long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    @Override
    @Transactional
    public TicketCommentEntity addComment(Long ticketId, String content, Boolean internal, 
            List<MultipartFile> attachments, Long userId) {
        try {
            // 验证工单是否存在
            // TicketEntity ticket = ticketService.getTicket(ticketId);
            
            // 创建评论
            TicketCommentEntity comment = new TicketCommentEntity();
            comment.setTicketId(ticketId);
            comment.setContent(content);
            comment.setUserId(userId);
            comment.setInternal(internal != null && internal);
            
            comment = commentRepository.save(comment);
            
            // 保存附件
            if (attachments != null && !attachments.isEmpty()) {
                attachments.forEach(file -> {
                    try {
                        // attachmentService.saveCommentAttachment(comment.getId(), file);
                    } catch (Exception e) {
                        log.error("Failed to save comment attachment", e);
                    }
                });
            }
            
            // 发送通知
            try {
                if (comment.getInternal()) {
                    ticketNotificationService.sendInternalCommentNotification(ticketId, comment.getId());
                } else {
                    ticketNotificationService.sendNewCommentNotification(ticketId, comment.getId());
                }
            } catch (Exception e) {
                log.error("Failed to send comment notification", e);
            }
            
            return comment;
            
        } catch (Exception e) {
            log.error("Failed to add comment", e);
            throw new RuntimeException("Failed to add comment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        try {
            TicketCommentEntity comment = getComment(commentId);
            
            // 删除附件
            try {
                attachmentService.deleteCommentAttachments(commentId);
            } catch (Exception e) {
                log.error("Failed to delete comment attachments", e);
            }
            
            // 删除评论
            commentRepository.delete(comment);
            
        } catch (Exception e) {
            log.error("Failed to delete comment", e);
            throw new RuntimeException("Failed to delete comment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public TicketCommentEntity replyComment(Long commentId, String content, Boolean internal, 
            List<MultipartFile> attachments, Long userId) {
        try {
            TicketCommentEntity parentComment = getComment(commentId);
            
            // 创建回复
            TicketCommentEntity reply = new TicketCommentEntity();
            reply.setTicketId(parentComment.getTicketId());
            reply.setContent(content);
            reply.setUserId(userId);
            reply.setInternal(internal != null && internal);
            reply.setParentId(commentId);
            
            reply = commentRepository.save(reply);
            
            // 保存附件
            if (attachments != null && !attachments.isEmpty()) {
                attachments.forEach(file -> {
                    try {
                        // attachmentService.saveCommentAttachment(reply.getId(), file);
                    } catch (Exception e) {
                        log.error("Failed to save reply attachment", e);
                    }
                });
            }
            
            // 发送通知
            try {
                ticketNotificationService.sendCommentReplyNotification(
                    parentComment.getUserId(), parentComment.getTicketId(), reply.getId());
            } catch (Exception e) {
                log.error("Failed to send reply notification", e);
            }
            
            return reply;
            
        } catch (Exception e) {
            log.error("Failed to reply comment", e);
            throw new RuntimeException("Failed to reply comment: " + e.getMessage());
        }
    }

    @Override
    public List<TicketCommentEntity> findPublicComments(Long ticketId) {
        // 验证工单是否存在
        ticketService.getTicket(ticketId);
        return commentRepository.findPublicComments(ticketId);
    }

    @Override
    public List<TicketCommentEntity> findInternalComments(Long ticketId) {
        // 验证工单是否存在
        ticketService.getTicket(ticketId);
        return commentRepository.findInternalComments(ticketId);
    }
}