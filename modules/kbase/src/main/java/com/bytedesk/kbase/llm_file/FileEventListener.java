/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 11:42:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_file;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.document.Document;
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

    private final FileService fileService;

    private final FileChunkService fileChunkService;
    
    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();
        // 专门存储大模型上传文件记录
        if (UploadTypeEnum.LLM_FILE.name().equalsIgnoreCase(upload.getType())) {
            log.info("UploadEventListener LLM_FILE: {}", upload.getFileName());
            // 
            List<Document> documents = fileService.parseFileContent(upload);
            
            // 使用智能摘要提取方法
            int maxContentLength = 60000; // 设置最大内容长度，预留一些空间
            String content = fileService.extractContentSummary(documents, maxContentLength);
            // 
            UserProtobuf userProtobuf = UserProtobuf.fromJson(upload.getUser());
            // 转换为 fileEntity，并保存
            FileRequest fileRequest = FileRequest.builder()
                .uploadUid(upload.getUid())
                .fileName(upload.getFileName())
                .fileUrl(upload.getFileUrl())
                .content(content)
                .categoryUid(upload.getCategoryUid())
                .kbUid(upload.getKbUid())
                .userUid(userProtobuf.getUid())
                .orgUid(upload.getOrgUid())
                .build();
            // 读取文件内容，写入到content字段，迁移到 ai 模块 SpringAIEventListener中读取
            FileResponse fileResponse = fileRestService.create(fileRequest);
            
            // 直接在这里进行chunk切分，使用专门的服务
            List<String> docIdList = fileChunkService.processFileChunks(documents, fileResponse);
            
            // 更新文件状态和docIdList
            Optional<FileEntity> fileEntity = fileRestService.findByUid(fileResponse.getUid());
            if (fileEntity.isPresent()) {
                FileEntity file = fileEntity.get();
                file.setSuccess();
                file.setDocIdList(docIdList);
                fileRestService.save(file);
            } else {
                log.warn("文件实体不存在: {}", fileResponse.getUid());
            }
        }
    }


}

