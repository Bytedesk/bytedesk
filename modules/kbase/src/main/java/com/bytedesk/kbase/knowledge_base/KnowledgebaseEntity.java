/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:16:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-18 17:24:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.knowledge_base;

import java.util.List;
import java.util.ArrayList;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PlatformEnum;

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
 * 将文档知识库跟AI知识库合并一个库，方便统一知识
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ KnowledgebaseEntityListener.class })
@Table(name = "bytedesk_kbase")
public class KnowledgebaseEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String name;

    @Builder.Default
    @Column(name = "kb_type", nullable = false)
    private String type = KnowledgebaseTypeEnum.HELPCENTER.name();

    // headline标头
    @Builder.Default
    private String headline = KnowledgebaseConsts.HEADLINE;

    // 自定义副标题
    @Builder.Default
    @Column(name = "sub_headline")
    private String subHeadline = KnowledgebaseConsts.SUB_HEADLINE;

    // 自定义网址
    @Builder.Default
    private String url = KnowledgebaseConsts.URL;

    @Builder.Default
    private String logoUrl = KnowledgebaseConsts.LOGO_URL;

    @Builder.Default
    private String faviconUrl = KnowledgebaseConsts.FAVICON_URL;

    // 主题色
    @Builder.Default
    private String primaryColor = BytedeskConsts.EMPTY_STRING;

    @Builder.Default
    private String theme = KnowledgebaseThemeEnum.DEFAULT.name();

    /**
     * 知识库描述
     * 输入一两句话简单介绍您的论坛。这将会显示为 <meta name="description"> 描述标签，一般为 160
     * 字的文本，用于介绍网页的内容。平常多被搜索引擎截取网页简介用。
     */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String descriptionHtml = KnowledgebaseConsts.KB_DESCRIPTION;

    // 自定义页眉, 添加显示于页面顶部、位于默认页眉上方的 HTML 代码。
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String headerHtml = BytedeskConsts.EMPTY_STRING;

    // 自定义页脚, 添加显示于页面底部的 HTML 代码。
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String footerHtml = BytedeskConsts.EMPTY_STRING;

    // 自定义 CSS, 添加 Less/CSS 代码以自定义论坛外观，此设置将覆盖默认样式
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String css = BytedeskConsts.EMPTY_STRING;

    @Builder.Default
    private String embedding = KnowledgebaseConsts.KB_EMBEDDING;

    @Builder.Default
    private String language = LanguageEnum.ZH_CN.name();

    @Builder.Default
    private String level = LevelEnum.ORGANIZATION.name();

    @Builder.Default
    private String platform = PlatformEnum.BYTEDESK.name();

    // @Builder.Default
    // @ManyToMany
    // private List<Tag> tags = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "bytedesk_kbase_base_tags")
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private boolean showChat = false;

    @Builder.Default
    private boolean published = true;

    // 某人工客服快捷回复知识库
    private String agentUid;

    public String getTheme() {
        return this.theme.toLowerCase();
    }
}
