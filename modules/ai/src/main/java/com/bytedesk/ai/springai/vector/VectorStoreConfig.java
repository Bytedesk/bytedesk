/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-24 14:37:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-24 14:39:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.vector;

import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {

    // @Bean("redisVectorStore")
    // @ConditionalOnProperty(name = "spring.ai.vectorstore.redis.enabled", havingValue = "true")
    // public VectorStore redisVectorStore(RedisConnectionFactory redisConnectionFactory) {
    //     return new RedisVectorStore(redisConnectionFactory);
    // }

    // @Bean("weaviateVectorStore")
    // @ConditionalOnProperty(name = "spring.ai.vectorstore.weaviate.enabled", havingValue = "true")
    // public VectorStore weaviateVectorStore(WeaviateClient weaviateClient) {
    //     return new WeaviateVectorStore(weaviateClient);
    // }
} 