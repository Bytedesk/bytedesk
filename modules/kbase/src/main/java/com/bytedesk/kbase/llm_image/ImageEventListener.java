/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-23 16:48:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_image;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.kbase.llm_image.event.ImageCreateEvent;
import com.bytedesk.kbase.llm_image.event.ImageDeleteEvent;
import com.bytedesk.kbase.llm_image.event.ImageUpdateDocEvent;
import com.bytedesk.kbase.llm_image.mq.ImageMessageService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ImageEventListener {

    private final ImageMessageService imageMessageService;

    // 使用MQ异步处理Image索引，解决大文件处理时的并发问题
    @EventListener
    public void onImageCreateEvent(ImageCreateEvent event) {
        ImageEntity image = event.getImage();
        log.info("ImageEventListener onImageCreateEvent: {} - 发送到MQ队列异步处理", image.getName());
        
        try {
            // 发送到MQ队列异步处理，避免直接阻塞事件处理
            String fileUid = image.getFile() != null ? image.getFile().getUid() : null;
            imageMessageService.sendToIndexQueue(image.getUid(), fileUid);
            log.debug("Image索引消息已发送到队列: {}", image.getUid());
        } catch (Exception e) {
            log.error("发送Image索引消息到队列失败: {}, 错误: {}", image.getName(), e.getMessage(), e);
            // 这里不抛出异常，避免影响主流程
        }
    }

    // 使用MQ异步处理Image更新索引
    @EventListener
    public void onImageUpdateDocEvent(ImageUpdateDocEvent event) {
        ImageEntity image = event.getImage();
        log.info("ImageEventListener ImageUpdateDocEvent: {} - 发送到MQ队列异步处理", image.getName());
        
        try {
            // 发送到MQ队列异步处理
            String fileUid = image.getFile() != null ? image.getFile().getUid() : null;
            imageMessageService.sendToIndexQueue(image.getUid(), fileUid);
            log.debug("Image更新索引消息已发送到队列: {}", image.getUid());
        } catch (Exception e) {
            log.error("发送Image更新索引消息到队列失败: {}, 错误: {}", image.getName(), e.getMessage(), e);
            // 这里不抛出异常，避免影响主流程
        }
    }

    @EventListener
    public void onImageDeleteEvent(ImageDeleteEvent event) {
        ImageEntity image = event.getImage();
        log.info("ImageEventListener onImageDeleteEvent: {} - 发送到MQ队列异步处理", image.getName());
        
        try {
            // 发送到MQ队列异步处理删除
            String fileUid = image.getFile() != null ? image.getFile().getUid() : null;
            imageMessageService.sendToDeleteQueue(image.getUid(), fileUid);
            log.debug("Image删除消息已发送到队列: {}", image.getUid());
        } catch (Exception e) {
            log.error("发送Image删除消息到队列失败: {}, 错误: {}", image.getName(), e.getMessage(), e);
            // 这里不抛出异常，避免影响主流程
        }
    }

}


