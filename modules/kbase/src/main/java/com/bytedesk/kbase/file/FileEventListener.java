/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 16:00:21
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

import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
import com.bytedesk.core.upload.event.UploadUpdateEvent;

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
        log.info("UploadEventListener create: {}", upload.getFileName());
        // 专门存储大模型上传文件记录
        if (upload.getType().equalsIgnoreCase(UploadTypeEnum.LLM.name())) {
            // 转换为 fileEntity，并保存
            FileRequest fileRequest = FileRequest.builder()
                .uploadUid(upload.getUid())
                .fileName(upload.getFileName())
                .fileUrl(upload.getFileUrl())
                .categoryUid(upload.getCategoryUid())
                .kbUid(upload.getKbUid())
                .build();
            fileRequest.setOrgUid(upload.getOrgUid());
            UserProtobuf userProtobuf = UserProtobuf.fromJson(upload.getUser());
            fileRequest.setUserUid(userProtobuf.getUid());
            // 读取文件内容，写入到content字段，迁移到 ai 模块 SpringAIEventListener中读取
            fileRestService.create(fileRequest);
        }
    }

    @EventListener
    public void onUploadUpdateEvent(UploadUpdateEvent event) {
        UploadEntity upload = event.getUpload();
        log.info("UploadEventListener update: {}", upload.getFileName());
        // 后台删除文件记录
        if (upload.isDeleted()) {
            // 通知python ai模块处理
            // redisPubsubService.sendDeleteFileMessage(upload.getUid(), upload.getDocIdList());
            // 删除redis中缓存的document
            // springAIVectorService.deleteDoc(upload.getDocIdList());
        }
    }
}

