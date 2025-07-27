/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 20:24:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-27 23:56:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload.tencent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 腾讯云COS上传服务实现
 *
 * @author bytedesk.com
 */
@Service("tencentUploadService")
@ConditionalOnProperty(name = "bytedesk.tencent.enabled", havingValue = "true", matchIfMissing = false)
public class TencentUploadService {

    @Autowired
    private TencentCos tencentCos;

    
    public String uploadAttachment(MediaType mediaType, String fileName, int width, int height, String username, File file) {
        return tencentCos.uploadAttachment(mediaType, fileName, width, height, username, file);
    }

    
    public String uploadAvatar(String fileName, File file) {
        return tencentCos.uploadAvatar(fileName, file);
    }

    
    public String uploadImage(String fileName, File file) {
        return tencentCos.uploadImage(fileName, file);
    }

    
    public String uploadVoice(String fileName, File file) {
        return tencentCos.uploadVoice(fileName, file);
    }

    
    public String uploadVideo(String fileName, File file) {
        return tencentCos.uploadVideo(fileName, file);
    }

    
    public String uploadFile(String fileName, File file) {
        return tencentCos.uploadFile(fileName, file);
    }

} 