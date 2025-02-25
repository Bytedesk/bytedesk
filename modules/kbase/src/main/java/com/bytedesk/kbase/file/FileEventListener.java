/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 11:06:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.file;

import java.io.IOException;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.bytedesk.kbase.upload.UploadEntity;
import com.bytedesk.kbase.upload.UploadTypeEnum;
import com.bytedesk.kbase.upload.event.UploadCreateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class FileEventListener {

    private final FileRestService fileRestService;
    
    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) throws IOException {
        UploadEntity upload = event.getUpload();
        log.info("UploadEventListener create: {}", upload.toString());
        // 专门存储大模型上传文件记录
        if (upload.getType().equals(UploadTypeEnum.LLM.name())) {
            // 转换为 fileEntity，并保存
            FileRequest fileRequest = FileRequest.builder()
                .uploadUid(upload.getUid())
                .name(upload.getFileName())
                .fileUrl(upload.getFileUrl())
                .build();
            // TODO： 读取文件内容，写入到content字段
            
            
            fileRestService.create(fileRequest);
        }
    }
}

