package com.bytedesk.ticket.comment;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_ticket_comment")
@EqualsAndHashCode(callSuper = true)
public class TicketCommentEntity extends BaseEntity {

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;
    
    @Column(nullable = false, length = 4000)
    private String content;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "internal")
    private Boolean internal = false;  // 是否内部评论
    
    @Column(name = "parent_id")
    private Long parentId;  // 回复的评论ID
} 