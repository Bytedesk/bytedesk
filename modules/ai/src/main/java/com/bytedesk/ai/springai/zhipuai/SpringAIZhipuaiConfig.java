/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:53:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-03 09:48:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.zhipuai;

import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore.MetadataField;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingOptions;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.ai.zhipuai.api.ZhiPuAiImageApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.core.redis.JedisProperties;
import com.bytedesk.kbase.config.KbaseConst;
// import com.zhipu.oapi.ClientV4;

import lombok.Data;
import redis.clients.jedis.JedisPooled;
/**
 * https://open.bigmodel.cn/dev/api#sdk_install
 * https://github.com/MetaGLM/zhipuai-sdk-java-v4
 * 
 * https://docs.spring.io/spring-ai/reference/api/chat/zhipuai-chat.html
 * https://docs.spring.io/spring-ai/reference/api/embeddings/zhipuai-embeddings.html
 */
@Data
@Configuration
@ConditionalOnProperty(name = "spring.ai.zhipuai.chat.enabled", havingValue = "true")
public class SpringAIZhipuaiConfig {

    @Value("${spring.ai.zhipuai.api-key:}")
    String zhipuaiApiKey;

    @Value("${spring.ai.zhipuai.chat.options.model:glm-4-flash}")
    String zhipuaiApiModel;

    @Value("${spring.ai.zhipuai.chat.options.temperature:0.7}")
    double zhipuaiApiTemperature;

    @Value("${spring.ai.zhipuai.embedding.options.model:embedding-2}")
    String zhipuaiEmbeddingModel;

    @Autowired
    private JedisProperties jedisProperties;

    @Bean("bytedeskZhipuaiApi")
    ZhiPuAiApi bytedeskZhipuaiApi() {
        return new ZhiPuAiApi(zhipuaiApiKey);
    }

    @Bean("bytedeskZhipuaiChatOptions")
    ZhiPuAiChatOptions bytedeskZhipuaiChatOptions() {
        return ZhiPuAiChatOptions.builder()
                .model(zhipuaiApiModel)
                .temperature(zhipuaiApiTemperature)
                .build();
    }

    // https://docs.spring.io/spring-ai/reference/api/embeddings/zhipuai-embeddings.html
    // https://open.bigmodel.cn/overview
    @Bean("bytedeskZhipuaiEmbeddingOptions")
    ZhiPuAiEmbeddingOptions bytedeskZhipuaiEmbeddingOptions() {
        return ZhiPuAiEmbeddingOptions.builder()
                .model(zhipuaiEmbeddingModel)
                .build();
    }

    // https://open.bigmodel.cn/dev/api/normal-model/glm-4
    @Bean("bytedeskZhipuaiChatModel")
    ZhiPuAiChatModel bytedeskZhipuaiChatModel() {
        return new ZhiPuAiChatModel(bytedeskZhipuaiApi(), bytedeskZhipuaiChatOptions());
    }

    @Bean("bytedeskZhipuaiEmbeddingModel")
    ZhiPuAiEmbeddingModel bytedeskZhipuaiEmbeddingModel() {
        return new ZhiPuAiEmbeddingModel(bytedeskZhipuaiApi(), MetadataMode.EMBED, bytedeskZhipuaiEmbeddingOptions());
    }

    @Bean("bytedeskZhipuaiChatClientBuilder")
    ChatClient.Builder bytedeskZhipuaiChatClientBuilder() {
        return ChatClient.builder(bytedeskZhipuaiChatModel());
    }

    @Bean("bytedeskZhipuaiChatClient")
    ChatClient bytedeskZhipuaiChatClient() {
        return bytedeskZhipuaiChatClientBuilder()
                .defaultOptions(bytedeskZhipuaiChatOptions())
                .build();
    }

    @Bean("bytedeskZhipuaiImageApi")
    ZhiPuAiImageApi bytedeskZhipuaiImageApi() {
        return new ZhiPuAiImageApi(zhipuaiApiKey);
    }

    @Bean("bytedeskZhipuaiImageModel")
    ZhiPuAiImageModel bytedeskZhipuaiImageModel() {
        return new ZhiPuAiImageModel(bytedeskZhipuaiImageApi());
    }

    // @Bean("bytedeskZhipuaiClient")
    // @ConditionalOnProperty(name = "spring.ai.zhipuai.chat.enabled", havingValue = "true")
    // ClientV4 bytedeskZhipuaiClient() {
    //     return new ClientV4.Builder(zhipuaiApiKey).build();
    // }

    @Bean("bytedeskZhipuaiRedisVectorStore")
    @ConditionalOnProperty(name = { "spring.ai.zhipuai.embedding.enabled", "spring.ai.vectorstore.redis.initialize-schema" }, havingValue = "true")
    public RedisVectorStore bytedeskZhipuaiRedisVectorStore(EmbeddingModel bytedeskZhipuaiEmbeddingModel, RedisVectorStoreProperties properties) {

            var kbUid = MetadataField.text(KbaseConst.KBASE_KB_UID);
            var fileUid = MetadataField.text(KbaseConst.KBASE_FILE_UID);
            //
            var jedisPooled = new JedisPooled(jedisProperties.getHost(),
                            jedisProperties.getPort(),
                            null,
                            jedisProperties.getPassword());
            
            // 初始化向量库, 创建索引
            return RedisVectorStore.builder(jedisPooled, bytedeskZhipuaiEmbeddingModel)
                    .indexName(properties.getIndex())
                    .prefix(properties.getPrefix())
                    .metadataFields(kbUid, fileUid)
                    .initializeSchema(true)
                    .build();
    }

}
