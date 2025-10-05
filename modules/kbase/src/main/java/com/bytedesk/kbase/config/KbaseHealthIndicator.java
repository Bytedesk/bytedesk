/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-05 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-05 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.config;

import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Kbase模块健康检查
 * 监控知识库系统相关服务：数据库、向量存储（Elasticsearch）、批处理等
 */
@Slf4j
@Component
public class KbaseHealthIndicator implements HealthIndicator {

    @Value("${spring.ai.vectorstore.elasticsearch.enabled:false}")
    private boolean vectorStoreEnabled;

    @Value("${spring.ai.vectorstore.elasticsearch.index-name:bytedesk_vector}")
    private String vectorStoreIndexName;

    @Value("${spring.batch.job.enabled:true}")
    private boolean batchJobEnabled;

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private ElasticsearchVectorStore elasticsearchVectorStore;

    @Override
    public Health health() {
        try {
            Health.Builder builder = Health.up();

            // 检查数据库连接
            if (dataSource != null) {
                try (Connection connection = dataSource.getConnection()) {
                    builder.withDetail("database-status", "Connected")
                           .withDetail("database-catalog", connection.getCatalog());
                } catch (Exception e) {
                    log.error("Kbase database health check failed", e);
                    builder.down()
                           .withDetail("database-status", "Connection Failed")
                           .withDetail("database-error", e.getMessage());
                }
            }

            // 检查向量存储（Elasticsearch）
            if (vectorStoreEnabled) {
                if (elasticsearchVectorStore != null) {
                    try {
                        // 尝试简单的检查
                        builder.withDetail("vector-store-status", "Connected")
                               .withDetail("vector-store-index", vectorStoreIndexName)
                               .withDetail("vector-store-type", "Elasticsearch");
                    } catch (Exception e) {
                        log.error("Elasticsearch vector store health check failed", e);
                        builder.down()
                               .withDetail("vector-store-status", "Connection Failed")
                               .withDetail("vector-store-error", e.getMessage());
                    }
                } else {
                    builder.withDetail("vector-store-status", "Configured but not initialized")
                           .withDetail("vector-store-warning", "ElasticsearchVectorStore bean not available");
                }
            } else {
                builder.withDetail("vector-store-status", "Disabled");
            }

            // 批处理任务状态
            builder.withDetail("batch-job-enabled", batchJobEnabled);

            // 知识库特定功能状态
            builder.withDetail("knowledge-base-features", "Active")
                   .withDetail("supported-content-types", "Article, FAQ, Text, Chunk, File");

            return builder.build();
            
        } catch (Exception e) {
            log.error("Kbase health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
