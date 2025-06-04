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

@Data
@Entity
@Table(name = "bytedesk_voc_feedback")
@EqualsAndHashCode(callSuper = true)
public class FeedbackEntity extends BaseEntity {

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    @Column(name = "user_id")
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