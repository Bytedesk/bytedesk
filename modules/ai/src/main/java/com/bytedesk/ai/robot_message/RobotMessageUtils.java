/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 15:46:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 15:46:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_message;

import java.time.LocalDateTime;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.uid.UidUtils;

public class RobotMessageUtils {

    public static MessageProtobuf createRobotMessage(ThreadEntity thread, ThreadProtobuf threadProtobuf,
            MessageProtobuf messageProtobuf) {
        // ThreadEntity thread = threadOptional.get();
        MessageExtra extraObject = JSONObject.parseObject(messageProtobuf.getExtra(), MessageExtra.class);
        String agent = thread.getAgent();
        RobotProtobuf robotProtobuf = JSON.parseObject(agent, RobotProtobuf.class);
        UserProtobuf user = UserProtobuf.builder()
                .nickname(robotProtobuf.getNickname())
                .avatar(robotProtobuf.getAvatar())
                .type(UserTypeEnum.ROBOT.name())
                .build();
        user.setUid(robotProtobuf.getUid());
        String messageUid = UidUtils.getInstance().getUid();
        MessageProtobuf message = MessageProtobuf.builder()
                .uid(messageUid)
                .status(MessageStatusEnum.SUCCESS)
                .thread(threadProtobuf)
                .user(user)
                .client(ClientEnum.ROBOT)
                .extra(JSONObject.toJSONString(extraObject))
                .createdAt(LocalDateTime.now())
                .build();
        return message;
    }
    
}
