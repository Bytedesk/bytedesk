/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-27 15:56:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.knowledge_base;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PlatformEnum;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgebaseRequest extends BaseRequest {

    private String name;

    // headline标头
    @Builder.Default
    private String headline = KnowledgebaseConsts.HEADLINE;

    // 自定义副标题
    @Builder.Default
    private String subheadline = KnowledgebaseConsts.SUB_HEADLINE;

    // 自定义网址
    @Builder.Default
    private String url = KnowledgebaseConsts.URL;

    @Builder.Default
    private String logoUrl = KnowledgebaseConsts.LOGO_URL;

    @Builder.Default
    private String faviconUrl = KnowledgebaseConsts.FAVICON_URL;

    // 主题色
    @Builder.Default
    private String primaryColor = BdConstants.EMPTY_STRING;

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
    private String headerHtml = BdConstants.EMPTY_STRING;

    // 自定义页脚, 添加显示于页面底部的 HTML 代码。
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String footerHtml = BdConstants.EMPTY_STRING;

    // 自定义 CSS, 添加 Less/CSS 代码以自定义论坛外观，此设置将覆盖默认样式
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String css = BdConstants.EMPTY_STRING;

    @Builder.Default
    private String embedding = KnowledgebaseConsts.KB_EMBEDING;

    @Builder.Default
    // @Enumerated(EnumType.STRING)
    // private LanguageEnum language = LanguageEnum.ZH_CN;
    private String language = LanguageEnum.ZH_CN.name();

    @Builder.Default
    // @Enumerated(EnumType.STRING)
    // private LevelEnum level = LevelEnum.ORGNIZATION;
    private String level = LevelEnum.ORGNIZATION.name();

    @Builder.Default
    // @Enumerated(EnumType.STRING)
    // private PlatformEnum platform = PlatformEnum.BYTEDESK;
    private String platform = PlatformEnum.BYTEDESK.name();

    // private KnowledgebaseTypeEnum type = KnowledgebaseTypeEnum.HELPDOC;
    // private String categoryUid;

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private Boolean published = true;

    private String agentUid;

}
