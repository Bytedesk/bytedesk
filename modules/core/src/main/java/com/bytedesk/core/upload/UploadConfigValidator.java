/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 20:24:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-27 23:38:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import com.bytedesk.core.upload.aliyun.AliyunProperties;
import com.bytedesk.core.upload.tencent.TencentProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 上传配置验证类
 * 在应用启动时验证配置的正确性
 *
 * @author bytedesk.com
 */
@Slf4j
@Component
public class UploadConfigValidator {

    @Value("${bytedesk.upload.provider:aliyun}")
    private String uploadProvider;

    @Autowired(required = false)
    private AliyunProperties aliyunProperties;

    @Autowired(required = false)
    private TencentProperties tencentProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void validateUploadConfig() {
        log.info("开始验证上传配置...");
        
        boolean aliyunEnabled = aliyunProperties != null && aliyunProperties.isEnabled();
        boolean tencentEnabled = tencentProperties != null && tencentProperties.isEnabled();
        
        log.info("阿里云OSS启用状态: {}", aliyunEnabled);
        log.info("腾讯云COS启用状态: {}", tencentEnabled);
        log.info("当前配置的上传提供商: {}", uploadProvider);
        
        // 检查是否启用了多个云存储服务
        if (aliyunEnabled && tencentEnabled) {
            log.warn("警告: 同时启用了阿里云OSS和腾讯云COS，可能会导致冲突！");
        }
        
        // 检查配置的提供商是否已启用
        if (uploadProvider.equalsIgnoreCase("aliyun") || uploadProvider.equalsIgnoreCase("oss")) {
            if (!aliyunEnabled) {
                log.error("错误: 配置使用阿里云OSS，但阿里云OSS未启用！请设置 bytedesk.aliyun.enabled=true");
            } else {
                log.info("阿里云OSS配置验证通过");
            }
        } else if (uploadProvider.equalsIgnoreCase("tencent") || uploadProvider.equalsIgnoreCase("cos")) {
            if (!tencentEnabled) {
                log.error("错误: 配置使用腾讯云COS，但腾讯云COS未启用！请设置 bytedesk.tencent.enabled=true");
            } else {
                log.info("腾讯云COS配置验证通过");
            }
        }
        
        // 检查是否有可用的云存储服务
        if (!aliyunEnabled && !tencentEnabled) {
            log.error("错误: 没有启用任何云存储服务！请至少启用一个云存储服务");
        }
        
        log.info("上传配置验证完成");
    }
} 