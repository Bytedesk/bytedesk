/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-10 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 08:43:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.providers.baidu.SpringAIBaiduService;
import com.bytedesk.ai.providers.custom.SpringAICustomService;
import com.bytedesk.ai.providers.dashscope.DashscopeService;
import com.bytedesk.ai.providers.deepseek.SpringAIDeepseekService;
import com.bytedesk.ai.providers.gitee.SpringAIGiteeService;
import com.bytedesk.ai.providers.minimax.SpringAIMinimaxService;
import com.bytedesk.ai.providers.moonshot.SpringAIMoonshotService;
import com.bytedesk.ai.providers.ollama.SpringAIOllamaService;
import com.bytedesk.ai.providers.siliconflow.SpringAISiliconFlowService;
import com.bytedesk.ai.providers.tencent.SpringAITencentService;
import com.bytedesk.ai.providers.volcengine.SpringAIVolcengineService;
import com.bytedesk.ai.providers.zhipuai.ZhipuaiService;
import com.bytedesk.core.llm.LlmProviderConstants;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 基础模块AI服务提供商
 * 提供基础模块中的核心AI服务，始终可用
 * 企业模块存在时会与企业版服务提供商一起工作
 */
@Slf4j
@Component("baseModuleAIServiceProvider")
@RequiredArgsConstructor
public class BaseModuleAIServiceProvider implements SpringAIServiceProvider {

    @Lazy private final Optional<SpringAIDeepseekService> springAIDeepseekService;
    // private final Optional<ZhipuaiService> zhipuaiService;
    @Lazy private final Optional<ZhipuaiService> zhipuaiService;
    @Lazy private final Optional<DashscopeService> springAIDashscopeService;
    @Lazy private final Optional<SpringAIOllamaService> springAIOllamaService;
    @Lazy private final Optional<SpringAISiliconFlowService> springAISiliconFlowService;
    @Lazy private final Optional<SpringAIGiteeService> springAIGiteeService;
    @Lazy private final Optional<SpringAITencentService> springAITencentService;
    @Lazy private final Optional<SpringAIBaiduService> springAIBaiduService;
    @Lazy private final Optional<SpringAIVolcengineService> springAIVolcengineService;
    @Lazy private final Optional<SpringAIMinimaxService> springAIMinimaxService;
    @Lazy private final Optional<SpringAIMoonshotService> springAIMoonshotService;
    
    @Lazy private final Optional<SpringAICustomService> springAICustomService;

    // 服务注册表，用于存储各种AI服务提供商的实现
    private final Map<String, SpringAIService> serviceRegistry = new HashMap<>();

    @PostConstruct
    public void registerServices() {
        springAIDashscopeService.ifPresent(service -> registerService(LlmProviderConstants.DASHSCOPE, service));
        zhipuaiService.ifPresent(service -> registerService(LlmProviderConstants.ZHIPUAI, service));
        springAIDeepseekService.ifPresent(service -> registerService(LlmProviderConstants.DEEPSEEK, service));
        springAIMinimaxService.ifPresent(service -> registerService(LlmProviderConstants.MINIMAX, service));
        springAIBaiduService.ifPresent(service -> registerService(LlmProviderConstants.BAIDU, service));
        springAIOllamaService.ifPresent(service -> registerService(LlmProviderConstants.OLLAMA, service));
        springAISiliconFlowService.ifPresent(service -> registerService(LlmProviderConstants.SILICONFLOW, service));
        springAIGiteeService.ifPresent(service -> registerService(LlmProviderConstants.GITEE, service));
        springAITencentService.ifPresent(service -> registerService(LlmProviderConstants.TENCENT, service));
        springAIVolcengineService.ifPresent(service -> registerService(LlmProviderConstants.VOLCENGINE, service));
        springAIMoonshotService.ifPresent(service -> registerService(LlmProviderConstants.MOONSHOT, service));
        
        springAICustomService.ifPresent(service -> registerService(LlmProviderConstants.CUSTOM, service));
        
        log.info("BaseModuleAIServiceProvider registered {} AI services", serviceRegistry.size());
    }

    private void registerService(String providerName, SpringAIService service) {
        if (service != null) {
            serviceRegistry.put(providerName, service);
            // log.debug("已注册基础模块AI服务提供商: {}", providerName);
        }
    }

    @Override
    public Set<String> getSupportedProviders() {
        return serviceRegistry.keySet();
    }

    @Override
    public SpringAIService getService(String providerName) {
        return serviceRegistry.get(providerName);
    }

    @Override
    public int getPriority() {
        return 200; // 基础模块优先级较低
    }
}
