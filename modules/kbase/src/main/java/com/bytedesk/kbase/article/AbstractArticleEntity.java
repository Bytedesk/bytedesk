/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:16:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-22 09:21:27
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

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
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

    @Column(nullable = false)
    private String type;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    @Column(name = "is_top")
    private boolean top = false;

    @Column(name = "is_published")
    private boolean published = false;

    @Column(name = "is_markdown")
    private boolean markdown = false;

    private int readCount = 0;

    private int likeCount = 0;

    // 状态 - 具体枚举值由子类定义
    private String status;

    // 编辑者
    private String editor = BytedeskConsts.EMPTY_STRING;

    // 有效开始日期
    private LocalDateTime startDate;

    // 有效结束日期
    private LocalDateTime endDate;

    // 是否需要审核
    @Column(name = "need_audit")
    private boolean needAudit = false;

    // 审核状态 - 具体枚举值由子类定义
    @Column(name = "audit_status")
    private String auditStatus;

    // 审核意见
    @Column(name = "audit_opinion")
    private String auditOpinion = BytedeskConsts.EMPTY_STRING;

    // 审核人
    @Column(name = "audit_user")
    private String auditUser = BytedeskConsts.EMPTY_STRING;

    // 是否需要密码访问
    @Column(name = "is_password_protected")
    private boolean isPasswordProtected = false;

    private String password;

    private String categoryUid; // 文章分类

    private String kbUid; // 对应知识库

    @Column(name = "create_user", length = 1024)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    // 多个附件
    @OneToMany
    @JoinColumn(name = "article_uid", referencedColumnName = "uuid")
    private List<UploadEntity> attachments = new ArrayList<>();
} 