/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-04 11:22:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-15 21:24:37
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

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final ThreadRestService threadRestService;
    // private final NoticeRestService noticeRestService;
    private final IMessageSendService messageSendService;
    private final UidUtils uidUtils;
    private final ModelMapper modelMapper;

    // send notice
    public void sendNotice(NoticeRequest request) {
        // 存储暂时未用到，暂不存储
        // NoticeResponse noticeResponse = noticeRestService.create(request);
        NoticeProtobuf noticeProtobuf = modelMapper.map(request, NoticeProtobuf.class);
        String jsonContent = noticeProtobuf.toJson();
        // do something
        String topic = TopicUtils.getSystemTopic(request.getUserUid());
        Optional<ThreadEntity> threadOptional = threadRestService.findFirstByTopic(topic);
        if (threadOptional.isPresent()) {
            ThreadEntity thread = threadOptional.get();
            // send notice message
            MessageProtobuf message = MessageUtils.createNoticeMessage(uidUtils.getUid(), thread.toProtobuf(), request.getOrgUid(), jsonContent);
            messageSendService.sendProtobufMessage(message);
        }
    }

}
