/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:16:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-21 23:27:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.kbase.kbase.KbaseTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 帮助文档
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ ArticleEntityListener.class })
@Table(name = "bytedesk_kbase_article")
public class ArticleEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String title;

    private String summary;
    // private String coverImageUrl;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String contentMarkdown;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String contentHtml;

    @Builder.Default
    @Column(name = "article_type", nullable = false)
    private String type = KbaseTypeEnum.HELPCENTER.name();

    // @Builder.Default
    // @ManyToMany
    // private List<Tag> tags = new ArrayList<>();

    // @Builder.Default
    // @ElementCollection
    // @CollectionTable(name = "bytedesk_kbase_article_tags")
    // private List<String> tags = new ArrayList<>();
    
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

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

    @Builder.Default
    private int likeCount = 0;

    // status 状态
    @Builder.Default
    private String status = ArticleStatusEnum.DRAFT.name();

    // editor 编辑者
    @Builder.Default
    private String editor = BytedeskConsts.EMPTY_STRING;

    // 有效开始日期
    private LocalDateTime startDate;

    // 有效结束日期
    private LocalDateTime endDate;

    // 是否需要审核
    @Builder.Default
    @Column(name = "need_audit")
    private boolean needAudit = false;

    // 审核状态
    @Builder.Default
    @Column(name = "audit_status")
    private String auditStatus = ArticleAuditStatusEnum.PENDING.name();

    // 审核意见
    @Builder.Default
    @Column(name = "audit_opinion")
    private String auditOpinion = BytedeskConsts.EMPTY_STRING;

    // 审核人
    @Builder.Default
    @Column(name = "audit_user")
    private String auditUser = BytedeskConsts.EMPTY_STRING;

    // 是否需要密码访问
    @Builder.Default
    @Column(name = "is_password_protected")
    private boolean isPasswordProtected = false;

    private String password;

    private String categoryUid; // 文章分类。生成页面时，先查询分类，后通过分类查询相关文章。

    private String kbUid; // 对应知识库

    @Builder.Default
    @Column(name = "create_user", length = 1024)
    // @JdbcTypeCode(SqlTypes.JSON)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    // TODO:多个附件
    // @Builder.Default
    // @OneToMany(mappedBy = "article")
    // private List<UploadEntity> attachments = new ArrayList<>();

    // public Document toDocument(@NonNull ArticleEntity article) {
    //     return new Document(article.getTitle() + article.getContentMarkdown(),
    //             Map.of("categoryUid", article.getCategoryUid(), "kbUid", article.getKbUid()));
    // }

}
