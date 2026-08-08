/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-06 11:28:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-21 15:00:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import lombok.experimental.UtilityClass;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotResponse;
import com.bytedesk.ai.robot_message.RobotMessageEntity;
import com.bytedesk.ai.robot_message.RobotMessageResponse;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.robot.RobotProtobufBasic;
import com.bytedesk.ai.robot_settings.RobotSettingsEntity;
import com.bytedesk.ai.robot_settings.tools.RobotToolConfig;
import com.bytedesk.ai.robot_settings.tools.RobotToolIntentContext;
import com.bytedesk.ai.robot_settings.tools.RobotToolIntentResolver;
import com.bytedesk.ai.robot_settings.tools.ToolChoice;
import com.bytedesk.ai.robot_settings.tools.RobotToolsSettingsEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.utils.ApplicationContextHolder;
// import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.kbase.settings_service.ServiceSettingsEntity;
import com.bytedesk.kbase.settings_service.ServiceSettingsResponseVisitor;
import com.bytedesk.kbase.quick_button.QuickButtonResponseVisitor;

@UtilityClass
public class ConvertAiUtils {

    private static ModelMapper getModelMapper() {
        return ApplicationContextHolder.getBean(ModelMapper.class);
    }

    public static RobotResponse convertToRobotResponse(RobotEntity entity) {
        return getModelMapper().map(entity, RobotResponse.class);
    }

    public static RobotProtobuf convertToRobotProtobuf(RobotEntity entity) {
        RobotProtobuf robotProtobuf = getModelMapper().map(entity, RobotProtobuf.class);
        robotProtobuf.setType(UserTypeEnum.ROBOT.name());
        applyPublishedToolSettings(entity, robotProtobuf);
        return robotProtobuf;
    }

    private static void applyPublishedToolSettings(RobotEntity entity, RobotProtobuf robotProtobuf) {
        if (entity == null || robotProtobuf == null) {
            return;
        }

        RobotSettingsEntity settings = entity.getSettings();
        if (settings == null) {
            return;
        }

        RobotToolsSettingsEntity toolsSettings = settings.getToolsSettings();
        if (toolsSettings == null || Boolean.FALSE.equals(toolsSettings.getEnabled())) {
            return;
        }

        RobotLlm llm = robotProtobuf.getLlm();
        if (llm == null) {
            llm = RobotLlm.builder().build();
            robotProtobuf.setLlm(llm);
        }

        Set<String> mergedTools = new LinkedHashSet<>();
        if (llm.getTools() != null) {
            mergedTools.addAll(llm.getTools());
        }

        for (RobotToolConfig toolConfig : safeToolConfigs(toolsSettings)) {
            mergedTools.addAll(resolveToolNames(toolConfig));
        }

        llm.setTools(new ArrayList<>(mergedTools));
        llm.setToolIntentContext(resolveToolIntentContext(entity, toolsSettings));

        if (StringUtils.hasText(toolsSettings.getToolChoice())) {
            llm.setToolChoice(normalizeToolChoice(toolsSettings.getToolChoice()));
        }
    }

    private static RobotToolIntentContext resolveToolIntentContext(RobotEntity entity,
            RobotToolsSettingsEntity toolsSettings) {
        if (!ApplicationContextHolder.isInitialized()) {
            return RobotToolIntentContext.empty();
        }

        RobotToolIntentResolver resolver = ApplicationContextHolder.getBean(RobotToolIntentResolver.class);
        return resolver.resolve(entity != null ? entity.getOrgUid() : null, toolsSettings);
    }

    private static List<RobotToolConfig> safeToolConfigs(RobotToolsSettingsEntity toolsSettings) {
        List<RobotToolConfig> toolConfigs = toolsSettings.getToolConfigs();
        return toolConfigs != null ? toolConfigs : List.of();
    }

    private static List<String> resolveToolNames(RobotToolConfig toolConfig) {
        if (toolConfig == null || Boolean.FALSE.equals(toolConfig.getEnabled())) {
            return List.of();
        }

        String bindingType = toolConfig.getBindingType();
        if (!StringUtils.hasText(bindingType)) {
            return fallbackToolName(toolConfig);
        }

        return switch (bindingType.trim().toUpperCase(Locale.ROOT)) {
            case "CLASS" -> resolveClassToolNames(toolConfig);
            case "SPRING_BEAN", "FUNCTION_BEAN" -> valueAsList(toolConfig.getBeanName(), toolConfig);
            case "MCP_TOOL" -> valueAsList(firstNonBlank(toolConfig.getMethodName(), toolConfig.getBeanName()), toolConfig);
            case "WEB_SEARCH" -> fallbackToolName(toolConfig);
            default -> fallbackToolName(toolConfig);
        };
    }

    private static List<String> resolveClassToolNames(RobotToolConfig toolConfig) {
        if (StringUtils.hasText(toolConfig.getMethodName())) {
            return List.of(toolConfig.getMethodName().trim());
        }
        if (!StringUtils.hasText(toolConfig.getClassName())) {
            return fallbackToolName(toolConfig);
        }

        try {
            Class<?> toolClass = Class.forName(toolConfig.getClassName().trim());
            List<String> toolNames = new ArrayList<>();
            for (Method method : toolClass.getMethods()) {
                if (AnnotationUtils.findAnnotation(method, Tool.class) != null) {
                    toolNames.add(method.getName());
                }
            }
            return toolNames.isEmpty() ? fallbackToolName(toolConfig) : toolNames;
        } catch (ClassNotFoundException ex) {
            return fallbackToolName(toolConfig);
        }
    }

    private static List<String> fallbackToolName(RobotToolConfig toolConfig) {
        if (StringUtils.hasText(toolConfig.getKey())) {
            return List.of(toolConfig.getKey().trim());
        }
        if (StringUtils.hasText(toolConfig.getName())) {
            return List.of(toolConfig.getName().trim());
        }
        return List.of();
    }

    private static List<String> valueAsList(String value, RobotToolConfig toolConfig) {
        if (StringUtils.hasText(value)) {
            return List.of(value.trim());
        }
        return fallbackToolName(toolConfig);
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private static String normalizeToolChoice(String toolChoice) {
        return ToolChoice.normalize(toolChoice);
    }

    public static String convertToRobotProtobufString(RobotEntity entity) {
        RobotProtobuf robotProtobuf = convertToRobotProtobuf(entity);
        return robotProtobuf.toJson();
    }

    /**
     * 转换为精简版机器人协议 JSON 字符串
     * 
     * <p>只包含基础显示信息（uid, nickname, avatar, type, orgUid），
     * 不包含 LLM 配置等大字段，适合存储到 thread.robot 字段。
     * 
     * @param entity 机器人实体
     * @return 精简版 JSON 字符串
     */
    public static String convertToRobotProtobufBasicString(RobotEntity entity) {
        RobotProtobufBasic robotProtobufBasic = RobotProtobufBasic.fromEntity(entity);
        return robotProtobufBasic.toJson();
    }

    public static String convertToUserProtobufString(RobotEntity entity) {
        UserProtobuf robotProtobuf = getModelMapper().map(entity, UserProtobuf.class);
        robotProtobuf.setType(UserTypeEnum.ROBOT.name());
        return JSON.toJSONString(robotProtobuf);
    }

    public static ServiceSettingsResponseVisitor convertToServiceSettingsResponseVisitor(
            ServiceSettingsEntity serviceSettings) {
        ServiceSettingsEntity source = serviceSettings != null ? serviceSettings : ServiceSettingsEntity.builder().build();
        ServiceSettingsResponseVisitor resp = getModelMapper().map(source, ServiceSettingsResponseVisitor.class);
        resp.setQuickButtons(QuickButtonResponseVisitor.fromEntities(source.getQuickButtons()));
        return resp;
    }

    public static RobotMessageResponse convertToRobotMessageResponse(RobotMessageEntity message) {
        RobotMessageResponse messageResponse = getModelMapper().map(message, RobotMessageResponse.class);
        // 
        if (message.getUser() != null) {
            UserProtobuf user = UserProtobuf.fromJson(message.getUser());
            if (user.getExtra() == null) {
                user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
            }
            messageResponse.setUser(user);
        }
        // robot
        if (message.getRobot()!= null) {
            UserProtobuf robot = UserProtobuf.fromJson(message.getRobot());
            if (robot.getExtra() == null) {
                robot.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
            }
            messageResponse.setRobot(robot);
        }
        // extra
        // if (message.getExtra() != null) {
        //     MessageExtra extra = MessageExtra.fromJson(message.getExtra());
        //     if (extra.getFeedback() == null) {
        //         extra.setFeedback(BytedeskConsts.EMPTY_JSON_STRING);
        //     }
        //     messageResponse.setExtra(extra);
        // }

        return messageResponse;
    }

}
