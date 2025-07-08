/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 12:08:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 08:43:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.reply;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Voice of Customer reply entity for feedback response management
 * Manages replies to user feedback and internal communication
 * 
 * Database Table: bytedesk_voc_reply
 * Purpose: Stores feedback replies, threaded conversations, and internal notes
 */
@Data
@Entity
@Table(name = "bytedesk_voc_reply")
@EqualsAndHashCode(callSuper = true)
public class ReplyEntity extends BaseEntity {

    /**
     * ID of the feedback this reply belongs to
     */
    @Column(name = "feedback_id")
    private Long feedbackId;

    /**
     * ID of the user who wrote this reply
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Content of the reply
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    /**
     * ID of the parent reply for threaded conversations
     */
    @Column(name = "parent_id")
    private Long parentId;  // 回复其他评论

    /**
     * Number of likes received on this reply
     */
    @Column(name = "like_count")
    private Integer likeCount = 0;

    /**
     * Whether this is an internal reply (not visible to customers)
     */
    private Boolean internal = false;  // 是否内部回复
} 