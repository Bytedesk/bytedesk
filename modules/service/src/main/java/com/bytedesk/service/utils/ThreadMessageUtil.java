/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-29 22:22:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 19:23:53
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
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.message.content.QueueContent;
import com.bytedesk.core.message.content.WelcomeContent;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.kbase.settings.ServiceSettingsEntity;
import com.bytedesk.kbase.settings.ServiceTrigger;
import lombok.experimental.UtilityClass;

// 可以根据需要选择是否使用 @Component 注解
// 如果该方法不需要被Spring容器管理，则不需要此注解
@UtilityClass
public class ThreadMessageUtil {

    /**
     * 结构化 WelcomeContent 的机器人欢迎消息
     */
    public static MessageEntity getThreadRobotWelcomeMessage(WelcomeContent content, ThreadEntity thread) {
        MessageExtra extra = MessageExtra.fromOrgUid(thread.getOrgUid());
        MessageUtils.attachSequenceNumber(extra, thread.getUid());
        String json = content != null ? content.toJson() : null;

        MessageEntity message = MessageEntity.builder()
                .uid(UidUtils.getInstance().getUid())
                .content(json)
                .thread(thread)
                .type(MessageTypeEnum.WELCOME.name())
                .status(MessageStatusEnum.READ.name())
                .channel(ChannelEnum.SYSTEM.name())
                .user(thread.getRobot())
                .orgUid(thread.getOrgUid())
                .extra(extra.toJson())
                .createdAt(BdDateUtils.now())
                .updatedAt(BdDateUtils.now())
                .build();
        return message;
    }

    /**
     * 结构化 WelcomeContent 的人工欢迎消息
     */
    public static MessageProtobuf getThreadWelcomeMessage(WelcomeContent content, ThreadEntity thread) {
        MessageExtra extra = MessageExtra.fromOrgUid(thread.getOrgUid());
        MessageUtils.attachSequenceNumber(extra, thread.getUid());
        String json = content != null ? content.toJson() : null;

        MessageEntity message = MessageEntity.builder()
                .uid(UidUtils.getInstance().getUid())
                .content(json)
                .type(MessageTypeEnum.WELCOME.name())
                .status(MessageStatusEnum.READ.name())
                .channel(ChannelEnum.SYSTEM.name())
                .user(thread.getAgent())
                .orgUid(thread.getOrgUid())
                .createdAt(BdDateUtils.now())
                .updatedAt(BdDateUtils.now())
                .thread(thread)
                .extra(extra.toJson())
                .build();

        return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    }

    /**
     * 结构化 QueueContent 的排队消息
     */
    public static MessageProtobuf getThreadQueueMessage(QueueContent content, ThreadEntity thread) {
        UserProtobuf system = UserProtobuf.getSystemUser();
        MessageExtra extra = MessageExtra.fromOrgUid(thread.getOrgUid());
        MessageUtils.attachSequenceNumber(extra, thread.getUid());
        String json = content != null ? content.toJson() : null;
        // 
        MessageEntity message = MessageEntity.builder()
                .uid(UidUtils.getInstance().getUid())
                .content(json)
                .type(MessageTypeEnum.QUEUE.name())
                .status(MessageStatusEnum.READ.name())
                .channel(ChannelEnum.SYSTEM.name())
                .user(system.toJson())
                .orgUid(thread.getOrgUid())
                .createdAt(BdDateUtils.now())
                .updatedAt(BdDateUtils.now())
                .thread(thread)
                .extra(extra.toJson())
                .build();
        return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    }

    /**
     * 构造发送给客服排队线程的 QUEUE_NOTICE 消息
     */
    // public static MessageProtobuf getAgentQueueNoticeMessage(QueueNotificationPayload payload, ThreadEntity thread) {
    //     UserProtobuf system = UserProtobuf.getSystemUser();
    //     MessageExtra extra = MessageExtra.fromOrgUid(thread.getOrgUid());
    //     MessageUtils.attachSequenceNumber(extra, thread.getUid());

    //     String json = payload != null ? JSON.toJSONString(payload) : null;

    //     MessageEntity message = MessageEntity.builder()
    //             .uid(UidUtils.getInstance().getUid())
    //             .content(json)
    //             .type(MessageTypeEnum.QUEUE_NOTICE.name())
    //             .status(MessageStatusEnum.READ.name())
    //             .channel(ChannelEnum.SYSTEM.name())
    //             .user(system.toJson())
    //             .orgUid(thread.getOrgUid())
    //             .createdAt(BdDateUtils.now())
    //             .updatedAt(BdDateUtils.now())
    //             .thread(thread)
    //             .extra(extra.toJson())
    //             .build();

    //     return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    // }

    public static MessageProtobuf getThreadContinueMessage(UserProtobuf user, ThreadEntity thread) {
        MessageExtra extra = MessageExtra.fromOrgUid(thread.getOrgUid());
        MessageUtils.attachSequenceNumber(extra, thread.getUid());

        MessageEntity message = MessageEntity.builder()
                .uid(thread.getUid()) // 使用会话的UID作为消息的UID，使得continue消息只保存一条即可
                .content(I18Consts.I18N_REENTER_TIP)
                .type(MessageTypeEnum.CONTINUE.name())
                .status(MessageStatusEnum.READ.name())
                .channel(ChannelEnum.SYSTEM.name())
                .user(user.toJson())
                .orgUid(thread.getOrgUid())
                .createdAt(BdDateUtils.now())
                .updatedAt(BdDateUtils.now())
                .thread(thread)
                .extra(extra.toJson())
                .build();

        return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    }

    public static MessageEntity getAgentThreadOfflineMessage(String content, ThreadEntity thread) {
        UserProtobuf system = UserProtobuf.getSystemUser();
        MessageExtra extra = MessageExtra.fromOrgUid(thread.getOrgUid());
        MessageUtils.attachSequenceNumber(extra, thread.getUid());

        MessageEntity message = MessageEntity.builder()
                .uid(UidUtils.getInstance().getUid())
                .content(content)
                .type(MessageTypeEnum.LEAVE_MSG.name())
                .status(MessageStatusEnum.READ.name())
                .channel(ChannelEnum.SYSTEM.name())
                .user(system.toJson())
                .orgUid(thread.getOrgUid())
                .createdAt(BdDateUtils.now())
                .updatedAt(BdDateUtils.now())
                .thread(thread)
                .extra(extra.toJson())
                .build();

        return message;
    }

    public static MessageEntity getThreadOfflineMessage(String content, ThreadEntity thread) {
        UserProtobuf system = UserProtobuf.getSystemUser();
        MessageExtra extra = MessageExtra.fromOrgUid(thread.getOrgUid());
        MessageUtils.attachSequenceNumber(extra, thread.getUid());

        MessageEntity message = MessageEntity.builder()
                .uid(UidUtils.getInstance().getUid())
                .content(content)
                .type(MessageTypeEnum.LEAVE_MSG.name())
                .status(MessageStatusEnum.READ.name())
                .channel(ChannelEnum.SYSTEM.name())
                .user(system.toJson())
                .orgUid(thread.getOrgUid())
                .createdAt(BdDateUtils.now())
                .updatedAt(BdDateUtils.now())
                .thread(thread)
                .extra(extra.toJson())
                .build();

        return message;
    }

    // 检查无响应触发
    public static void checkNoResponse(String userUid, long lastActiveTime, ServiceSettingsEntity settings) {
        if (!settings.getEnableProactiveTrigger()) {
            return;
        }

        // 检查用户是否在灰度范围内
        // if (grayReleaseService.isUserInGrayRelease(userUid,
        // GrayReleaseFeature.PROACTIVE_TRIGGER.getCode())) {
        // // 启用功能
        // // ...
        // }

        // 检查用户是否可以使用主动触发功能
        // if (!settings.getGrayReleaseConfig().isUserInGrayRelease(userUid,
        // "proactive_trigger")) {
        // return;
        // }

        ServiceTrigger trigger = JSON.parseObject(
                settings.getTriggerConditions(),
                ServiceTrigger.class);

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