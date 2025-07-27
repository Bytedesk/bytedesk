/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 20:24:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-27 23:45:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import org.springframework.http.MediaType;

import java.io.File;

/**
 * 统一上传服务接口
 * 支持阿里云OSS和腾讯云COS
 *
 * @author bytedesk.com
 */
public interface UploadService {

    /**
     * 上传附件
     */
    String uploadAttachment(MediaType mediaType, String fileName, int width, int height, String username, File file);

    /**
     * 上传头像
     */
    String uploadAvatar(String fileName, File file);

    /**
     * 上传图片
     */
    String uploadImage(String fileName, File file);

    /**
     * 上传语音
     */
    String uploadVoice(String fileName, File file);

    /**
     * 上传视频
     */
    String uploadVideo(String fileName, File file);

    /**
     * 上传文件
     */
    String uploadFile(String fileName, File file);

} 