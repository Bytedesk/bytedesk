/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:35:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.task_comment;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.converter.JsonStringListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * TaskComment entity for content categorization and organization
 * Provides task_commentging functionality for various system entities
 * 
 * Database Table: bytedesk_core_task_comment
 * Purpose: Stores task_comment definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({TaskCommentEntityListener.class})
@Table(
    name = "bytedesk_core_task_comment",
    indexes = {
        @Index(name = "idx_task_comment_uid", columnList = "uuid"),
        @Index(name = "idx_task_comment_org_uid", columnList = "org_uid"),
        @Index(name = "idx_task_comment_user_uid", columnList = "user_uid"),
        @Index(name = "idx_task_comment_task_uid", columnList = "task_uid")
    }
)
public class TaskCommentEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Task uid that this comment belongs to
     */
    @Column(name = "task_uid", length = 64)
    private String taskUid;

    /**
    * Comment content
    */
    @Builder.Default
    @Column(name = "content", length = 2048)
    private String content = "";

    /**
    * Optional images for this comment
    */
    @Builder.Default
    @Convert(converter = JsonStringListConverter.class)
    @Column(name = "images", columnDefinition = "TEXT")
    private List<String> images = new ArrayList<>();


}
