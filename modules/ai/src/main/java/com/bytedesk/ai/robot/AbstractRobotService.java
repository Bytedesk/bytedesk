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

import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.ai.springai.service.SpringAIServiceRegistry;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;

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
        log.info("processSyncRequest robotName: {}, orgUid: {}, query: {}, searchKnowledgeBase: {}", 
                robotName, orgUid, query, searchKnowledgeBase);

        Optional<RobotEntity> robotOptional = getRobotRestService().findByNameAndOrgUidAndDeletedFalse(robotName, orgUid);
        if (robotOptional.isPresent()) {
            String provider;
            if (robotOptional.get().getSettings() != null && robotOptional.get().getSettings().getLlm() != null) {
                provider = robotOptional.get().getSettings().getLlm().getTextProvider();
            } else {
                provider = robotOptional.get().getLlm().getTextProvider();
            }
            log.info("processSyncRequest provider: {}", provider);

            try {
                BaseSpringAIService service = (BaseSpringAIService) getSpringAIServiceRegistry().getServiceByProviderName(provider);
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

    /**
     * 通用的多模态直接调用 LLM 方法，支持图片URL输入
     */
    protected String processMultiModalSyncRequest(String robotName, String orgUid, String textQuery, String imageUrl, String errorMessage, boolean searchKnowledgeBase) {
        log.info("processMultiModalSyncRequest robotName: {}, orgUid: {}, textQuery: {}, imageUrl: {}, searchKnowledgeBase: {}", 
                robotName, orgUid, textQuery, imageUrl, searchKnowledgeBase);

        Optional<RobotEntity> robotOptional = getRobotRestService().findByNameAndOrgUidAndDeletedFalse(robotName, orgUid);
        if (robotOptional.isPresent()) {
            String provider;
            if (robotOptional.get().getSettings() != null && robotOptional.get().getSettings().getLlm() != null) {
                provider = robotOptional.get().getSettings().getLlm().getTextProvider();
            } else {
                provider = robotOptional.get().getLlm().getTextProvider();
            }
            log.info("processMultiModalSyncRequest provider: {}", provider);

            try {
                BaseSpringAIService service = (BaseSpringAIService) getSpringAIServiceRegistry().getServiceByProviderName(provider);
                RobotProtobuf robot = RobotProtobuf.convertFromRobotEntity(robotOptional.get());
                
                // 如果服务支持多模态处理（如ZhipuMultiModelService），使用多模态方法
                if (service instanceof com.bytedesk.ai.zhipuai.ZhipuaiMultiModelService) {
                    com.bytedesk.ai.zhipuai.ZhipuaiMultiModelService multiModelService = 
                        (com.bytedesk.ai.zhipuai.ZhipuaiMultiModelService) service;
                    
                    // 创建包含图片的MessageProtobuf
                    MessageProtobuf imageMessage = createImageMessage(imageUrl, textQuery);
                    return multiModelService.processMultiModalSyncRequest(imageMessage, robot, searchKnowledgeBase);
                } else {
                    // 回退到文本处理，附加图片URL信息
                    String combinedQuery = textQuery + " (图片链接: " + imageUrl + ")";
                    return service.processSyncRequest(combinedQuery, robot, searchKnowledgeBase);
                }

            } catch (IllegalArgumentException e) {
                log.warn("Provider {} not found for multi-modal request", provider);
                throw new RuntimeException(errorMessage);
            }
        }
        return "Robot not found";
    }

    /**
     * 创建包含图片信息的MessageProtobuf
     */
    private MessageProtobuf createImageMessage(String imageUrl, String textQuery) {
        try {
            // 创建ImageContent
            com.bytedesk.core.message.content.ImageContent imageContent = 
                com.bytedesk.core.message.content.ImageContent.builder()
                    .url(imageUrl)
                    .label(textQuery)
                    .build();
            
            return MessageProtobuf.builder()
                    .type(MessageTypeEnum.IMAGE)
                    .content(imageContent.toJson())
                    .build();
        } catch (Exception e) {
            log.error("Error creating image message", e);
            // 回退创建文本消息
            return MessageProtobuf.builder()
                    .type(MessageTypeEnum.TEXT)
                    .content(textQuery)
                    .build();
        }
    }

}