/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 09:52:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.file;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
import com.bytedesk.kbase.file.event.FileChunkEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class FileEventListener {

    private final FileRestService fileRestService;

    private final FileChunkService fileChunkService;

    private final BytedeskEventPublisher bytedeskEventPublisher;
    
    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();
        // 专门存储大模型上传文件记录
        if (UploadTypeEnum.LLM_FILE.name().equalsIgnoreCase(upload.getType())) {
            log.info("UploadEventListener LLM_FILE: {}", upload.getFileName());
            // 
            List<Document> documents = fileChunkService.parseFileContent(upload);
            // 提取文本内容
            StringBuilder contentBuilder = new StringBuilder();
            for (Document doc : documents) {
                contentBuilder.append(doc.getText()).append("\n");
            }
            String content = contentBuilder.toString();
            // 
            UserProtobuf userProtobuf = UserProtobuf.fromJson(upload.getUser());
            // FileUploadExtra extra = FileUploadExtra.fromJson(upload.getExtra());
            // String content = fileChunkService.parseFileContent(upload);
            // 转换为 fileEntity，并保存
            FileRequest fileRequest = FileRequest.builder()
                .uploadUid(upload.getUid())
                .fileName(upload.getFileName())
                .fileUrl(upload.getFileUrl())
                .content(content)
                // .autoGenerateLlmQa(extra.isAutoGenerateLlmQa())
                // .autoLlmChunk(extra.isAutoLlmChunk())
                .categoryUid(upload.getCategoryUid())
                .kbUid(upload.getKbUid())
                .userUid(userProtobuf.getUid())
                .orgUid(upload.getOrgUid())
                .build();
            // 读取文件内容，写入到content字段，迁移到 ai 模块 SpringAIEventListener中读取
            FileResponse fileResponse = fileRestService.initVisitor(fileRequest);
            // 
            bytedeskEventPublisher.publishEvent(new FileChunkEvent(documents, fileResponse));
        }
    }


}

