/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-13 10:04:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:19:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.utils.MessageUtils;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessagePersistCache messagePersistCache;

    private final ThreadRestService threadRestService;

    private final IMessageSendService messageSendService;

    private final UidUtils uidUtils;

    public String processMessageJson(String messageJson, Boolean isRobot) {

        MessageProtobuf messageProtobuf = MessageProtobuf.fromJson(messageJson); 

        // 收到消息，更新消息状态为发送成功
        if (messageProtobuf.getStatus().equals(MessageStatusEnum.SENDING)) {
            messageProtobuf.setStatus(MessageStatusEnum.SUCCESS);
        } 
        
        // 机器人消息默认设置为已读
        if (isRobot) {
            messageProtobuf.setStatus(MessageStatusEnum.READ);
        }

        // 防止客户端时间错误，使用服务器时间戳，指定Asia/Shanghai时区
        messageProtobuf.setCreatedAt(BdDateUtils.now());

        // 
        String messageJsonResult = messageProtobuf.toJson();
        // 保存消息
        messagePersistCache.pushForPersist(messageJsonResult);

        return messageJsonResult;
    }

    public void sendForceLogoutMessage(UserEntity user, String orgUid, String sourceType, String sourceUid, String reason) {
        if (user == null || !StringUtils.hasText(user.getUid())) {
            return;
        }

        ThreadEntity thread = getOrCreateSystemThread(user);
        String content = JSON.toJSONString(Map.of(
            "reason", StringUtils.hasText(reason) ? reason : I18Consts.I18N_FORCE_LOGOUT_REASON,
                "sourceType", StringUtils.hasText(sourceType) ? sourceType : "UNKNOWN",
                "sourceUid", StringUtils.hasText(sourceUid) ? sourceUid : "",
                "action", "FORCE_LOGOUT"));

        MessageProtobuf message = MessageUtils.createKickoffMessage(
                uidUtils.getUid(),
                thread.toProtobuf(),
                orgUid,
                content);
        messageSendService.sendProtobufMessage(message);
    }

    private ThreadEntity getOrCreateSystemThread(UserEntity user) {
        String topic = TopicUtils.getSystemTopic(user.getUid());
        Optional<ThreadEntity> existing = threadRestService.findFirstByTopicAndOwner(topic, user);
        if (existing.isPresent()) {
            return existing.get();
        }

        ThreadResponse created = threadRestService.createSystemNoticeAccountThread(user);
        return threadRestService.findByUid(created.getUid())
                .orElseThrow(() -> new RuntimeException("Failed to create system notice thread for user: " + user.getUid()));
    }


}
