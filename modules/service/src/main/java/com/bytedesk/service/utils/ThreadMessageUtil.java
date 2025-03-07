/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-29 22:22:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-07 10:41:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.utils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.settings.ServiceSettings;
import com.bytedesk.kbase.settings.ServiceTrigger;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.workgroup.WorkgroupEntity;

import java.time.LocalDateTime;

// 可以根据需要选择是否使用 @Component 注解
// 如果该方法不需要被Spring容器管理，则不需要此注解
public class ThreadMessageUtil {

    public static MessageProtobuf getThreadUnifiedWelcomeMessage(ThreadEntity thread) {
        MessageEntity message = MessageEntity.builder()
                .content(thread.getContent())
                .type(MessageTypeEnum.WELCOME.name())
                .status(MessageStatusEnum.READ.name())
                .client(ClientEnum.SYSTEM.name())
                .user(thread.getAgent())
                .build();
        message.setUid(UidUtils.getInstance().getUid());
        message.setOrgUid(thread.getOrgUid());
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        message.setThreadTopic(thread.getTopic());
        //
        MessageExtra extra = MessageUtils.getMessageExtra(thread.getOrgUid());
        message.setExtra(JSON.toJSONString(extra));
        //
        return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    }

    public static MessageProtobuf getThreadRobotWelcomeMessage(RobotEntity robot, ThreadEntity thread) {
        // ... 方法的实现保持不变 ...
        MessageEntity message = MessageEntity.builder()
                .content(robot.getServiceSettings().getWelcomeTip())
                .type(MessageTypeEnum.WELCOME.name())
                .status(MessageStatusEnum.READ.name())
                .client(ClientEnum.SYSTEM.name())
                .user(thread.getAgent())
                .build();
        message.setUid(UidUtils.getInstance().getUid());
        message.setOrgUid(thread.getOrgUid());
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        message.setThreadTopic(thread.getTopic());
        //
        MessageExtra extra = MessageUtils.getMessageExtra(thread.getOrgUid());
        message.setExtra(JSON.toJSONString(extra));
        //
        return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    }

    // 将此方法设为静态，以便在没有实例化类的情况下调用
    public static MessageProtobuf getThreadWelcomeMessage(AgentEntity agent, ThreadEntity thread) {
        UserProtobuf user = ServiceConvertUtils.convertToUserProtobuf(agent);
        // ... 方法的实现保持不变 ...
        MessageEntity message = MessageEntity.builder()
                .content(agent.getServiceSettings().getWelcomeTip())
                .type(MessageTypeEnum.WELCOME.name())
                .status(MessageStatusEnum.READ.name())
                .client(ClientEnum.SYSTEM.name())
                .user(JSON.toJSONString(user))
                .build();
        message.setUid(UidUtils.getInstance().getUid());
        message.setOrgUid(thread.getOrgUid());
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        message.setThreadTopic(thread.getTopic());
        //
        MessageExtra extra = MessageUtils.getMessageExtra(thread.getOrgUid());
        message.setExtra(JSON.toJSONString(extra));
        //
        return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    }

    public static MessageProtobuf getAgentThreadQueueMessage(AgentEntity agent, ThreadEntity thread) {
        UserProtobuf user = ServiceConvertUtils.convertToUserProtobuf(agent);
        // ... 方法的实现保持不变 ...
        MessageEntity message = MessageEntity.builder()
                .content(thread.getContent())
                .type(MessageTypeEnum.QUEUE.name())
                .status(MessageStatusEnum.READ.name())
                .client(ClientEnum.SYSTEM.name())
                .user(JSON.toJSONString(user))
                .build();
        message.setUid(UidUtils.getInstance().getUid());
        message.setOrgUid(thread.getOrgUid());
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        message.setThreadTopic(thread.getTopic());
        //
        MessageExtra extra = MessageUtils.getMessageExtra(thread.getOrgUid());
        message.setExtra(JSON.toJSONString(extra));
        //
        return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    }

    public static MessageProtobuf getThreadQueuingMessage(UserProtobuf user, ThreadEntity thread) {
        // ... 方法的实现保持不变 ...
        MessageEntity message = MessageEntity.builder()
                .content(thread.getContent())
                .type(MessageTypeEnum.QUEUE.name())
                .status(MessageStatusEnum.READ.name())
                .client(ClientEnum.SYSTEM.name())
                .user(JSON.toJSONString(user))
                .build();
        message.setUid(UidUtils.getInstance().getUid());
        message.setOrgUid(thread.getOrgUid());
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        message.setThreadTopic(thread.getTopic());
        //
        MessageExtra extra = MessageUtils.getMessageExtra(thread.getOrgUid());
        message.setExtra(JSON.toJSONString(extra));
        //
        return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    }

    public static MessageProtobuf getThreadContinueMessage(UserProtobuf user, ThreadEntity thread) {
        // ... 方法的实现保持不变 ...
        MessageEntity message = MessageEntity.builder()
                .content(I18Consts.I18N_REENTER_TIP)
                .type(MessageTypeEnum.CONTINUE.name())
                .status(MessageStatusEnum.READ.name())
                .client(ClientEnum.SYSTEM.name())
                .user(JSON.toJSONString(user))
                .build();
        message.setUid(thread.getUid()); // 使用会话的UID作为消息的UID，使得continue消息只保存一条即可
        message.setOrgUid(thread.getOrgUid());
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        message.setThreadTopic(thread.getTopic());
        //
        MessageExtra extra = MessageUtils.getMessageExtra(thread.getOrgUid());
        message.setExtra(JSON.toJSONString(extra));
        //
        return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    }

    public static MessageEntity getAgentThreadOfflineMessage(AgentEntity agent, ThreadEntity thread) {

        UserProtobuf user = ServiceConvertUtils.convertToUserProtobuf(agent);
        // ... 方法的实现保持不变 ...
        MessageEntity message = MessageEntity.builder()
                .content(agent.getLeaveMsgSettings().getLeaveMsgTip())
                .type(MessageTypeEnum.LEAVE_MSG.name())
                .status(MessageStatusEnum.READ.name())
                .client(ClientEnum.SYSTEM.name())
                .user(JSON.toJSONString(user))
                .build();
        message.setUid(UidUtils.getInstance().getUid());
        message.setOrgUid(thread.getOrgUid());
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        message.setThreadTopic(thread.getTopic());
        //
        MessageExtra extra = MessageUtils.getMessageExtra(thread.getOrgUid());
        message.setExtra(JSON.toJSONString(extra));
        //
        // return ConvertServiceUtils.convertToMessageProtobuf(message, thread);
        return message;
    }

    public static MessageEntity getThreadOfflineMessage(WorkgroupEntity workgroup, ThreadEntity thread) {

        UserProtobuf user = ServiceConvertUtils.convertToUserProtobuf(workgroup);
        // ... 方法的实现保持不变 ...
        MessageEntity message = MessageEntity.builder()
                .content(thread.getContent())
                .type(MessageTypeEnum.LEAVE_MSG.name())
                .status(MessageStatusEnum.READ.name())
                .client(ClientEnum.SYSTEM.name())
                .user(JSON.toJSONString(user))
                .build();
        message.setUid(UidUtils.getInstance().getUid());
        message.setOrgUid(thread.getOrgUid());
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        message.setThreadTopic(thread.getTopic());
        //
        MessageExtra extra = MessageUtils.getMessageExtra(thread.getOrgUid());
        message.setExtra(JSON.toJSONString(extra));
        //
        // return ConvertServiceUtils.convertToMessageProtobuf(message, thread);
        return message;
    }

    // 检查无响应触发
    public static void checkNoResponse(String userId, long lastActiveTime, ServiceSettings settings) {
        if (!settings.isEnableProactiveTrigger()) {
            return;
        }

        ServiceTrigger trigger = JSON.parseObject(
            settings.getTriggerConditions(), 
            ServiceTrigger.class
        );

        // 检查无响应触发
        trigger.getConditions().stream()
            .filter(c -> c.getType().equals(ServiceTrigger.TriggerCondition.TYPE_NO_RESPONSE))
            .filter(c -> (System.currentTimeMillis() - lastActiveTime) / 1000 > c.getTimeout())
            .findFirst()
            .ifPresent(condition -> {
                // TODO: 发送主动推送消息
                // sendProactiveMessage(userId, condition.getMessage());
            });
    }


}