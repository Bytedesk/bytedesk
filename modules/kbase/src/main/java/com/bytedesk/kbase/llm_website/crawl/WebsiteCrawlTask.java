/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-05 16:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 17:02:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_website.crawl;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * 抓取任务实体
 */
@Slf4j
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_kbase_llm_website_crawl_task")
public class WebsiteCrawlTask extends BaseEntity {
    
    /**
     * 任务ID
     */
    @Column(unique = true, nullable = false)
    private String taskId;
    
    /**
     * 网站UID
     */
    @Column(nullable = false)
    private String websiteUid;
    
    /**
     * 网站URL
     */
    @Column(nullable = false)
    private String websiteUrl;
    
    /**
     * 任务状态
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WebsiteCrawlStatus status = WebsiteCrawlStatus.PENDING;
    
    /**
     * 抓取配置（JSON格式存储）
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String configJson;
    
    /**
     * 总页面数
     */
    @Builder.Default
    private Integer totalPages = 0;
    
    /**
     * 已处理页面数
     */
    @Builder.Default
    private Integer processedPages = 0;
    
    /**
     * 成功页面数
     */
    @Builder.Default
    private Integer successPages = 0;
    
    /**
     * 失败页面数
     */
    @Builder.Default
    private Integer failedPages = 0;
    
    /**
     * 开始时间
     */
    private Long startTime;
    
    /**
     * 结束时间
     */
    private Long endTime;
    
    /**
     * 错误信息
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String errorMessage;
    
    /**
     * 抓取结果（JSON格式存储）
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String resultJson;
    
    /**
     * 最后更新时间
     */
    private ZonedDateTime lastUpdateTime;
    
    // 辅助方法
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 设置抓取配置
     */
    public void setConfig(WebsiteCrawlConfig config) {
        try {
            this.configJson = objectMapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            log.error("序列化抓取配置失败", e);
        }
    }
    
    /**
     * 获取抓取配置
     */
    public WebsiteCrawlConfig getConfig() {
        try {
            if (configJson != null) {
                return objectMapper.readValue(configJson, WebsiteCrawlConfig.class);
            }
        } catch (JsonProcessingException e) {
            log.error("反序列化抓取配置失败", e);
        }
        return WebsiteCrawlConfig.getDefault();
    }
    
    /**
     * 设置抓取结果
     */
    public void setResult(WebsiteCrawlResult result) {
        try {
            this.resultJson = objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            log.error("序列化抓取结果失败", e);
        }
    }
    
    /**
     * 获取抓取结果
     */
    public WebsiteCrawlResult getResult() {
        try {
            if (resultJson != null) {
                return objectMapper.readValue(resultJson, WebsiteCrawlResult.class);
            }
        } catch (JsonProcessingException e) {
            log.error("反序列化抓取结果失败", e);
        }
        return null;
    }
    
    /**
     * 获取进度百分比
     */
    public double getProgressPercent() {
        if (totalPages == null || totalPages == 0) {
            return 0.0;
        }
        return (double) (processedPages != null ? processedPages : 0) / totalPages * 100;
    }
    
    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        if (processedPages == null || processedPages == 0) {
            return 0.0;
        }
        return (double) (successPages != null ? successPages : 0) / processedPages * 100;
    }
    
    /**
     * 获取持续时间（毫秒）
     */
    public Long getDuration() {
        if (startTime == null) {
            return null;
        }
        
        long endTimeValue = endTime != null ? endTime : System.currentTimeMillis();
        return endTimeValue - startTime;
    }
    
    /**
     * 是否正在运行
     */
    public boolean isRunning() {
        return status == WebsiteCrawlStatus.RUNNING;
    }
    
    /**
     * 是否已完成
     */
    public boolean isCompleted() {
        return status == WebsiteCrawlStatus.COMPLETED || status == WebsiteCrawlStatus.FAILED || status == WebsiteCrawlStatus.STOPPED;
    }
}
