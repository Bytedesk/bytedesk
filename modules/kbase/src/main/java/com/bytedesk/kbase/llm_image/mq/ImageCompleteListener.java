/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-05 15:10:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 15:10:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_image.mq;

import java.util.Optional;
import java.util.List;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.jms.JmsArtemisConsts;
import com.bytedesk.kbase.llm_image.ImageEntity;
import com.bytedesk.kbase.llm_image.ImageRestService;
import com.bytedesk.kbase.llm_image.ImageStatusEnum;
import com.bytedesk.kbase.llm_file.FileEntity;
import com.bytedesk.kbase.llm_file.FileRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Image处理完成监听器
 * 用于统计文件的image处理进度并更新文件状态
 */
@Slf4j
@Component
@AllArgsConstructor
public class ImageCompleteListener {

    private final ImageRestService imageRestService;
    private final FileRestService fileRestService;

    /**
     * 监听image处理完成的消息
     * 当文件的所有image都处理完成时，更新文件状态
     */
    @JmsListener(destination = JmsArtemisConsts.QUEUE_IMAGE_COMPLETE, containerFactory = "jmsArtemisQueueFactory")
    public void onImageProcessComplete(ImageCompleteMessage message) {
        try {
            log.debug("接收到image处理完成通知: 文件={}, image={}", message.getFileUid(), message.getImageUid());
            
            // 检查该文件的所有image是否都已处理完成
            checkAndUpdateFileStatus(message.getFileUid());
            
        } catch (Exception e) {
            log.error("处理image完成通知时出错: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 检查文件的所有image处理状态并更新文件状态
     */
    private void checkAndUpdateFileStatus(String fileUid) {
        try {
            Optional<FileEntity> fileOpt = fileRestService.findByUid(fileUid);
            if (!fileOpt.isPresent()) {
                log.warn("找不到文件: {}", fileUid);
                return;
            }
            
            FileEntity file = fileOpt.get();
            
            // 查询该文件的所有image
            List<ImageEntity> images = imageRestService.findByFileUid(fileUid);
            if (images.isEmpty()) {
                log.warn("文件没有关联的images: {}", fileUid);
                return;
            }
            
            // 统计处理状态
            long totalImages = images.size();
            long elasticSuccessCount = images.stream()
                    .mapToLong(image -> ImageStatusEnum.SUCCESS.name().equals(image.getElasticStatus()) ? 1 : 0)
                    .sum();
            long vectorSuccessCount = images.stream()
                    .mapToLong(image -> ImageStatusEnum.SUCCESS.name().equals(image.getVectorStatus()) ? 1 : 0)
                    .sum();
            long elasticErrorCount = images.stream()
                    .mapToLong(image -> ImageStatusEnum.ERROR.name().equals(image.getElasticStatus()) ? 1 : 0)
                    .sum();
            long vectorErrorCount = images.stream()
                    .mapToLong(image -> ImageStatusEnum.ERROR.name().equals(image.getVectorStatus()) ? 1 : 0)
                    .sum();
            
            log.info("文件{}的image处理状态统计: 总数={}, 全文索引成功={}, 向量索引成功={}, 全文索引失败={}, 向量索引失败={}", 
                    file.getFileName(), totalImages, elasticSuccessCount, vectorSuccessCount, elasticErrorCount, vectorErrorCount);
            
            // 更新文件状态
            boolean updated = false;
            
            // 检查全文索引状态
            if (elasticSuccessCount == totalImages) {
                if (!ImageStatusEnum.SUCCESS.name().equals(file.getElasticStatus())) {
                    file.setElasticSuccess();
                    updated = true;
                }
            } else if (elasticErrorCount > 0 && (elasticSuccessCount + elasticErrorCount) == totalImages) {
                if (!ImageStatusEnum.ERROR.name().equals(file.getElasticStatus())) {
                    file.setElasticError();
                    updated = true;
                }
            }
            
            // 检查向量索引状态
            if (vectorSuccessCount == totalImages) {
                if (!ImageStatusEnum.SUCCESS.name().equals(file.getVectorStatus())) {
                    file.setVectorSuccess();
                    updated = true;
                }
            } else if (vectorErrorCount > 0 && (vectorSuccessCount + vectorErrorCount) == totalImages) {
                if (!ImageStatusEnum.ERROR.name().equals(file.getVectorStatus())) {
                    file.setVectorError();
                    updated = true;
                }
            }
            
            if (updated) {
                fileRestService.save(file);
                log.info("文件状态已更新: {}, 全文索引: {}, 向量索引: {}", 
                        file.getFileName(), file.getElasticStatus(), file.getVectorStatus());
            }
            
        } catch (Exception e) {
            log.error("检查和更新文件状态时出错: {}, 错误: {}", fileUid, e.getMessage(), e);
        }
    }
}
