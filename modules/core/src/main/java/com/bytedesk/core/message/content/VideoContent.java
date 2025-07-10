/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-17 15:43:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-19 16:20:48
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
 * 视频消息内容类
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VideoContent extends BaseContent {
    private String url;           // 视频URL
    private String coverUrl;      // 视频封面URL
    private String duration;      // 视频时长 (秒)
    private String width;         // 视频宽度
    private String height;        // 视频高度
    private String format;        // 视频格式 (兼容性字段)
    private String mimeType;      // MIME类型 (如: video/mp4, video/avi)
    private String label;         // 视频标签/说明
    private String size;          // 文件大小 (字节)
    private String hash;          // 文件哈希值 (SHA256)
    private String filename;      // 文件名
    private String caption;       // 视频说明文字

    /**
     * 从JSON字符串反序列化为VideoContent对象
     * @param json JSON字符串
     * @return VideoContent对象，如果解析失败返回null
     */
    public static VideoContent fromJson(String json) {
        return BaseContent.fromJson(json, VideoContent.class);
    }
} 