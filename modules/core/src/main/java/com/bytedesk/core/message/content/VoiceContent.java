/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-17 17:42:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-17 17:44:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message.content;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 语音消息内容类
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VoiceContent implements Serializable {
    private String url;       // 语音文件URL
    private String duration;  // 语音时长（秒）
    private String format;    // 文件格式（如ogg, mp3）
    private String caption;   // 语音说明文字
    private String label;     // 语音标签
}
