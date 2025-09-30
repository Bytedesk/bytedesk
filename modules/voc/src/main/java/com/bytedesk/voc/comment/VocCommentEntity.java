/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 14:13:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.comment;

import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.Entity;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * VocComment entity for content categorization and organization
 * Provides comment functionality for various system entities
 * 
 * Database Table: bytedesk_voc_comment
 * Purpose: Stores comment definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({VocCommentEntityListener.class})
@Table(name = "bytedesk_voc_comment")
public class VocCommentEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;


    private String title;
    
    // @Builder.Default
    // @Column(name = "comment_type")
    // private String type = VocCommentTypeEnum.CUSTOMER.name();

    // 
    private String content;

    private String imageUrl;

    private List<String> categoryUids;

 
}
