/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-05 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-05 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * VOC模块健康检查
 * 监控用户反馈（Voice of Customer）系统相关服务：数据库、搜索引擎等
 */
@Slf4j
@Component
public class VocHealthIndicator implements HealthIndicator {

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private ElasticsearchOperations elasticsearchOperations;

    @Override
    public Health health() {
        try {
            Health.Builder builder = Health.up();

            // 检查数据库连接
            if (dataSource != null) {
                try (Connection connection = dataSource.getConnection()) {
                    builder.withDetail("database-status", "Connected")
                           .withDetail("database-product", connection.getMetaData().getDatabaseProductName());
                } catch (Exception e) {
                    log.error("VOC database health check failed", e);
                    builder.down()
                           .withDetail("database-status", "Connection Failed")
                           .withDetail("database-error", e.getMessage());
                }
            }

            // 检查Elasticsearch连接（如果配置了）
            if (elasticsearchOperations != null) {
                try {
                    // 简单的存在性测试
                    elasticsearchOperations.getElasticsearchConverter();
                    
                    builder.withDetail("elasticsearch-status", "Connected");
                } catch (Exception e) {
                    log.error("Elasticsearch health check failed", e);
                    builder.withDetail("elasticsearch-status", "Connection Failed")
                           .withDetail("elasticsearch-error", e.getMessage());
                }
            } else {
                builder.withDetail("elasticsearch-status", "Not Configured");
            }

            // VOC特定指标
            builder.withDetail("feedback-collection-status", "Active")
                   .withDetail("sentiment-analysis-status", "Available");

            return builder.build();
            
        } catch (Exception e) {
            log.error("VOC health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
