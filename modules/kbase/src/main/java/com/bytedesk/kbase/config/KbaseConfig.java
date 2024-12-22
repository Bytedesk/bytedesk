/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-27 20:51:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-22 18:03:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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

import com.bytedesk.core.redis.JedisProperties;

import redis.clients.jedis.JedisPooled;

@Configuration
public class KbaseConfig {

        @Autowired
        private JedisProperties jedisProperties;

        // https://docs.spring.io/spring-ai/reference/api/vectordbs/redis.html
        // 初始化向量库, 创建索引
        @Bean
        @ConditionalOnProperty(name = "spring.ai.vectorstore.redis.initialize-schema", havingValue = "true")
        public RedisVectorStore vectorStore(EmbeddingModel embeddingModel, RedisVectorStoreProperties properties) {

                var kbUid = MetadataField.text(KbaseConst.KBASE_KB_UID);
                var fileUid = MetadataField.text(KbaseConst.KBASE_FILE_UID);
                //
                var jedisPooled = new JedisPooled(jedisProperties.getHost(),
                                jedisProperties.getPort(),
                                null,
                                jedisProperties.getPassword());
                // 初始化向量库, 创建索引
                return RedisVectorStore.builder(jedisPooled, embeddingModel)
                        .indexName(properties.getIndex())
                        .prefix(properties.getPrefix())
                        .metadataFields(kbUid, fileUid)
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
