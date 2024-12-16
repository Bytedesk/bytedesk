/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-29 22:22:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-16 15:52:13
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

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.utils.ConvertServiceUtils;

import java.time.LocalDateTime;
// import java.time.LocalDateTime;

// 可以根据需要选择是否使用 @Component 注解
// 如果该方法不需要被Spring容器管理，则不需要此注解
public class ThreadMessageUtil {

    // 将此方法设为静态，以便在没有实例化类的情况下调用
    public static MessageProtobuf getThreadWelcomeMessage(UserProtobuf user, ThreadEntity thread) {
        // ... 方法的实现保持不变 ...
        MessageEntity message = MessageEntity.builder()
                .content(thread.getContent())
                .type( MessageTypeEnum.WELCOME.name())
                .status(MessageStatusEnum.READ.name())
                .client(ClientEnum.SYSTEM.name())
                .user(JSON.toJSONString(user))
                .build();
        message.setUid(UidUtils.getInstance().getUid());
        message.setOrgUid(thread.getOrgUid());
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        // 
        if (user.getType().equals(UserTypeEnum.ROBOT.name())) {
            message.setType(MessageTypeEnum.WELCOME.name());
            message.setContent(thread.getContent());
        }
        //
        if (thread.isOffline()) {
            message.setType(MessageTypeEnum.LEAVE_MSG.name());
        }
        message.setThreadTopic(thread.getTopic());
        //
        MessageExtra extra = MessageUtils.getMessageExtra(thread.getOrgUid());
        message.setExtra(JSON.toJSONString(extra));
        //
        return ConvertServiceUtils.convertToMessageProtobuf(message, thread);
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
        return ConvertServiceUtils.convertToMessageProtobuf(message, thread);
    }


    public static MessageProtobuf getThreadOfflineMessage(AgentEntity agent, ThreadEntity thread) {

        UserProtobuf user = ConvertServiceUtils.convertToUserProtobuf(agent);
        // ... 方法的实现保持不变 ...
        MessageEntity message = MessageEntity.builder()
                .content(thread.getContent())
                .type(MessageTypeEnum.LEAVE_MSG.name())
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
        return ConvertServiceUtils.convertToMessageProtobuf(message, thread);
    }
}