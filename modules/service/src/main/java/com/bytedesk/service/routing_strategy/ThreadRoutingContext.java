/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-29 22:07:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-03 16:08:06
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.service.visitor.VisitorRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * 创建新策略：
 * 1. 创建一个策略接口，定义创建线程的方法
 * 2. 按照此格式命名策略：ThreadTypeEnum.**.name() + CsThreadStrategy
 */
@Slf4j
@Component
public class ThreadRoutingContext {
    
    // 策略模式，将每个策略都封装起来
    private final Map<ThreadTypeEnum, ThreadRoutingStrategy> strategyMap;

    @Autowired
    public ThreadRoutingContext(List<ThreadRoutingStrategy> strategies) {
        strategyMap = new EnumMap<>(ThreadTypeEnum.class);
        for (ThreadRoutingStrategy strategy : strategies) {
            // 假设每个策略类都有一个与之对应的Bean名称，可以通过Bean名称和枚举值进行匹配。
            // 在实际应用中，可能需要其他机制来确保策略与枚举的正确匹配。
            // log.info("strategy: {}", strategy.getClass());
            String beanName = strategy.getClass().getAnnotation(Component.class).value();
            ThreadTypeEnum type = ThreadTypeEnum.valueOf(beanName.toUpperCase().replace("THREADSTRATEGY", ""));
            strategyMap.put(type, strategy);
        }
    }

    public MessageProtobuf createCsThread(VisitorRequest visitorRequest) {
        ThreadTypeEnum type = visitorRequest.formatType();
        ThreadRoutingStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new RuntimeException("Thread type " + type.name() + " not supported");
        }
        return strategy.createThread(visitorRequest);
    }
}