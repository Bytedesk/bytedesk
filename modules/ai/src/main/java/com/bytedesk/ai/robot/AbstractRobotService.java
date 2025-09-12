/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-12 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-12 11:30:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import java.util.Optional;

import com.bytedesk.ai.springai.service.SpringAIService;
import com.bytedesk.ai.springai.service.SpringAIServiceRegistry;
import com.bytedesk.core.constant.I18Consts;

import lombok.extern.slf4j.Slf4j;

/**
 * Robot服务的抽象基类，提供通用的LLM请求处理方法
 * 
 * @Author: jackning
 */
@Slf4j
public abstract class AbstractRobotService {

    /**
     * 获取RobotRestService实例，由子类实现
     */
    protected abstract RobotRestService getRobotRestService();
    
    /**
     * 获取SpringAIServiceRegistry实例，由子类实现
     */
    protected abstract SpringAIServiceRegistry getSpringAIServiceRegistry();

    /**
     * 通用的直接调用 LLM 方法
     * 统一了企业模块和核心模块的 processLlmRequest 实现
     */
    protected String processSyncRequest(String robotName, String orgUid, String query) {
        return processSyncRequest(robotName, orgUid, query, I18Consts.I18N_LLM_CONFIG_TIP);
    }

    /**
     * 通用的直接调用 LLM 方法，支持自定义错误消息
     */
    protected String processSyncRequest(String robotName, String orgUid, String query, String errorMessage) {
        return processSyncRequest(robotName, orgUid, query, errorMessage, true);
    }

    /**
     * 通用的直接调用 LLM 方法，支持自定义错误消息和控制是否查询知识库
     */
    protected String processSyncRequest(String robotName, String orgUid, String query, String errorMessage, boolean searchKnowledgeBase) {
        Optional<RobotEntity> robotOptional = getRobotRestService().findByNameAndOrgUidAndDeletedFalse(robotName, orgUid);
        if (robotOptional.isPresent()) {
            RobotLlm llm = robotOptional.get().getLlm();
            String provider = llm.getTextProvider();

            try {
                // Get the appropriate service from registry
                SpringAIService service = getSpringAIServiceRegistry().getServiceByProviderName(provider);

                // 使用新添加的接口方法直接调用大模型并获取结果
                RobotProtobuf robot = RobotProtobuf.convertFromRobotEntity(robotOptional.get());
                //
                return service.processSyncRequest(query, robot, searchKnowledgeBase);

            } catch (IllegalArgumentException e) {
                log.warn("Provider {} not found, falling back to OpenAI", provider);
                throw new RuntimeException(errorMessage);
            }
        }
        return "Robot not found";
    }
}