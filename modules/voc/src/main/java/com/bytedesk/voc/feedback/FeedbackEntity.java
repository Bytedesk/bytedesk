package com.bytedesk.voc.feedback;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_voc_feedback")
@EqualsAndHashCode(callSuper = true)
public class FeedbackEntity extends BaseEntity {

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String type = "suggestion"; // suggestion, bug, complaint, other

    private String status = "pending"; // pending, processing, resolved, closed

    @Column(name = "reply_count")
    private Integer replyCount = 0;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "assigned_to")
    private Long assignedTo; // 分配给哪个管理员处理
} 