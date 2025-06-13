/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:16:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-19 14:13:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.LlmConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.member.MemberEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * kbase + space
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ KbaseEntityListener.class })
@Table(name = "bytedesk_kbase")
public class KbaseEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    @Builder.Default
    @Column(name = "kb_type")
    private String type = KbaseTypeEnum.HELPCENTER.name();

    // headline标头
    @Builder.Default
    private String headline = KbaseConsts.HEADLINE;

    // 自定义副标题
    @Builder.Default
    @Column(name = "sub_headline")
    private String subHeadline = KbaseConsts.SUB_HEADLINE;

    // 自定义网址
    @Builder.Default
    private String url = KbaseConsts.KBASE_URL;

    @Builder.Default
    private String logoUrl = KbaseConsts.KBASE_LOGO_URL;

    @Builder.Default
    private String faviconUrl = KbaseConsts.KBASE_FAVICON_URL;

    // 自定义封面图片
    private String coverImageUrl;

    // 自定义背景图片
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
    @Column(name = "is_favorite")
    private Boolean favorite = false;

    // 是否公开: 内部知识库可设置为公开，外部知识库可设置为不公开
    @Builder.Default
    @Column(name = "is_public")
    private Boolean isPublic = false;

    /**
     * 知识库描述
     * 输入一两句话简单介绍您的论坛。这将会显示为 <meta name="description"> 描述标签，一般为 160
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

    @Builder.Default
    private String language = LanguageEnum.ZH_CN.name();

    // 有效开始日期
    private LocalDateTime startDate;

    // 有效结束日期
    private LocalDateTime endDate;

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    // 大模型知识库-嵌入向量提供者
    @Builder.Default
    @Column(name = "llm_embedding_provider")
    private String embeddingProvider = LlmConsts.DEFAULT_EMBEDDING_PROVIDER;
    
    @Builder.Default
    @Column(name = "llm_embedding_model")
    private String embeddingModel = LlmConsts.DEFAULT_EMBEDDING_MODEL; //"embedding-v2";

    @Builder.Default
    private Boolean showChat = false;

    @Builder.Default
    private Boolean published = true;

    // 某人工客服快捷回复知识库
    private String agentUid;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    private List<MemberEntity> members = new ArrayList<>();

    public String getTheme() {
        return this.theme.toLowerCase();
    }
}
