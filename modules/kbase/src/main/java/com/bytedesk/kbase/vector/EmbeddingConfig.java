/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-04 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-07-04 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 */
package com.bytedesk.kbase.vector;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedding 配置的简单 DTO，用于跨模块传递配置信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingConfig {

    private String provider;

    private String model;

    private String apiKey;

    private String baseUrl;

    private Integer dimensions;
}
