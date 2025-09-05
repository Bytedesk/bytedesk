/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-05 16:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 16:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_website;

import com.bytedesk.kbase.llm_website.crawl.WebsiteCrawlConfig;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网站抓取请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "网站抓取请求")
public class WebsiteCrawlRequest {
    
    @NotBlank(message = "网站UID不能为空")
    @Schema(description = "网站UID", example = "123456789")
    private String websiteUid;
    
    @Schema(description = "抓取配置", example = "{}")
    private WebsiteCrawlConfig config;
}
