/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-29 22:22:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-07 22:35:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor.strategy;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.Message;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.Thread;
import com.bytedesk.core.thread.ThreadStatusEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.utils.ConvertServiceUtils;

import java.util.Date;

// import org.modelmapper.ModelMapper;

// 可以根据需要选择是否使用 @Component 注解
// 如果该方法不需要被Spring容器管理，则不需要此注解
public class ThreadMessageUtil {

    // 将此方法设为静态，以便在没有实例化类的情况下调用
    public static MessageProtobuf getThreadMessage(UserProtobuf user, Thread thread, boolean isReenter) {
        // ... 方法的实现保持不变 ...
        Message message = Message.builder()
                .content(isReenter ? I18Consts.I18N_REENTER_TIP : thread.getContent())
                .type(isReenter ? MessageTypeEnum.CONTINUE.name() : MessageTypeEnum.WELCOME.name())
                .status(MessageStatusEnum.READ.name())
                .client(ClientEnum.SYSTEM.name())
                .user(JSON.toJSONString(user))
                .build();
        // message.setUid(uidUtils.getCacheSerialUid());
        // message.setUid(Utils.getUid());
        message.setUid(UidUtils.getInstance().getDefaultSerialUid());
        message.setOrgUid(thread.getOrgUid());
        message.setCreatedAt(new Date());
        message.setUpdatedAt(new Date());
        // 
        if (user.getType().equals(UserTypeEnum.ROBOT.name())) {
            message.setType(MessageTypeEnum.WELCOME.name());
            message.setContent(thread.getContent());
        }
        //
        if (thread.getStatus().equals(ThreadStatusEnum.OFFLINE.name())) {
            message.setType(MessageTypeEnum.LEAVE_MSG.name());
        }
        message.setThreadTopic(thread.getTopic());
        //
        MessageExtra extra = MessageUtils.getMessageExtra(thread.getOrgUid());
        message.setExtra(JSON.toJSONString(extra));
        //
        return ConvertServiceUtils.convertToMessageProtobuf(message, thread);
    }
}