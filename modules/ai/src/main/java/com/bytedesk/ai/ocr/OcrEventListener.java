/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-28 15:33:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-29 10:27:31
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.robot.RobotService;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
import com.bytedesk.core.utils.BdUploadUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class OcrEventListener {

    private final RobotService robotService;

    /**
     * 监听上传创建事件，判断是否图片类型，进行OCR识别
     * 
     * @param event 上传创建事件
     */
    @EventListener
    @Async
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();
        
        // 检查是否为LLM相关文件上传
        if (!UploadTypeEnum.LLM_FILE.name().equalsIgnoreCase(upload.getType()) 
            && !UploadTypeEnum.LLM.name().equalsIgnoreCase(upload.getType())) {
            log.debug("跳过非LLM类型文件: {}", upload.getType());
            return;
        }
        
        // 检查是否为图片文件
        if (!isImageFile(upload)) {
            log.debug("跳过非图片文件: {} ({})", upload.getFileName(), upload.getFileType());
            return;
        }
        
        // 获取组织UID，如果没有则使用默认值
        String orgUid = upload.getOrgUid();
        if (orgUid == null || orgUid.isEmpty()) {
            log.warn("UploadEntity缺少orgUid，跳过OCR处理: {}", upload.getFileName());
            return;
        }
        
        log.info("开始OCR处理图片: {} (URL: {})", upload.getFileName(), upload.getFileUrl());
        
        try {
            // 调用OCR服务
            String extractedText = robotService.ocrExtraction(upload.getFileUrl(), orgUid);
            
            if (extractedText != null && !extractedText.isEmpty()) {
                log.info("OCR提取成功，文件: {}，提取文字长度: {} 字符", 
                    upload.getFileName(), extractedText.length());
                
                // 这里可以将提取的文字保存到数据库，或者发送通知等
                // 例如：ocrResultService.saveOcrResult(upload.getUid(), extractedText);
                log.debug("OCR提取结果预览: {}", 
                    extractedText.length() > 100 ? extractedText.substring(0, 100) + "..." : extractedText);
            } else {
                log.info("OCR提取结果为空，文件: {}", upload.getFileName());
            }
        } catch (Exception e) {
            log.error("OCR处理失败，文件: {} - 错误: {}", upload.getFileName(), e.getMessage(), e);
        }
    }

    /**
     * 判断是否为图片文件
     * 
     * @param upload 上传实体
     * @return 是否为图片文件
     */
    private boolean isImageFile(UploadEntity upload) {
        return BdUploadUtils.isImageFile(upload.getFileName(), upload.getFileType());
    }
    
}
