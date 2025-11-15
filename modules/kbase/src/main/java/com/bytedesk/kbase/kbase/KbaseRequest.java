/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-16 10:51:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.llm.LlmDefaults;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class KbaseRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    // private String type;

    // private String subType;

    // headline标头
    @Builder.Default
    private String headline = KbaseConsts.HEADLINE;

    // 自定义副标题
    @Builder.Default
    private String subHeadline = KbaseConsts.SUB_HEADLINE;

    // 自定义网址
    @Builder.Default
    private String url = KbaseConsts.KBASE_URL;

    @Builder.Default
    private String logoUrl = KbaseConsts.KBASE_LOGO_URL;

    @Builder.Default
    private String faviconUrl = KbaseConsts.KBASE_FAVICON_URL;

    private String coverImageUrl;

    private String backgroundImageUrl;

    // 主题色
    @Builder.Default
    private String primaryColor = BytedeskConsts.EMPTY_STRING;

    @Builder.Default
    private String theme = KbaseThemeEnum.DEFAULT.name();

    // 成员数量
    @Builder.Default
    private Integer memberCount = 0;

    // 文章数量
    @Builder.Default
    private Integer articleCount = 0;

    // 是否收藏
    @Builder.Default
    private Boolean favorite = false;

    // 是否公开
    @Builder.Default
    private Boolean isPublic = false;

    /**
     * 知识库描述
     * 输入一两句话简单介绍您的论坛。这将会显示为 <meta name="description"&gt; 描述标签，一般为 160
     * 字的文本，用于介绍网页的内容。平常多被搜索引擎截取网页简介用。
     */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String descriptionHtml = KbaseConsts.KB_DESCRIPTION;

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

    // @Builder.Default
    // private String embedding = KbaseConsts.KB_EMBEDDING;

    @Builder.Default
    // @Enumerated(EnumType.STRING)
    // private LanguageEnum language = LanguageEnum.ZH_CN;
    private String language = LanguageEnum.ZH_CN.name();

    // @Builder.Default
    // private String level = LevelEnum.ORGANIZATION.name();

    // @Builder.Default
    // private String platform = PlatformEnum.BYTEDESK.name();

    // private KbaseTypeEnum type = KbaseTypeEnum.HELPCENTER;
    // private String categoryUid;

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();
    
    private ZonedDateTime startDate;

    private ZonedDateTime endDate;

    // 大模型知识库-嵌入向量提供者
    @Builder.Default
    private String embeddingProvider = LlmDefaults.DEFAULT_EMBEDDING_PROVIDER;
    
    @Builder.Default
    private String embeddingModel = LlmDefaults.DEFAULT_EMBEDDING_MODEL;

    @Builder.Default
    private Boolean showChat = false;

    @Builder.Default
    private Boolean published = true;

    private String agentUid;

    // 从 notebase 查询, 同时查询 HELPCENTER 和 NOTEBASE 两种类型
    @Builder.Default
    private Boolean queryNotebase = false;

    @NotEmpty(message = "memberUids must not be empty")
    @Builder.Default
    private List<String> memberUids = new ArrayList<String>();

}
