/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-19 11:36:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-04 16:52:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.action.ActionEntity;
import com.bytedesk.core.action.ActionCreateEvent;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AuthEventListener {
    
    private final IMessageSendService messageSendService;

    private final UidUtils uidUtils;

    @EventListener
    public void onActionCreateEvent(ActionCreateEvent event) {
        log.info("onActionCreateEvent Received event: {}", event.toString());
        // do something
        ActionEntity action = event.getAction();
        // 监听登录action，发送登录系统消息，提醒相关用户
        if (action.getAction().equals(BytedeskConsts.ACTION_LOGIN_USERNAME)
                || action.getAction().equals(BytedeskConsts.ACTION_LOGIN_MOBILE)
                || action.getAction().equals(BytedeskConsts.ACTION_LOGIN_EMAIL)) {
            //
            UserEntity user = action.getUser();
            if (user == null) {
                return;
            }
            JSONObject contentObject = new JSONObject();
            contentObject.put(I18Consts.I18N_NOTICE_TITLE, action.getTitle());
            contentObject.put(I18Consts.I18N_NOTICE_CONTENT, action.getAction());
            contentObject.put(I18Consts.I18N_NOTICE_IP, action.getIp());
            contentObject.put(I18Consts.I18N_NOTICE_IP_LOCATION, action.getIpLocation());
            // 
            MessageProtobuf message = MessageUtils.createNoticeMessage(uidUtils.getCacheSerialUid(), user.getUid(), user.getOrgUid(),
                    JSON.toJSONString(contentObject));
            // MessageUtils.notifyUser(message);
            messageSendService.sendProtobufMessage(message);
        }
    }


}
