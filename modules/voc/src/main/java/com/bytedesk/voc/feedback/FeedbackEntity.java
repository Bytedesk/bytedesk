/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 12:08:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 08:43:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.feedback;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Voice of Customer feedback entity for user feedback management
 * Manages user feedback, suggestions, and complaint tracking
 * 
 * Database Table: bytedesk_voc_feedback
 * Purpose: Stores user feedback content, status tracking, and assignment management
 */
@Data
@Entity
@Table(name = "bytedesk_voc_feedback")
@EqualsAndHashCode(callSuper = true)
public class FeedbackEntity extends BaseEntity {

    /**
     * Main content of the feedback
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    /**
     * ID of the user who submitted the feedback
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Type of feedback (suggestion, bug, complaint, other)
     */
    private String type = "suggestion"; // suggestion, bug, complaint, other

    /**
     * Current status of the feedback (pending, processing, resolved, closed)
     */
    private String status = "pending"; // pending, processing, resolved, closed

    /**
     * Number of replies to this feedback
     */
    @Column(name = "reply_count")
    private Integer replyCount = 0;

    /**
     * Number of likes received on this feedback
     */
    @Column(name = "like_count")
    private Integer likeCount = 0;

    /**
     * ID of the administrator assigned to handle this feedback
     */
    @Column(name = "assigned_to")
    private Long assignedTo; // 分配给哪个管理员处理
} 