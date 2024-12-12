/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 12:08:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 12:17:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.reply;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_voc_reply")
@EqualsAndHashCode(callSuper = true)
public class ReplyEntity extends BaseEntity {

    @Column(name = "feedback_id", nullable = false)
    private Long feedbackId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "parent_id")
    private Long parentId;  // 回复其他评论

    @Column(name = "like_count")
    private Integer likeCount = 0;

    private Boolean internal = false;  // 是否内部回复
} 