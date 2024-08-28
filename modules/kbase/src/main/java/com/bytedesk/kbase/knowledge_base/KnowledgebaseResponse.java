/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-27 16:26:10
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

import java.util.Date;
import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgebaseResponse extends BaseResponse {

    private String name;

    private String type;

    // headline标头
    private String headline;

    // 自定义网址
    private String url;

    private String logoUrl;

    private String faviconUrl;

    // 主题色
    private String primaryColor;

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

    private String embedding;

    private String language;

    private String level;

    private String platform;

    private List<String> tags;

    private Boolean published;

    private String orgUid;

    private Date updatedAt;

    private String agentUid;
}
