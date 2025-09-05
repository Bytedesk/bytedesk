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
package com.bytedesk.kbase.llm_website.crawl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 抓取结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlResult {
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 总页面数
     */
    private int totalPages;
    
    /**
     * 成功抓取的页面数
     */
    private int successPages;
    
    /**
     * 失败的页面数
     */
    private int failedPages;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 开始时间
     */
    private long startTime;
    
    /**
     * 结束时间
     */
    private long endTime;
    
    /**
     * 耗时（毫秒）
     */
    private long duration;
    
    /**
     * 创建成功结果
     */
    public static CrawlResult success(int successPages, int failedPages) {
        return CrawlResult.builder()
            .success(true)
            .totalPages(successPages + failedPages)
            .successPages(successPages)
            .failedPages(failedPages)
            .build();
    }
    
    /**
     * 创建失败结果
     */
    public static CrawlResult failure(String errorMessage) {
        return CrawlResult.builder()
            .success(false)
            .errorMessage(errorMessage)
            .build();
    }
    
    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        if (totalPages == 0) {
            return 0.0;
        }
        return (double) successPages / totalPages * 100;
    }
}
