/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:16:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 16:57:33
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
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.kbase.kbase.KbaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 帮助文档抽象基类
 */
@MappedSuperclass
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractArticleEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String title;

    private String summary;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String contentMarkdown;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String contentHtml;

    @Column(name = "article_type")
    private String type = MessageTypeEnum.TEXT.name();

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

    @Column(name = "is_markdown")
    @Builder.Default
    private Boolean markdown = false;

    @Builder.Default
    private Integer readCount = 0;

    @Builder.Default
    private Integer likeCount = 0;

    // 编辑者
    @Builder.Default
    private String editor = BytedeskConsts.EMPTY_STRING;

    // 有效开始日期
    private LocalDateTime startDate;

    // 有效结束日期
    private LocalDateTime endDate;

    // 是否需要审核
    @Column(name = "need_audit")
    @Builder.Default
    private Boolean needAudit = false;

    // 审核状态 - 具体枚举值由子类定义
    @Column(name = "audit_status")
    private String auditStatus;

    // 审核意见
    @Column(name = "audit_opinion")
    @Builder.Default
    private String auditOpinion = BytedeskConsts.EMPTY_STRING;

    // 审核人
    @Column(name = "audit_user")
    @Builder.Default
    private String auditUser = BytedeskConsts.EMPTY_STRING;

    // 是否需要密码访问
    @Column(name = "is_password_protected")
    @Builder.Default
    private Boolean isPasswordProtected = false;

    private String password;

    private String categoryUid; // 文章分类

     // 替换kbUid为KbaseEntity
    @ManyToOne(fetch = FetchType.LAZY)
    private KbaseEntity kbase;

    @Column(name = "create_user", length = 1024)
    @Builder.Default
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    // elastic 索引状态 (ArticleStatusEnum: PENDING, PROCESSING, SUCCESS, ERROR)
    private String elasticStatus;

    // 向量索引状态 (ArticleStatusEnum: PENDING, PROCESSING, SUCCESS, ERROR)
    private String vectorStatus;

    // 存储在向量库中的文档ID列表
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    @Builder.Default
    private List<String> docIdList = new ArrayList<>();

    // 多个附件，暂时不启用，直接将链接放在contentMarkdown/contentHtml中即可
    // @OneToMany
    // @JoinColumn(name = "article_uid", referencedColumnName = "uuid")
    // private List<UploadEntity> attachments = new ArrayList<>();
}