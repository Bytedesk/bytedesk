/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-23 16:59:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 17:02:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.file;

import java.io.Serializable;

import com.alibaba.fastjson2.JSON;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadExtra implements Serializable {

    private static final long serialVersionUID = 1L;

    // 使用 "true" 或 "false" 来表示布尔值
    private String autoGenerateLlmQa;

    // 使用 "true" 或 "false" 来表示布尔值
    private String autoLlmSplit;

    // 转换为布尔值
    public boolean isAutoGenerateLlmQa() {
        return Boolean.parseBoolean(autoGenerateLlmQa);
    }

    // 转换为布尔值
    public boolean isAutoLlmSplit() {
        return Boolean.parseBoolean(autoLlmSplit);
    }

    // fromJson
    public static FileUploadExtra fromJson(String extra) {
        return JSON.parseObject(extra, FileUploadExtra.class);
    }
    
}
