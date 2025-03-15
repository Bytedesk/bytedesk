/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-19 11:36:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-15 10:42:59
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
import com.bytedesk.core.action.ActionCreateEvent;
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

    // private final IMessageSendService messageSendService;

    // private final UidUtils uidUtils;

    // private final ThreadRestService threadRestService;

    private final NoticeService noticeService;

    @EventListener
    public void onActionCreateEvent(ActionCreateEvent event) {
        log.info("onActionCreateEvent Received event: {}", event.toString());
        // do something
        ActionEntity action = event.getAction();
        if (action == null) {
            return;
        }
        // 监听登录action，发送登录系统消息，提醒相关用户
        if (action.getAction().equals(BytedeskConsts.ACTION_LOGIN_USERNAME)
                || action.getAction().equals(BytedeskConsts.ACTION_LOGIN_MOBILE)
                || action.getAction().equals(BytedeskConsts.ACTION_LOGIN_EMAIL)) {
            //
            UserEntity user = action.getUser();
            if (user == null) {
                return;
            }
            //
            NoticeExtraLogin noticeExtraLogin = NoticeExtraLogin.builder()
                    .loginIp(action.getIp())
                    .loginLocation(action.getIpLocation())
                    .build();
            NoticeRequest noticeRequest = NoticeRequest.builder()
                    .title(action.getTitle())
                    .content(action.getAction())
                    .type(NoticeTypeEnum.LOGIN.name())
                    .status(MessageStatusEnum.SUCCESS.name())
                    .extra(noticeExtraLogin.toJson())
                    .userUid(user.getUid())
                    .orgUid(user.getOrgUid())
                    .build();
            noticeService.sendNotice(noticeRequest);
        }
    }

}
