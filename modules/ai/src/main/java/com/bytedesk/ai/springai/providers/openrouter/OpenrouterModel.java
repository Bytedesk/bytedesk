/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-02 22:47:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-02 22:47:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.openrouter;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OpenRouter 模型信息实体类
 * 基于 OpenRouter API (/models) 返回的真实数据结构创建
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenrouterModel {

    /**
     * 模型ID (例如: "openai/gpt-4", "anthropic/claude-3-sonnet")
     */
    private String id;

    /**
     * 模型的规范化标识符
     */
    private String canonicalSlug;

    /**
     * Hugging Face 模型ID（如果可用）
     */
    private String huggingFaceId;

    /**
     * 模型显示名称
     */
    private String name;

    /**
     * 模型创建时间戳
     */
    private Long created;

    /**
     * 模型描述信息
     */
    private String description;

    /**
     * 上下文长度（最大token数）
     */
    private Integer contextLength;

    /**
     * 模型架构信息
     */
    private Architecture architecture;

    /**
     * 定价信息
     */
    private Pricing pricing;

    /**
     * 顶级提供商信息
     */
    private TopProvider topProvider;

    /**
     * 每请求限制
     */
    private Object perRequestLimits;

    /**
     * 支持的参数列表
     */
    private List<String> supportedParameters;

    /**
     * 模型架构信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Architecture {
        /**
         * 模态类型 (例如: "text->text", "text+image->text")
         */
        private String modality;

        /**
         * 输入模态列表 (例如: ["text"], ["text", "image"])
         */
        private List<String> inputModalities;

        /**
         * 输出模态列表 (例如: ["text"])
         */
        private List<String> outputModalities;

        /**
         * 分词器类型 (例如: "GPT", "Claude", "Llama3")
         */
        private String tokenizer;

        /**
         * 指令类型 (例如: "chatml", "llama3", null)
         */
        private String instructType;
    }

    /**
     * 定价信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pricing {
        /**
         * 提示token价格（每token美元）
         */
        private String prompt;

        /**
         * 完成token价格（每token美元）
         */
        private String completion;

        /**
         * 请求价格
         */
        private String request;

        /**
         * 图像处理价格
         */
        private String image;

        /**
         * 网络搜索价格
         */
        private String webSearch;

        /**
         * 内部推理价格
         */
        private String internalReasoning;

        /**
         * 输入缓存读取价格
         */
        private String inputCacheRead;

        /**
         * 输入缓存写入价格
         */
        private String inputCacheWrite;
    }

    /**
     * 顶级提供商信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProvider {
        /**
         * 提供商支持的上下文长度
         */
        private Integer contextLength;

        /**
         * 最大完成token数
         */
        private Integer maxCompletionTokens;

        /**
         * 是否启用内容审核
         */
        private Boolean isModerated;
    }

    /**
     * 从Map转换为OpenrouterModel对象
     */
    @SuppressWarnings("unchecked")
    public static OpenrouterModel fromMap(Map<String, Object> map) {
        OpenrouterModel model = new OpenrouterModel();
        
        model.setId((String) map.get("id"));
        model.setCanonicalSlug((String) map.get("canonical_slug"));
        model.setHuggingFaceId((String) map.get("hugging_face_id"));
        model.setName((String) map.get("name"));
        model.setCreated(((Number) map.get("created")).longValue());
        model.setDescription((String) map.get("description"));
        model.setContextLength((Integer) map.get("context_length"));
        model.setPerRequestLimits(map.get("per_request_limits"));
        model.setSupportedParameters((List<String>) map.get("supported_parameters"));

        // 转换架构信息
        Map<String, Object> archMap = (Map<String, Object>) map.get("architecture");
        if (archMap != null) {
            Architecture arch = new Architecture();
            arch.setModality((String) archMap.get("modality"));
            arch.setInputModalities((List<String>) archMap.get("input_modalities"));
            arch.setOutputModalities((List<String>) archMap.get("output_modalities"));
            arch.setTokenizer((String) archMap.get("tokenizer"));
            arch.setInstructType((String) archMap.get("instruct_type"));
            model.setArchitecture(arch);
        }

        // 转换定价信息
        Map<String, Object> pricingMap = (Map<String, Object>) map.get("pricing");
        if (pricingMap != null) {
            Pricing pricing = new Pricing();
            pricing.setPrompt((String) pricingMap.get("prompt"));
            pricing.setCompletion((String) pricingMap.get("completion"));
            pricing.setRequest((String) pricingMap.get("request"));
            pricing.setImage((String) pricingMap.get("image"));
            pricing.setWebSearch((String) pricingMap.get("web_search"));
            pricing.setInternalReasoning((String) pricingMap.get("internal_reasoning"));
            pricing.setInputCacheRead((String) pricingMap.get("input_cache_read"));
            pricing.setInputCacheWrite((String) pricingMap.get("input_cache_write"));
            model.setPricing(pricing);
        }

        // 转换顶级提供商信息
        Map<String, Object> providerMap = (Map<String, Object>) map.get("top_provider");
        if (providerMap != null) {
            TopProvider provider = new TopProvider();
            provider.setContextLength((Integer) providerMap.get("context_length"));
            provider.setMaxCompletionTokens((Integer) providerMap.get("max_completion_tokens"));
            provider.setIsModerated((Boolean) providerMap.get("is_moderated"));
            model.setTopProvider(provider);
        }

        return model;
    }
}
