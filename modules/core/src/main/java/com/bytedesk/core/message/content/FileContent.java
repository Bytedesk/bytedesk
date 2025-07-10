/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-17 15:43:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-10 18:10:35
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
 * 文件消息内容类
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FileContent extends BaseContent {
    private String url;           // 文件URL
    private String name;          // 文件名称
    private String size;          // 文件大小 (字节)
    private String type;          // 文件MIME类型
    private String label;         // 文件标签/说明
    private String hash;          // 文件哈希值 (SHA256)
    private String filename;      // 文件名 (兼容性字段)

    /**
     * 从JSON字符串反序列化为FileContent对象
     * @param json JSON字符串
     * @return FileContent对象，如果解析失败返回null
     */
    public static FileContent fromJson(String json) {
        return BaseContent.fromJson(json, FileContent.class);
    }
} 