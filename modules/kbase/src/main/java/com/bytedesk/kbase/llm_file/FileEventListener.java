/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 16:20:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_file;

import java.io.IOException;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;

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
        // 专门存储大模型上传文件记录
        if (upload.getType().equalsIgnoreCase(UploadTypeEnum.LLM_FILE.name())) {
            log.info("UploadEventListener LLM_FILE: {}", upload.getFileName());
            // 
            UserProtobuf userProtobuf = UserProtobuf.fromJson(upload.getUser());
            FileUploadExtra extra = FileUploadExtra.fromJson(upload.getExtra());
            // 转换为 fileEntity，并保存
            FileRequest fileRequest = FileRequest.builder()
                .uploadUid(upload.getUid())
                .fileName(upload.getFileName())
                .fileUrl(upload.getFileUrl())
                .autoGenerateLlmQa(extra.isAutoGenerateLlmQa())
                .autoLlmChunk(extra.isAutoLlmChunk())
                .categoryUid(upload.getCategoryUid())
                .kbUid(upload.getKbUid())
                .userUid(userProtobuf.getUid())
                .orgUid(upload.getOrgUid())
                .build();
            // 读取文件内容，写入到content字段，迁移到 ai 模块 SpringAIEventListener中读取
            fileRestService.create(fileRequest);
        }
    }


}

