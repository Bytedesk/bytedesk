/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 11:42:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 08:43:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.forum.post;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_forum_post",
      indexes = {
          @Index(name = "idx_post_category", columnList = "category_id", unique = false),
          @Index(name = "idx_post_user", columnList = "user_id", unique = false)
      }
)
@EqualsAndHashCode(callSuper = true)
public class PostEntity extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "comment_count")
    private Integer commentCount = 0;

    private String status = "published"; // draft, published, deleted

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;
} 