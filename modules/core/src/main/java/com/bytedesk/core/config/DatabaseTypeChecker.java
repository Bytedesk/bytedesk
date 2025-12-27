/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 07:59:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 09:59:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DatabaseTypeChecker {

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void checkDatabaseType() {
        String databaseType = getDatabaseType();
        log.info("checkDatabaseType Connected to:  {}", databaseType);
    }

    public String getDatabaseType() {
        if (dataSourceUrl.contains("mysql")) {
            return "MySQL";
        } else if (dataSourceUrl.contains("postgresql")) {
            return "PostgreSQL";
        } else if (dataSourceUrl.contains("oracle")) {
            return "Oracle";
        } else {
            return "Unknown";
        }
    }

    public String getDatabaseTypeByDatasource() {
        String dataSourceClassName = dataSource.getClass().getName();
        if (dataSourceClassName.contains("Mysql") || dataSourceClassName.contains("MySQL")) {
            return "MySQL";
        } else if (dataSourceClassName.contains("Postgres") || dataSourceClassName.contains("PostgreSQL")) {
            return "PostgreSQL";
        } else if (dataSourceClassName.contains("Oracle")) {
            return "Oracle";
        } else {
            return "Unknown";
        }
    }

    public String getDatabaseTypeByDatasourceMetaData() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String databaseProductName = metaData.getDatabaseProductName();
            if (databaseProductName.contains("MySQL")) {
                return "MySQL";
            } else if (databaseProductName.contains("PostgreSQL")) {
                return "PostgreSQL";
            } else if (databaseProductName.contains("Oracle")) {
                return "Oracle";
            } else {
                return "Unknown";
            }
        } catch (SQLException e) {
            log.error("获取数据库类型失败", e);
            return "Error";
        }
    }
}