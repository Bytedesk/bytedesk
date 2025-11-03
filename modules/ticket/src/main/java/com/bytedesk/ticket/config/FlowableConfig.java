/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 13:45:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-18 22:16:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.config;

import org.flowable.app.spring.SpringAppEngineConfiguration;
import org.flowable.cmmn.spring.SpringCmmnEngineConfiguration;
import org.flowable.dmn.spring.SpringDmnEngineConfiguration;
import org.flowable.eventregistry.spring.SpringEventRegistryEngineConfiguration;
import org.flowable.idm.spring.SpringIdmEngineConfiguration;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Force Flowable to use the PostgreSQL databaseType for KingbaseES.
 * Kingbase is PostgreSQL-compatible, but Flowable can't detect it from
 * the product name "KingbaseES". Setting databaseType to "postgres"
 * avoids the startup failure.
 */
@Configuration
public class FlowableConfig {

    /**
     * Make database type configurable via application properties.
     * Examples:
     * - flowable.database-type=postgres   (for Kingbase/PostgreSQL)
     * - flowable.database-type=mysql      (for MySQL)
     * If not set, Flowable will try to auto-detect. For Kingbase please set to postgres.
     */
    @Value("${flowable.database-type:}")
    private String configuredDbType;

    private String normalizeDbType(String dbType) {
        if (!StringUtils.hasText(dbType)) {
            return null;
        }
        String v = dbType.trim().toLowerCase();
        // Normalize common aliases
        if (v.equals("postgresql") || v.equals("pg")) {
            return "postgres";
        }
        if (v.startsWith("kingbase")) { // kingbase / kingbasees -> postgres
            return "postgres";
        }
        return v; // mysql, oracle, mssql, db2, h2, etc.
    }

    private void applyDbType(org.flowable.common.engine.impl.AbstractEngineConfiguration configuration) {
        String normalized = normalizeDbType(configuredDbType);
        if (StringUtils.hasText(normalized)) {
            configuration.setDatabaseType(normalized);
        }
    }

    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> processEngineConfigurer() {
        // engineConfiguration.setEnableProcessDefinitionInfoCache(true);
        return configuration -> applyDbType(configuration);
    }

    @Bean
    public EngineConfigurationConfigurer<SpringDmnEngineConfiguration> dmnEngineConfigurer() {
        return configuration -> applyDbType(configuration);
    }

    @Bean
    public EngineConfigurationConfigurer<SpringCmmnEngineConfiguration> cmmnEngineConfigurer() {
        return configuration -> applyDbType(configuration);
    }

    @Bean
    public EngineConfigurationConfigurer<SpringAppEngineConfiguration> appEngineConfigurer() {
        return configuration -> applyDbType(configuration);
    }

    @Bean
    public EngineConfigurationConfigurer<SpringIdmEngineConfiguration> idmEngineConfigurer() {
        return configuration -> applyDbType(configuration);
    }

    @Bean
    public EngineConfigurationConfigurer<SpringEventRegistryEngineConfiguration> eventRegistryEngineConfigurer() {
        return configuration -> applyDbType(configuration);
    }
}
