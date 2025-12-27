/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-26 11:11:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-26 11:29:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.upload.storage.UploadStorageException;

@Configuration
public class UploadConfig {

    @Autowired
    private BytedeskProperties properties;
    
    @Bean
    public Path uploadDir() {
        if (properties.getUploadDir().trim().length() == 0) {
        throw new UploadStorageException("上传目录未配置（bytedesk.uploadDir 不能为空）", 500);
        }
        return Paths.get(properties.getUploadDir());
    }
}
