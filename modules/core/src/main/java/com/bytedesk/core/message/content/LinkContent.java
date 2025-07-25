/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-17 15:43:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-10 18:11:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 链接消息内容类
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LinkContent extends BaseContent {
    private String url;           // 链接URL
    private String title;         // 链接标题
    private String description;   // 链接描述
    private String imageUrl;      // 链接预览图片URL
    private String label;         // 链接标签/说明

    /**
     * 从JSON字符串反序列化为LinkContent对象
     * @param json JSON字符串
     * @return LinkContent对象，如果解析失败返回null
     */
    public static LinkContent fromJson(String json) {
        return BaseContent.fromJson(json, LinkContent.class);
    }
} 