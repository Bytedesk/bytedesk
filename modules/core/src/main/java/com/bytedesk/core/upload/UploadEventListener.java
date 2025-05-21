/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-28 06:48:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-12 13:04:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class UploadEventListener {

    // private final UploadRestService uploadService;

    // @EventListener
    // public void onUploadCreateEvent(UploadCreateEvent event) throws IOException {
    //     UploadEntity upload = event.getUpload();
    //     log.info("UploadEventListener create: {}", upload.toString());
    //     // etl分块处理
    //     // type contains llm
    //     if (upload.getType().toLowerCase().contains(UploadTypeEnum.LLM.name().toLowerCase())) {
    //         // ai 模块处理
    //         return;
    //     }
    //     // 导入Excel文件
    //     // Resource resource = uploadService.loadAsResource(upload.getFileName());
    //     // if (resource.exists()) {
    //     //     String filePath = resource.getFile().getAbsolutePath();
    //     //     log.info("UploadEventListener loadAsResource: {}", filePath);
    //     // }
    // }

    // @EventListener
    // public void onUploadUpdateEvent(UploadUpdateEvent event) {
    //     UploadEntity upload = event.getUpload();
    //     log.info("UploadEventListener update: {}", upload.toString());
    //     // 后台删除文件记录
    //     if (upload.isDeleted()) {
    //         // 通知python ai模块处理
    //         // redisPubsubService.sendDeleteFileMessage(upload.getUid(), upload.getDocIdList());
    //         // 删除redis中缓存的document
    //         // springAIVectorService.deleteDoc(upload.getDocIdList());
    //     }
    // }

   

}
