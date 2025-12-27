/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-19 11:36:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-16 11:58:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.action.ActionEntity;
import com.bytedesk.core.action.event.ActionCreateEvent;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.notice.NoticeRequest;
import com.bytedesk.core.notice.NoticeService;
import com.bytedesk.core.notice.NoticeTypeEnum;
import com.bytedesk.core.notice.extra.NoticeExtraLogin;
import com.bytedesk.core.rbac.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AuthEventListener {

    private final NoticeService noticeService;

    @EventListener
    public void onActionCreateEvent(ActionCreateEvent event) {
        // do something
        ActionEntity action = event.getAction();
        if (action == null) {
            return;
        }
        UserEntity user = action.getUser();
        if (user == null) {
            return;
        }
        final String actionName = action.getAction();
        // 监听登录action，发送登录系统消息，提醒相关用户
        if (BytedeskConsts.ACTION_LOGIN_USERNAME.equals(actionName)
                || BytedeskConsts.ACTION_LOGIN_MOBILE.equals(actionName)
                || BytedeskConsts.ACTION_LOGIN_EMAIL.equals(actionName)) {
            //
            NoticeExtraLogin noticeExtraLogin = NoticeExtraLogin.builder()
                    .loginIp(action.getIp())
                    .loginLocation(action.getIpLocation())
                    .build();
            NoticeRequest noticeRequest = NoticeRequest.builder()
                    .title(action.getTitle())
                    .content(action.getAction())
                    .type(NoticeTypeEnum.LOGIN.name())
                    .status(MessageStatusEnum.READ.name())
                    .extra(noticeExtraLogin.toJson())
                    .userUid(user.getUid())
                    .orgUid(user.getOrgUid())
                    .build();
            noticeService.sendLoginNotice(noticeRequest);
        }
    }

}
