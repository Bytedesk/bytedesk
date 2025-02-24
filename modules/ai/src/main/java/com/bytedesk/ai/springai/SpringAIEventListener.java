/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-24 09:34:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-24 10:01:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import java.io.IOException;

import org.springframework.context.event.EventListener;

import com.bytedesk.kbase.upload.UploadEntity;
import com.bytedesk.kbase.upload.UploadTypeEnum;
import com.bytedesk.kbase.upload.event.UploadCreateEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpringAIEventListener {
    
    private final SpringAIVectorService vectorService;

    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) throws IOException {
        UploadEntity upload = event.getUpload();
        log.info("UploadEventListener create: {}", upload.toString());
        // etl分块处理
        if (upload.getType().equals(UploadTypeEnum.LLM.name())) {
            // 通知python ai模块处理
            // uploadVectorStore.readSplitWriteToVectorStore(upload);
            return;
        }
    }
    
    
}
