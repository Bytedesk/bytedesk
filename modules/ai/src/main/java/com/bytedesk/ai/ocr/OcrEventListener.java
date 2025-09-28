/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-28 15:33:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-28 15:33:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.ocr;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class OcrEventListener {

    /**
     * 监听上传创建事件，过滤图片，处理OCR识别
     * 
     * @param event 上传创建事件
     */
    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();
        // 专门存储大模型上传文件记录
        if (UploadTypeEnum.LLM_FILE.name().equalsIgnoreCase(upload.getType())) {
            log.info("OcrEventListener LLM_FILE: {} - OCR开始", upload.getFileName());
        }
    }
    
    
}
