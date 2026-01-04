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
package com.bytedesk.kbase.blog;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.message.MessageTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Blog entity for content categorization and organization
 * Provides blogging functionality for various system entities
 * 
 * Database Table: bytedesk_kbase_blog
 * Purpose: Stores blog definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ BlogEntityListener.class })
@Table(name = "bytedesk_kbase_blog")
public class BlogEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    private String name;

    /**
     * 摘要/描述
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * 内容类型（TEXT/MARKDOWN/HTML...），默认 TEXT
     */
    @Builder.Default
    @Column(name = "blog_type")
    private String type = MessageTypeEnum.TEXT.name();

    // 封面图
    private String coverImageUrl;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String contentMarkdown;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String contentHtml;

    // 文章分类（CategoryEntity.uid）
    private String categoryUid;

    // 归属知识库（KbaseEntity.uid）
    private String kbUid;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    @Builder.Default
    private List<String> tagList = new ArrayList<>();

    @Column(name = "is_top")
    @Builder.Default
    private Boolean top = false;

    @Column(name = "is_published")
    @Builder.Default
    private Boolean published = false;

    @Builder.Default
    private Integer readCount = 0;

    @Builder.Default
    private Integer likeCount = 0;

    // 编辑者
    @Builder.Default
    private String editor = BytedeskConsts.EMPTY_STRING;

    @PrePersist
    public void prePersist() {
        if (type == null) {
            type = MessageTypeEnum.TEXT.name();
        }
        if (published == null) {
            published = false;
        }
        if (top == null) {
            top = false;
        }
        if (description == null) {
            description = I18Consts.I18N_DESCRIPTION;
        }
        if (editor == null) {
            editor = BytedeskConsts.EMPTY_STRING;
        }
    }

    @PostLoad
    public void postLoad() {
        if (type == null) {
            type = MessageTypeEnum.TEXT.name();
        }
    }

}
