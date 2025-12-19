/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 15:46:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-10 21:04:53
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
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;

public class RobotMessageUtils {

        public static MessageProtobuf createRobotMessage(ThreadProtobuf thread, RobotProtobuf robot,
                        MessageProtobuf messageProtobuf) {
                MessageExtra extra = MessageExtra.fromJson(messageProtobuf.getExtra());

                return MessageProtobuf.builder()
                        .uid(UidUtils.getInstance().getUid())
                        .status(MessageStatusEnum.READ)
                        .thread(thread)
                        .user(robot.toUserProtobuf())
                        .channel(ChannelEnum.SYSTEM)
                        .extra(extra.toJson())
                        // .extra(messageProtobuf.getExtra())
                        .createdAt(BdDateUtils.now())
                        .build();
        }

}
