/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-27 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 17:25:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload.watermark;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 水印配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "bytedesk.watermark")
public class WatermarkConfig {

    /**
     * 是否启用水印
     */
    private boolean enabled = false;

    /**
     * 水印文字
     */
    private String text = "bytedesk.com";

    /**
     * 水印位置
     */
    private WatermarkService.WatermarkPosition position = WatermarkService.WatermarkPosition.BOTTOM_RIGHT;

    /**
     * 字体大小
     */
    private int fontSize = 24;

    /**
     * 字体名称
     */
    private String fontName = "Arial";

    /**
     * 水印颜色 (RGBA格式，例如: "255,255,255,128")
     */
    private String color = "255,255,255,128";

    /**
     * 透明度 (0.0-1.0)
     */
    private float opacity = 0.5f;

    /**
     * 边距
     */
    private int margin = 20;

    /**
     * 是否只对图片文件添加水印
     */
    private boolean imageOnly = true;

    /**
     * 最小图片尺寸（像素），小于此尺寸的图片不添加水印
     */
    private int minImageSize = 100;

    /**
     * 最大图片尺寸（像素），大于此尺寸的图片不添加水印
     */
    private int maxImageSize = 5000;
} 