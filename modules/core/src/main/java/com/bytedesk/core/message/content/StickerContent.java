/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-17 15:40:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-10 17:35:32
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
 * 贴纸消息内容类
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class StickerContent extends BaseContent {

    private String url;           // 贴纸URL
    private String label;         // 贴纸标签/说明
    private String mimeType;      // MIME类型 (如: image/webp, image/png)
    private String size;          // 文件大小 (字节)
    private String hash;          // 文件哈希值 (SHA256)
    private String filename;      // 文件名
    private String caption;       // 贴纸说明文字
}
