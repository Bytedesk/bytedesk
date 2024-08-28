/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-19 11:36:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-23 21:39:15
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
import com.bytedesk.core.action.Action;
import com.bytedesk.core.action.ActionCreateEvent;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageService;
import com.bytedesk.core.rbac.user.User;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AuthEventListener {
    
    private final MessageService messageService;

    @EventListener
    public void onActionCreateEvent(ActionCreateEvent event) {
        log.info("onActionCreateEvent Received event: {}", event.getAction().getTitle());
        // do something
        Action action = event.getAction();
        // 监听登录action，发送登录系统消息，提醒相关用户
        if (action.getAction().equals(BdConstants.ACTION_LOGIN_USERNAME)
                || action.getAction().equals(BdConstants.ACTION_LOGIN_MOBILE)
                || action.getAction().equals(BdConstants.ACTION_LOGIN_EMAIL)) {
            //
            User user = action.getUser();
            JSONObject contentObject = new JSONObject();
            contentObject.put(I18Consts.I18N_NOTICE_TITLE, action.getTitle());
            contentObject.put(I18Consts.I18N_NOTICE_CONTENT, action.getAction());
            contentObject.put(I18Consts.I18N_NOTICE_IP, action.getIp());
            contentObject.put(I18Consts.I18N_NOTICE_IPLOCATION, action.getIpLocation());
            MessageProtobuf messsage = messageService.createNoticeMessage(user, JSON.toJSONString(contentObject));
            //
            messageService.notifyUser(messsage);
        }
    }


}