package com.bytedesk.ticket.attachment;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_ticket_attachment")
@EqualsAndHashCode(callSuper = true)
public class TicketAttachmentEntity extends BaseEntity {

    @Column(name = "ticket_id")
    private Long ticketId;
    
    @Column(name = "comment_id")
    private Long commentId;
    
    @Column(nullable = false)
    private String filename;
    
    @Column(name = "content_type")
    private String contentType;
    
    private Long size;
    
    @Column(name = "storage_path")
    private String storagePath;
    
    @Column(name = "download_url")
    private String downloadUrl;
    
    private String thumbnail;  // 缩略图URL
    
    @Column(name = "upload_by")
    private Long uploadBy;
} 