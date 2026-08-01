/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-06-11 00:00:00
 * @LastEditors: githubcopilot
 * @LastEditTime: 2026-06-11 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class DatabaseIndexBootstrapService {

    private final DataSource dataSource;

    public void ensureIndex(String tableName, String indexName, List<String> requiredColumns,
            String standardCreateSql, String mysqlCreateSql) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResolvedTable resolvedTable = resolveTable(metaData, connection, tableName);
            if (resolvedTable == null) {
                log.debug("skip index bootstrap because table {} does not exist", tableName);
                return;
            }

            Set<String> existingColumns = getExistingColumns(metaData, resolvedTable);
            Set<String> missingColumns = new LinkedHashSet<>();
            for (String requiredColumn : requiredColumns) {
                if (!existingColumns.contains(normalize(requiredColumn))) {
                    missingColumns.add(requiredColumn);
                }
            }
            if (!missingColumns.isEmpty()) {
                log.warn("skip index {} on table {} because columns are missing: {}", indexName, tableName, missingColumns);
                return;
            }

            if (indexExists(metaData, resolvedTable, indexName)) {
                log.debug("index {} already exists on table {}", indexName, tableName);
                return;
            }

            String createSql = isMySql(metaData) ? mysqlCreateSql : standardCreateSql;
            try (Statement statement = connection.createStatement()) {
                statement.execute(createSql);
                log.info("created missing index {} on table {}", indexName, tableName);
            }
        } catch (SQLException e) {
            log.warn("failed to bootstrap index {} on table {}", indexName, tableName, e);
        }
    }

    private ResolvedTable resolveTable(DatabaseMetaData metaData, Connection connection, String tableName) throws SQLException {
        String[] catalogs = new String[] { connection.getCatalog(), null };
        String[] schemas = new String[] { connection.getSchema(), null };
        String[] candidates = new String[] { tableName, tableName.toLowerCase(Locale.ROOT), tableName.toUpperCase(Locale.ROOT) };

        for (String catalog : catalogs) {
            for (String schema : schemas) {
                for (String candidate : candidates) {
                    try (ResultSet resultSet = metaData.getTables(catalog, schema, candidate, new String[] { "TABLE" })) {
                        if (resultSet.next()) {
                            return new ResolvedTable(catalog, schema, resultSet.getString("TABLE_NAME"));
                        }
                    }
                }
            }
        }
        return null;
    }

    private Set<String> getExistingColumns(DatabaseMetaData metaData, ResolvedTable resolvedTable) throws SQLException {
        Set<String> existingColumns = new LinkedHashSet<>();
        try (ResultSet resultSet = metaData.getColumns(resolvedTable.catalog(), resolvedTable.schema(), resolvedTable.tableName(), null)) {
            while (resultSet.next()) {
                existingColumns.add(normalize(resultSet.getString("COLUMN_NAME")));
            }
        }
        return existingColumns;
    }

    private boolean indexExists(DatabaseMetaData metaData, ResolvedTable resolvedTable, String indexName) throws SQLException {
        try (ResultSet resultSet = metaData.getIndexInfo(resolvedTable.catalog(), resolvedTable.schema(), resolvedTable.tableName(), false, false)) {
            while (resultSet.next()) {
                String existingIndexName = resultSet.getString("INDEX_NAME");
                if (existingIndexName != null && normalize(existingIndexName).equals(normalize(indexName))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isMySql(DatabaseMetaData metaData) throws SQLException {
        return normalize(metaData.getDatabaseProductName()).contains("mysql");
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private record ResolvedTable(String catalog, String schema, String tableName) {
    }
}