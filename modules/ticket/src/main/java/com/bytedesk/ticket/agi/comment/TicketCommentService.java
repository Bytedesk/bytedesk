package com.bytedesk.ticket.agi.comment;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * 工单评论服务接口
 * 提供评论的增删改查、回复、附件管理等功能
 */
public interface TicketCommentService {
    
    /**
     * 获取工单评论列表
     * @param ticketId 工单ID
     * @return 评论列表，按创建时间升序排序
     */
    List<TicketCommentEntity> getTicketComments(Long ticketId);
    
    /**
     * 获取评论详情
     * @param commentId 评论ID
     * @return 评论实体
     * @throws RuntimeException 评论不存在时抛出
     */
    TicketCommentEntity getComment(Long commentId);
    
    /**
     * 添加评论
     * @param ticketId 工单ID
     * @param content 评论内容
     * @param internal 是否内部评论
     * @param attachments 附件列表
     * @param userId 评论用户ID
     * @return 创建的评论实体
     */
    TicketCommentEntity addComment(Long ticketId, String content, Boolean internal, 
            List<MultipartFile> attachments, Long userId);
    
    /**
     * 删除评论
     * @param commentId 评论ID
     * @throws RuntimeException 评论不存在时抛出
     */
    void deleteComment(Long commentId);
    
    /**
     * 回复评论
     * @param commentId 被回复的评论ID
     * @param content 回复内容
     * @param internal 是否内部评论
     * @param attachments 附件列表
     * @param userId 回复用户ID
     * @return 创建的回复评论实体
     * @throws RuntimeException 评论不存在时抛出
     */
    TicketCommentEntity replyComment(Long commentId, String content, Boolean internal, 
            List<MultipartFile> attachments, Long userId);
    
    /**
     * 获取公开评论列表
     * @param ticketId 工单ID
     * @return 公开评论列表，按创建时间升序排序
     */
    List<TicketCommentEntity> findPublicComments(Long ticketId);
    
    /**
     * 获取内部评论列表
     * @param ticketId 工单ID
     * @return 内部评论列表，按创建时间升序排序
     */
    List<TicketCommentEntity> findInternalComments(Long ticketId);
} 