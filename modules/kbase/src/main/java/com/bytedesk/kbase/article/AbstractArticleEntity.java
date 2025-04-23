/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:16:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 15:49:21
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
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

    @Column(nullable = false)
    private String type;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    @Builder.Default
    private List<String> tagList = new ArrayList<>();

    @Column(name = "is_top")
    @Builder.Default
    private boolean top = false;

    @Column(name = "is_published")
    @Builder.Default
    private boolean published = false;

    @Column(name = "is_markdown")
    @Builder.Default
    private boolean markdown = false;

    // 是否开启自动生成enable_llm_qa问答
    @Builder.Default
    @Column(name = "is_auto_generate_llm_qa")
    private boolean autoGenerateLlmQa = false;

    // 是否已经生成llm问答
    @Builder.Default
    @Column(name = "is_llm_qa_generated")
    private boolean llmQaGenerated = false;

    // 是否开启自动llm split切块
    @Builder.Default
    @Column(name = "is_auto_llm_split")
    private boolean autoLlmSplit = false;

    // 是否已经自动llm split切块
    @Builder.Default
    @Column(name = "is_llm_splitted")
    private boolean llmSplitted = false;

    @Builder.Default
    private int readCount = 0;

    @Builder.Default
    private int likeCount = 0;

    // 状态 - 具体枚举值由子类定义
    private String status;

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
    private boolean needAudit = false;

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
    private boolean isPasswordProtected = false;

    private String password;

    private String categoryUid; // 文章分类

    private String kbUid; // 对应知识库

    @Column(name = "create_user", length = 1024)
    @Builder.Default
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    // 多个附件，暂时不启用，直接将链接放在contentMarkdown/contentHtml中即可
    // @OneToMany
    // @JoinColumn(name = "article_uid", referencedColumnName = "uuid")
    // private List<UploadEntity> attachments = new ArrayList<>();
}