/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:16:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-21 10:43:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.notebase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.ai.document.Document;
import org.springframework.lang.NonNull;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.kbase.knowledge_base.KnowledgebaseTypeEnum;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 内部知识库，个人笔记
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ NotebaseEntityListener.class })
@Table(name = "bytedesk_kbase_notebase")
public class NotebaseEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String title;

    private String summary;
    // private String coverImageUrl;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String contentMarkdown;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String contentHtml;

    @Builder.Default
    @Column(name = "Notebase_type", nullable = false)
    private String type = KnowledgebaseTypeEnum.HELPCENTER.name();

    // @Builder.Default
    // @ManyToMany
    // private List<Tag> tags = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "bytedesk_kbase_Notebase_tags")
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    @Column(name = "is_top")
    private boolean top = false;

    @Builder.Default
    @Column(name = "is_published")
    private boolean published = false;

    @Builder.Default
    @Column(name = "is_markdown")
    private boolean markdown = false;

    @Builder.Default
    private int readCount = 0;

    private String categoryUid; // 文章分类。生成页面时，先查询分类，后通过分类查询相关文章。

    private String kbUid; // 对应知识库

    @Builder.Default
    @Column(name = "create_user", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    //
    public Document toDocument(@NonNull NotebaseEntity Notebase) {
        return new Document(Notebase.getTitle() + Notebase.getContentMarkdown(),
                Map.of("categoryUid", Notebase.getCategoryUid(), "kbUid", Notebase.getKbUid()));
    }

}
