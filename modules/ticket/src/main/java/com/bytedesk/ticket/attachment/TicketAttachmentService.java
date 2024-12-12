package com.bytedesk.ticket.attachment;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * 工单附件服务接口
 */
public interface TicketAttachmentService {
    
    /**
     * 保存工单附件
     * @param ticketId 工单ID
     * @param file 上传的文件
     * @return 附件实体
     */
    TicketAttachmentEntity saveTicketAttachment(Long ticketId, MultipartFile file);
    
    /**
     * 保存评论附件
     * @param ticketId 工单ID
     * @param commentId 评论ID
     * @param file 上传的文件
     * @return 附件实体
     */
    TicketAttachmentEntity saveCommentAttachment(Long ticketId, Long commentId, MultipartFile file);
    
    /**
     * 获取工单的所有附件
     * @param ticketId 工单ID
     * @return 附件列表
     */
    List<TicketAttachmentEntity> getTicketAttachments(Long ticketId);
    
    /**
     * 获取评论的所有附件
     * @param commentId 评论ID
     * @return 附件列表
     */
    List<TicketAttachmentEntity> getCommentAttachments(Long commentId);
    
    /**
     * 删除附件
     * @param attachmentId 附件ID
     */
    void deleteAttachment(Long attachmentId);
    
    /**
     * 删除工单的所有附件
     * @param ticketId 工单ID
     */
    void deleteTicketAttachments(Long ticketId);
    
    /**
     * 删除评论的所有附件
     * @param commentId 评论ID
     */
    void deleteCommentAttachments(Long commentId);
    
    /**
     * 获取附件
     * @param attachmentId 附件ID
     * @return 附件实体
     */
    TicketAttachmentEntity getAttachment(Long attachmentId);
    
    /**
     * 获取附件内容
     * @param attachmentId 附件ID
     * @return 附件字节数组
     */
    byte[] getAttachmentContent(Long attachmentId);
    
    /**
     * 生成附件缩略图
     * @param attachmentId 附件ID
     * @return 缩略图URL
     */
    String generateThumbnail(Long attachmentId);
    
    /**
     * 验证附件
     * @param file 上传的文件
     * @throws AttachmentException 验证失败时抛出
     */
    void validateAttachment(MultipartFile file);
} 