/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-17 15:40:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-10 18:45:26
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
 * 图片消息内容类
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ImageContent extends BaseContent {
    
    private static final long serialVersionUID = 1L;

    private String url;           // 图片URL
    private String width;         // 图片宽度
    private String height;        // 图片高度
    private String label;         // 图片标签/说明
    private String mimeType;      // MIME类型 (如: image/jpeg, image/png)
    private String size;          // 文件大小 (字节)
    private String hash;          // 文件哈希值 (SHA256)
    private String thumbnail;     // 缩略图URL
    private String filename;      // 文件名

    /**
     * 从JSON字符串反序列化为ImageContent对象
     * @param json JSON字符串
     * @return ImageContent对象，如果解析失败返回null
     */
    public static ImageContent fromJson(String json) {
        return BaseContent.fromJson(json, ImageContent.class);
    }
}
