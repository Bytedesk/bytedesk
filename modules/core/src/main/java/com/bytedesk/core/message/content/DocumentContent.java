/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-17 17:43:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-19 16:18:42
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
 * 文档消息内容类
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentContent extends BaseContent {
    private String url;       // 文档文件URL
    private String name;      // 文件名称
    private String size;      // 文件大小
    private String type;      // 文件MIME类型
    private String caption;   // 文档说明文字
    private String thumbnail; // 缩略图URL
    private String label;     // 文档标签
}
