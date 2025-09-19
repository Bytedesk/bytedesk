/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-29 22:07:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 08:43:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.routing_strategy;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.service.visitor.VisitorRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * 线程路由上下文 - 策略模式实现
 * 
 * <p>功能：
 * 1. 管理不同类型线程的路由策略
 * 2. 根据线程类型自动选择合适的策略处理线程创建
 * 3. 支持动态策略注册和查找
 * 
 * <p>策略命名规范：
 * - 策略Bean名称：{threadType}ThreadStrategy (如：agentThreadStrategy)
 * - 策略类名：{ThreadType}ThreadRoutingStrategy (如：AgentThreadRoutingStrategy)
 * 
 * <p>支持的线程类型：
 * - AGENT: 一对一客服线程
 * - WORKGROUP: 工作组客服线程  
 * - ROBOT: 机器人线程
 * - WORKFLOW: 工作流线程
 * - UNIFIED: 统一客服入口线程
 * 
 * @author jackning 270580156@qq.com
 * @since 1.0.0
 */
@Slf4j
@Component
public class ThreadRoutingContext {
    
    /**
     * 策略映射表 - 线程类型到策略的映射
     */
    private final Map<ThreadTypeEnum, AbstractThreadRoutingStrategy> strategyMap;
    
    /**
     * Spring应用上下文，用于动态获取策略Bean
     */
    private final ApplicationContext applicationContext;

    /**
     * 构造函数 - 初始化策略映射
     * 
     * @param strategies 所有AbstractThreadRoutingStrategy实现类的列表
     * @param applicationContext Spring应用上下文
     */
    public ThreadRoutingContext(List<AbstractThreadRoutingStrategy> strategies, ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.strategyMap = new EnumMap<>(ThreadTypeEnum.class);
        initializeStrategies(strategies);
        logRegisteredStrategies();
    }

    /**
     * 创建客服线程的主要入口方法
     * 
     * @param visitorRequest 访客请求信息
     * @return 消息协议对象
     * @throws IllegalArgumentException 当线程类型不支持时
     */
    public MessageProtobuf createCsThread(VisitorRequest visitorRequest) {
        ThreadTypeEnum type = visitorRequest.formatType();
        log.debug("Creating thread for type: {}", type);
        
        AbstractThreadRoutingStrategy strategy = getStrategy(type);
        if (strategy == null) {
            log.error("No strategy found for thread type: {}", type);
            throw new IllegalArgumentException("Thread type " + type.name() + " not supported");
        }
        
        try {
            return strategy.createThread(visitorRequest);
        } catch (Exception e) {
            log.error("Error creating thread for type {}: {}", type, e.getMessage(), e);
            throw new RuntimeException("Failed to create thread for type " + type.name(), e);
        }
    }

    /**
     * 获取指定类型的策略
     * 
     * @param type 线程类型
     * @return 对应的策略实现，如果不存在则返回null
     */
    public AbstractThreadRoutingStrategy getStrategy(ThreadTypeEnum type) {
        return strategyMap.get(type);
    }
    
    /**
     * 检查是否支持指定的线程类型
     * 
     * @param type 线程类型
     * @return 如果支持返回true，否则返回false
     */
    public boolean isSupported(ThreadTypeEnum type) {
        return strategyMap.containsKey(type);
    }
    
    /**
     * 获取所有支持的线程类型
     * 
     * @return 支持的线程类型集合
     */
    public java.util.Set<ThreadTypeEnum> getSupportedTypes() {
        return strategyMap.keySet();
    }

    /**
     * 初始化策略映射
     * 
     * @param strategies 策略列表
     */
    private void initializeStrategies(List<AbstractThreadRoutingStrategy> strategies) {
        for (AbstractThreadRoutingStrategy strategy : strategies) {
            registerStrategy(strategy);
        }
        
        // 尝试通过Bean名称注册遗漏的策略
        registerMissingStrategiesByBeanName();
    }

    /**
     * 注册单个策略
     * 
     * @param strategy 策略实例
     */
    private void registerStrategy(AbstractThreadRoutingStrategy strategy) {
        try {
            String beanName = getBeanName(strategy);
            ThreadTypeEnum type = extractTypeFromBeanName(beanName);
            
            if (type != null) {
                strategyMap.put(type, strategy);
                // log.debug("Registered strategy: {} -> {}", type, strategy.getClass().getSimpleName());
            } else {
                log.warn("Cannot determine thread type for strategy: {}", strategy.getClass().getSimpleName());
            }
        } catch (Exception e) {
            log.warn("Failed to register strategy {}: {}", strategy.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * 通过Bean名称注册遗漏的策略
     */
    private void registerMissingStrategiesByBeanName() {
        for (ThreadTypeEnum type : ThreadTypeEnum.values()) {
            if (!strategyMap.containsKey(type)) {
                String beanName = generateBeanName(type);
                try {
                    AbstractThreadRoutingStrategy strategy = applicationContext.getBean(beanName, AbstractThreadRoutingStrategy.class);
                    strategyMap.put(type, strategy);
                    log.debug("Registered missing strategy by bean name: {} -> {}", type, beanName);
                } catch (Exception e) {
                    // log.debug("No strategy bean found for type {}: {}", type, beanName);
                }
            }
        }
    }

    /**
     * 获取策略的Bean名称
     * 
     * @param strategy 策略实例
     * @return Bean名称，如果获取失败返回类名的首字母小写形式
     */
    private String getBeanName(AbstractThreadRoutingStrategy strategy) {
        try {
            // 尝试从@Component注解获取value
            Component componentAnnotation = strategy.getClass().getAnnotation(Component.class);
            if (componentAnnotation != null && StringUtils.hasText(componentAnnotation.value())) {
                return componentAnnotation.value();
            }
            
            // 尝试从Spring上下文获取Bean名称
            String[] beanNames = applicationContext.getBeanNamesForType(strategy.getClass());
            if (beanNames.length > 0) {
                return beanNames[0];
            }
            
            // 默认使用类名的首字母小写形式
            String className = strategy.getClass().getSimpleName();
            return Character.toLowerCase(className.charAt(0)) + className.substring(1);
        } catch (Exception e) {
            log.warn("Error getting bean name for {}: {}", strategy.getClass().getSimpleName(), e.getMessage());
            String className = strategy.getClass().getSimpleName();
            return Character.toLowerCase(className.charAt(0)) + className.substring(1);
        }
    }

    /**
     * 从Bean名称提取线程类型
     * 
     * @param beanName Bean名称
     * @return 线程类型，如果无法提取则返回null
     */
    private ThreadTypeEnum extractTypeFromBeanName(String beanName) {
        if (!StringUtils.hasText(beanName)) {
            return null;
        }
        
        // 移除"ThreadStrategy"后缀
        String typeString = beanName;
        if (typeString.endsWith("ThreadStrategy")) {
            typeString = typeString.substring(0, typeString.length() - "ThreadStrategy".length());
        }
        
        try {
            // 将首字母转换为大写
            typeString = typeString.toUpperCase();
            return ThreadTypeEnum.valueOf(typeString);
        } catch (IllegalArgumentException e) {
            log.debug("Cannot convert bean name '{}' to ThreadTypeEnum", beanName);
            return null;
        }
    }

    /**
     * 根据线程类型生成Bean名称
     * 
     * @param type 线程类型
     * @return Bean名称
     */
    private String generateBeanName(ThreadTypeEnum type) {
        return type.name().toLowerCase() + "ThreadStrategy";
    }

    /**
     * 记录已注册的策略
     */
    private void logRegisteredStrategies() {
        if (log.isInfoEnabled()) {
            log.info("Thread routing strategies registered:");
            strategyMap.forEach((type, strategy) -> 
                log.info("  {} -> {}", type, strategy.getClass().getSimpleName()));
            
            if (strategyMap.isEmpty()) {
                log.warn("No thread routing strategies registered!");
            }
        }
    }
}