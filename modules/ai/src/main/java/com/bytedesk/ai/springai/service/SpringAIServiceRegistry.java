/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-19 13:05:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-03 16:09:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.bytedesk.ai.springai.providers.baidu.SpringAIBaiduService;
import com.bytedesk.ai.springai.providers.custom.SpringAICustomService;
import com.bytedesk.ai.springai.providers.dashscope.SpringAIDashscopeService;
import com.bytedesk.ai.springai.providers.deepseek.SpringAIDeepseekService;
import com.bytedesk.ai.springai.providers.gitee.SpringAIGiteeService;
import com.bytedesk.ai.springai.providers.minimax.SpringAIMinimaxService;
import com.bytedesk.ai.springai.providers.ollama.SpringAIOllamaService;
import com.bytedesk.ai.springai.providers.siliconflow.SpringAISiliconFlowService;
import com.bytedesk.ai.springai.providers.tencent.SpringAITencentService;
import com.bytedesk.ai.springai.providers.volcengine.SpringAIVolcengineService;
import com.bytedesk.ai.zhipuai.ZhipuaiService;
import com.bytedesk.core.constant.LlmConsts;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SpringAI服务注册表
 * 用于管理和获取不同AI提供商的服务实例
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringAIServiceRegistry {

    private final Optional<SpringAIDeepseekService> springAIDeepseekService;
    // private final Optional<SpringAIZhipuaiService> springAIZhipuaiService;
    private final Optional<ZhipuaiService> zhipuaiService;
    private final Optional<SpringAIDashscopeService> springAIDashscopeService;
    private final Optional<SpringAIOllamaService> springAIOllamaService;
    private final Optional<SpringAISiliconFlowService> springAISiliconFlowService;
    private final Optional<SpringAIGiteeService> springAIGiteeService;
    private final Optional<SpringAITencentService> springAITencentService;
    private final Optional<SpringAIBaiduService> springAIBaiduService;
    private final Optional<SpringAIVolcengineService> springAIVolcengineService;
    private final Optional<SpringAIMinimaxService> springAIMinimaxService;
    private final Optional<SpringAICustomService> springAICustomService;

    // 服务注册表，用于存储各种AI服务提供商的实现
    private final Map<String, SpringAIService> serviceRegistry = new HashMap<>();

    @PostConstruct
    public void init() {
        // 在初始化时注册所有可用的服务
        registerService(LlmConsts.DEEPSEEK, springAIDeepseekService.orElse(null));
        // registerService(LlmConsts.ZHIPUAI, springAIZhipuaiService.orElse(null));
        registerService(LlmConsts.ZHIPUAI, zhipuaiService.orElse(null));
        registerService(LlmConsts.DASHSCOPE, springAIDashscopeService.orElse(null));
        registerService(LlmConsts.OLLAMA, springAIOllamaService.orElse(null));
        registerService(LlmConsts.SILICONFLOW, springAISiliconFlowService.orElse(null));
        registerService(LlmConsts.GITEE, springAIGiteeService.orElse(null));
        registerService(LlmConsts.TENCENT, springAITencentService.orElse(null));
        registerService(LlmConsts.BAIDU, springAIBaiduService.orElse(null));
        registerService(LlmConsts.VOLCENGINE, springAIVolcengineService.orElse(null));
        registerService(LlmConsts.MINIMAX, springAIMinimaxService.orElse(null));
        registerService(LlmConsts.CUSTOM, springAICustomService.orElse(null));
        
        log.info("SpringAI服务注册表初始化完成，注册了{}个服务", serviceRegistry.size());
    }

    /**
     * 注册一个AI服务提供商
     * 
     * @param providerName 提供商名称
     * @param service 服务实现
     */
    private void registerService(String providerName, SpringAIService service) {
        if (service != null) {
            serviceRegistry.put(providerName, service);
            // log.info("已注册AI服务提供商: {}", providerName);
        }
    }

    /**
     * 获取指定提供商的服务实现
     * 
     * @param providerName 提供商名称
     * @return SpringAI服务实现
     * @throws IllegalArgumentException 如果提供商不存在
     */
    public SpringAIService getServiceByProviderName(String providerName) {
        SpringAIService service = serviceRegistry.get(providerName);
        if (service == null) {
            throw new IllegalArgumentException("未找到AI服务提供商: " + providerName);
        }
        return service;
    }

    /**
     * 检查指定提供商是否可用
     * 
     * @param providerName 提供商名称
     * @return 如果提供商可用返回true，否则返回false
     */
    public Boolean isServiceAvailable(String providerName) {
        return serviceRegistry.containsKey(providerName);
    }
}
