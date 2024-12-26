/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-26 14:49:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.strategy;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.routing.RouteService;
import com.bytedesk.service.utils.ThreadMessageUtil;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor_thread.VisitorThreadService;
import com.bytedesk.service.workgroup.WorkgroupEntity;

import com.bytedesk.service.workgroup.WorkgroupRestService;

import jakarta.annotation.Nonnull;

import com.bytedesk.core.thread.ThreadEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jack Ning 270580156@qq.com
 */
@Slf4j
@Component("workgroupCsThreadStrategy")
@AllArgsConstructor
public class WorkgroupCsThreadCreationStrategy implements CsThreadCreationStrategy {

    private final WorkgroupRestService workgroupService;

    private final ThreadRestService threadService;

    private final VisitorThreadService visitorThreadService;

    // private final IRouteService routeService;
    private final RouteService routeService;

    private final IMessageSendService messageSendService;

    @Override
    public MessageProtobuf createCsThread(VisitorRequest visitorRequest) {
        return createWorkgroupCsThread(visitorRequest);
    }

    public MessageProtobuf createWorkgroupCsThread(VisitorRequest visitorRequest) {
        //
        String workgroupUid = visitorRequest.getSid();
        String topic = TopicUtils.formatOrgWorkgroupThreadTopic(workgroupUid, visitorRequest.getUid());
        // 是否已经存在会话
        Optional<ThreadEntity> threadOptional = threadService.findFirstByTopic(topic);
        if (threadOptional.isPresent() && !visitorRequest.getForceAgent()) {
            ThreadEntity thread = threadOptional.get();
            if (thread.isStarted()) {
                log.info("Already have a processing thread {}", topic);
                return getWorkgroupProcessingMessage(visitorRequest, threadOptional.get());
            }
        }
        //
        WorkgroupEntity workgroup = workgroupService.findByUid(workgroupUid)
                .orElseThrow(() -> new RuntimeException("Workgroup uid " + workgroupUid + " not found"));
        // 
        ThreadEntity thread = null;
        if (threadOptional.isPresent()) {
            // 存在会话，且已经关闭，重新初始化
            thread = threadOptional.get().reInitWorkgroup();
        } else {
            // 不存在会话，创建会话
            thread = visitorThreadService.createWorkgroupThread(visitorRequest, workgroup, topic);
        }
        thread = visitorThreadService.reInitWorkgroupThreadExtra(visitorRequest, thread, workgroup);
        // thread.setSerialNumber(counter.getCurrentNumber());
        // 未强制转人工的情况下，判断是否转机器人
        if (!visitorRequest.getForceAgent()) {
            Boolean isOffline = !workgroup.isConnected();
            Boolean isInServiceTime = workgroup.getLeaveMsgSettings().isInServiceTime();
            Boolean transferToRobot = workgroup.getRobotSettings().shouldTransferToRobot(isOffline, isInServiceTime);
            if (transferToRobot) {
                // 转机器人
                RobotEntity robot = workgroup.getRobotSettings().getRobot();
                // 
                return routeService.routeToRobot(visitorRequest, thread, robot);
            }
        }
        // 
        return routeService.routeToWorkgroup(visitorRequest, thread, workgroup);
    }

    // Q-原样返回会话
    private MessageProtobuf getWorkgroupProcessingMessage(VisitorRequest visitorRequest, @Nonnull ThreadEntity thread) {
        //
        UserProtobuf user = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        log.info("getWorkgroupProcessingMessage user: {}, agent {}", user.toString(), thread.getAgent());
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadContinueMessage(user, thread);
        // 微信公众号等渠道不能重复推送”继续会话“消息
        if (!visitorRequest.isWeChat()) {
            // 广播消息，由消息通道统一处理
            messageSendService.sendProtobufMessage(messageProtobuf);
        }
        // 
        return messageProtobuf;
    }


}
