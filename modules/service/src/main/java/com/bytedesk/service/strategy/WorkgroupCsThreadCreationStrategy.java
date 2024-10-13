/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-12 15:51:51
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
import com.bytedesk.ai.robot.Robot;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadService;
import com.bytedesk.core.thread.ThreadStatusEnum;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.route.IRouteService;
import com.bytedesk.service.utils.ConvertServiceUtils;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.workgroup.Workgroup;
import com.bytedesk.service.workgroup.WorkgroupService;
import com.bytedesk.core.thread.Thread;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 技能组会话
@Slf4j
@Component("workgroupCsThreadStrategy")
@AllArgsConstructor
public class WorkgroupCsThreadCreationStrategy implements CsThreadCreationStrategy {

    private final WorkgroupService workgroupService;

    private final ThreadService threadService;

    private final IRouteService routeService;

    private final UidUtils uidUtils;

    @Override
    public MessageProtobuf createCsThread(VisitorRequest visitorRequest) {
        return createWorkgroupCsThread(visitorRequest);
    }

    public MessageProtobuf createWorkgroupCsThread(VisitorRequest visitorRequest) {
        //
        String workgroupUid = visitorRequest.getSid();
        String topic = TopicUtils.formatOrgWorkgroupThreadTopic(workgroupUid, visitorRequest.getUid());
        // 是否已经存在进行中会话
        Thread thread = getProcessingThread(topic);
        if (thread != null && !visitorRequest.getForceAgent()) {
            log.info("Already have a processing thread " + JSON.toJSONString(thread));
            return getWorkgroupProcessingMessage(visitorRequest, thread);
        }
        //
        Workgroup workgroup = workgroupService.findByUid(workgroupUid)
                .orElseThrow(() -> new RuntimeException("Workgroup uid " + workgroupUid + " not found"));
        //
        thread = getWorkgroupThread(visitorRequest, workgroup, topic);

        // 未强制转人工的情况下，判断是否转机器人
        if (!visitorRequest.getForceAgent()) {
            Boolean isOffline = !workgroup.isConnected();
            Boolean transferToRobot = workgroup.getServiceSettings().shouldTransferToRobot(isOffline);
            if (transferToRobot) {
                // 转机器人
                // 将robot设置为agent
                Robot robot = workgroup.getServiceSettings().getRobot();

                return routeService.routeRobot(visitorRequest, thread, robot);
            }
        }
        // 
        return routeService.routeWorkgroup(visitorRequest, thread, workgroup);
    }

    // 是否存在未关闭的会话
    private Thread getProcessingThread(String topic) {
        // 拉取未关闭会话
        Optional<Thread> threadOptional = threadService.findByTopicNotClosed(topic);
        if (threadOptional.isPresent()) {
            return threadOptional.get();
        }
        return null;
    }

    // FIXME: 如果访客重复打开、关闭页面，会重复发送continue消息
    private MessageProtobuf getWorkgroupProcessingMessage(VisitorRequest visitorRequest, Thread thread) {
        if (thread == null) {
            throw new RuntimeException("Thread cannot be null");
        }
        //
        thread.setUnreadCount(1);
        thread.setStatus(ThreadStatusEnum.CONTINUE.name());
        threadService.save(thread);
        //
        UserProtobuf user = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        log.info("getWorkgroupProcessingMessage user: {}, agent {}", user.toString(), thread.getAgent());
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadMessage(user, thread, true);
        // 广播消息，由消息通道统一处理
        MessageUtils.notifyUser(messageProtobuf);

        return messageProtobuf;
    }

    private Thread getWorkgroupThread(VisitorRequest visitorRequest, Workgroup workgroup, String topic) {
        //
        Thread thread = Thread.builder().build();
        Optional<Thread> threadOptional = threadService.findByTopic(topic);
        if (threadOptional.isPresent()) {
            thread = threadOptional.get();
        } else {
            thread.setTopic(topic);
            thread.setType(ThreadTypeEnum.WORKGROUP.name());
            thread.setClient(ClientEnum.fromValue(visitorRequest.getClient()).name());
            thread.setOrgUid(workgroup.getOrgUid());
        }
        // 强制生成新会话uid，代表新会话。便于会话跟踪计数统计
        thread.setUid(uidUtils.getUid());
        //
        UserProtobuf visitor = ConvertServiceUtils.convertToUserProtobuf(visitorRequest);
        thread.setUser(JSON.toJSONString(visitor));
        thread.setExtra(JSON.toJSONString(
                ConvertServiceUtils.convertToServiceSettingsResponseVisitor(workgroup.getServiceSettings())));
        //
        return thread;
    }

    

}
