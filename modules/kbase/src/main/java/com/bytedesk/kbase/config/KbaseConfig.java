/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-27 20:51:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-19 10:15:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.config;

import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreProperties;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore.MetadataField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.bytedesk.core.redis.JedisProperties;

import redis.clients.jedis.JedisPooled;

@Configuration
public class KbaseConfig {

        @Autowired
        private JedisProperties jedisProperties;

        // https://docs.spring.io/spring-ai/reference/api/vectordbs/redis.html
        // https://redis.io/docs/interact/search-and-query/
        // 初始化向量库, 创建索引
        @Primary
        @Bean("ollamaRedisVectorStore")
        @ConditionalOnProperty(name = { "spring.ai.ollama.embedding.enabled", "spring.ai.vectorstore.redis.initialize-schema" }, havingValue = "true")
        public RedisVectorStore ollamaRedisVectorStore(EmbeddingModel ollamaEmbeddingModel, RedisVectorStoreProperties properties) {

                var kbUid = MetadataField.text(KbaseConst.KBASE_KB_UID);
                var fileUid = MetadataField.text(KbaseConst.KBASE_FILE_UID);
                //
                var jedisPooled = new JedisPooled(jedisProperties.getHost(),
                                jedisProperties.getPort(),
                                null,
                                jedisProperties.getPassword());
                // 初始化向量库, 创建索引
                return RedisVectorStore.builder(jedisPooled, ollamaEmbeddingModel)
                        .indexName(properties.getIndex())
                        .prefix(properties.getPrefix())
                        .metadataFields(kbUid, fileUid)
                        .initializeSchema(true)
                        .build();
        }

        @Bean("zhipuaiRedisVectorStore")
        @ConditionalOnProperty(name = { "spring.ai.zhipuai.embedding.enabled", "spring.ai.vectorstore.redis.initialize-schema" }, havingValue = "true")
        public RedisVectorStore zhipuaiRedisVectorStore(EmbeddingModel zhipuaiEmbeddingModel, RedisVectorStoreProperties properties) {

                var kbUid = MetadataField.text(KbaseConst.KBASE_KB_UID);
                var fileUid = MetadataField.text(KbaseConst.KBASE_FILE_UID);
                //
                var jedisPooled = new JedisPooled(jedisProperties.getHost(),
                                jedisProperties.getPort(),
                                null,
                                jedisProperties.getPassword());
                // 初始化向量库, 创建索引
                return RedisVectorStore.builder(jedisPooled, zhipuaiEmbeddingModel)
                        .indexName(properties.getIndex())
                        .prefix(properties.getPrefix())
                        .metadataFields(kbUid, fileUid)
                        .initializeSchema(true)
                        .build();
        }

        // 使用pgvector存储向量, 报错
        // @Bean
        // public VectorStore vectorStore(EmbeddingModel embeddingModel, JdbcTemplate
        // jdbcTemplate) {
        // // FIXME: ERROR: column cannot have more than 2000 dimensions for hnsw index
        // return new PgVectorStore(jdbcTemplate, embeddingModel);
        // }

}
