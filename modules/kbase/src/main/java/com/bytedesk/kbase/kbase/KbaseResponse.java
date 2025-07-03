/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-19 14:15:04
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
import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class KbaseResponse extends BaseResponse {

    private String name;

    private String description;

    private String type;

    // headline标头
    private String headline;

    private String subHeadline;

    // 自定义网址, 
    private String url;

    private String logoUrl;

    private String faviconUrl;

    private String coverImageUrl;

    private String backgroundImageUrl;

    // 主题色
    private String primaryColor;

    private String theme;

    // 成员数量
    private Integer memberCount;

    // 文章数量
    private Integer articleCount;

    // 是否收藏
    private Boolean favorite;

    // 是否公开
    private Boolean isPublic;

    /**
     * 知识库描述
     * 输入一两句话简单介绍您的论坛。这将会显示为 <meta name="description"> 描述标签，一般为 160
     * 字的文本，用于介绍网页的内容。平常多被搜索引擎截取网页简介用。
     */
    private String descriptionHtml;

    // 自定义页眉, 添加显示于页面顶部、位于默认页眉上方的 HTML 代码。
    private String headerHtml;

    // 自定义页脚, 添加显示于页面底部的 HTML 代码。
    private String footerHtml;

    // 自定义 CSS, 添加 Less/CSS 代码以自定义论坛外观，此设置将覆盖默认样式
    private String css;

    private String language;

    private List<String> tagList;

    private Boolean showChat;

    private Boolean published;

    // 有效开始日期
    private ZonedDateTime startDate;

    // 有效结束日期
    private ZonedDateTime endDate;

    // 大模型知识库-嵌入向量提供者
    private String embeddingProvider;
    
    private String embeddingModel;

    private String agentUid;

    private String userUid;
    
    private List<UserProtobuf> members;
}
