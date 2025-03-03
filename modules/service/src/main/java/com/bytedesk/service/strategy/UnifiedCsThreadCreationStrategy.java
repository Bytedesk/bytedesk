/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-03 16:05:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.strategy;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.routing.RouteService;
import com.bytedesk.service.unified.UnifiedEntity;
import com.bytedesk.service.unified.UnifiedRestService;
import com.bytedesk.service.utils.ThreadMessageUtil;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor_thread.VisitorThreadService;

import jakarta.annotation.Nonnull;

import com.bytedesk.core.thread.ThreadEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 统一客服入口策略器
@Slf4j
@Component("unifiedCsThreadStrategy")
@AllArgsConstructor
public class UnifiedCsThreadCreationStrategy implements CsThreadCreationStrategy {

    private final UnifiedRestService unifiedService;

    private final ThreadRestService threadService;

    private final VisitorThreadService visitorThreadService;

    private final RouteService routeService;

    @Override
    public MessageProtobuf createCsThread(VisitorRequest visitorRequest) {
        return createUnifiedCsThread(visitorRequest);
    }

    // 统一客服入口，不支持转人工
    public MessageProtobuf createUnifiedCsThread(VisitorRequest visitorRequest) {
        String unifiedUid = visitorRequest.getSid();
        UnifiedEntity unified = unifiedService.findByUid(unifiedUid)
                .orElseThrow(() -> new RuntimeException("Unified uid " + unifiedUid + " not found"));
        //  
        String topic = TopicUtils.formatOrgUnifiedThreadTopic(unified.getUid(), visitorRequest.getUid());
        // 
        ThreadEntity thread = null;
        Optional<ThreadEntity> threadOptional = threadService.findFirstByTopic(topic);
        if (threadOptional.isPresent()) {
            thread = threadOptional.get();
            // 
            if (thread.isStarted()) {
                thread = visitorThreadService.reInitUnifiedThreadExtra(thread, unified); // 方便测试
                // 返回未关闭，或 非留言状态的会话
                log.info("Already have a processing unified thread {}", topic);
                return getUnifiedContinueMessage(visitorRequest, thread);
            } else {
                // 重新初始化
                thread = threadOptional.get().reInit(false);
            }
        } else {
            thread = visitorThreadService.createUnifiedThread(visitorRequest, unified, topic);
        }
        // 重新初始化会话额外信息，例如客服状态等
        thread = visitorThreadService.reInitUnifiedThreadExtra(thread, unified);

        return routeService.routeToUnified(visitorRequest, unified);
    }

    private MessageProtobuf getUnifiedContinueMessage(VisitorRequest visitorRequest, @Nonnull ThreadEntity thread) {
        //
        // UserProtobuf user = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        // log.info("getRobotContinueMessage user: {}, agent {}", user.toString(), thread.getAgent());
        // 
        return ThreadMessageUtil.getThreadUnifiedWelcomeMessage(thread);
    }

}
