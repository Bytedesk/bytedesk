/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 15:46:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-10 10:40:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_message;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;

public class RobotMessageUtils {

        public static MessageProtobuf createRobotMessage(ThreadProtobuf thread, RobotProtobuf robot,
                        MessageProtobuf messageProtobuf) {

                return MessageProtobuf.builder()
                        .uid(UidUtils.getInstance().getUid())
                        .status(MessageStatusEnum.SUCCESS)
                        .thread(thread)
                        .user(robot.toUserProtobuf())
                        .channel(ChannelEnum.SYSTEM)
                        .extra(messageProtobuf.getExtra())
                        .createdAt(BdDateUtils.now())
                        .build();
        }

        // public static MessageProtobuf createRobotMessage(ThreadEntity thread,
        // ThreadProtobuf threadProtobuf,
        // RobotEntity robot,
        // MessageProtobuf messageProtobuf) {
        // MessageExtra extraObject = JSONObject.parseObject(messageProtobuf.getExtra(),
        // MessageExtra.class);
        // UserProtobuf user = UserProtobuf.builder()
        // .nickname(robot.getNickname())
        // .avatar(robot.getAvatar())
        // .type(UserTypeEnum.ROBOT.name())
        // .build();
        // user.setUid(robot.getUid());
        // String messageUid = UidUtils.getInstance().getUid();
        // MessageProtobuf message = MessageProtobuf.builder()
        // .uid(messageUid)
        // .status(MessageStatusEnum.SUCCESS)
        // .thread(threadProtobuf)
        // .user(user)
        // .channel(ChannelEnum.ROBOT)
        // .extra(JSONObject.toJSONString(extraObject))
        // .createdAt(BdDateUtils.now())
        // .build();
        // return message;
        // }

}
