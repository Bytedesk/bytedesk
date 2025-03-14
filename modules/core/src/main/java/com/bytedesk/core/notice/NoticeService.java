/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-04 11:22:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-14 16:05:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notice;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.topic.TopicUtils;

@Service
public class NoticeService {

    // send notice
    public void sendLoginNotice(NoticeRequest request) {
        // do something
        JSONObject contentObject = new JSONObject();
            contentObject.put(I18Consts.I18N_NOTICE_TITLE, action.getTitle());
            contentObject.put(I18Consts.I18N_NOTICE_CONTENT, action.getAction());
            contentObject.put(I18Consts.I18N_NOTICE_IP, action.getIp());
            contentObject.put(I18Consts.I18N_NOTICE_IP_LOCATION, action.getIpLocation());
            String content = JSON.toJSONString(contentObject);
            // 
            String userUid = user.getUid();
            String topic = TopicUtils.getSystemTopic(userUid);
            Optional<ThreadEntity> threadOptional = threadRestService.findFirstByTopic(topic);
            if (threadOptional.isPresent()) {
                ThreadEntity thread = threadOptional.get();
                MessageProtobuf message = MessageUtils.createNoticeMessage(uidUtils.getUid(), thread, user.getOrgUid(), content);
                messageSendService.sendProtobufMessage(message);
            }
    }

    // send transfer notice
    public void sendTransferNotice() {
        // do something
    }

    // send invite notice
    public void sendInviteNotice() {
        // do something
    }

    // send close notice
    public void sendCloseNotice() {
        // do something
    }

    // send timeout notice
    public void sendTimeoutNotice() {
        
    }

    // send offline notice
    public void sendOfflineNotice() {
        // do something
    }
    
}
