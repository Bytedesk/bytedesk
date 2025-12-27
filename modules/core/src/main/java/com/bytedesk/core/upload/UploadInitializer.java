/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 11:02:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.bytedesk.core.upload.storage.UploadStorageException;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UploadInitializer {

    private final Path uploadDir;

    @PostConstruct
    public void init() {
        try {
			Files.createDirectories(uploadDir);
		} catch (IOException e) {
            throw new UploadStorageException(
                "初始化上传目录失败（请检查磁盘权限/路径配置）: " + (e.getMessage() == null ? "未知错误" : e.getMessage()),
                503,
                e
            );
		}
    }
    
}
