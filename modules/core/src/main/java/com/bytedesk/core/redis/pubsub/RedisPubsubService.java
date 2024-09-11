/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-23 19:25:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-31 08:06:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis.pubsub;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.redis.pubsub.message.RedisPubsubMessageFile;
import com.bytedesk.core.redis.pubsub.message.RedisPubsubMessageQa;

@Service
public class RedisPubsubService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void sendParseFileMessage(String fileUid, String fileUrl, String kbUid) {
        // 
        RedisPubsubMessageFile messageFile = RedisPubsubMessageFile.builder()
                .fileUid(fileUid)
                .fileUrl(fileUrl)
                .kbUid(kbUid)
                .build();
        //
        RedisPubsubMessage messageObject = RedisPubsubMessage.builder()
                .type(RedisPubsubMessageType.PARSE_FILE.name())
                .content(JSON.toJSONString(messageFile))
                .build();
        //
        stringRedisTemplate.convertAndSend(
                RedisPubsubConst.BYTEDESK_PUBSUB_CHANNEL_STRING,
                JSON.toJSONString(messageObject));
    }
    
    public void sendDeleteFileMessage(String fileUid, List<String> docIds) {
        //
        RedisPubsubMessageFile messageFile = RedisPubsubMessageFile.builder()
                .fileUid(fileUid)
                .docIds(docIds)
                .build();
        //
        RedisPubsubMessage messageObject = RedisPubsubMessage.builder()
                .type(RedisPubsubMessageType.DELETE_FILE.name())
                .content(JSON.toJSONString(messageFile))
                .build();
        //
        stringRedisTemplate.convertAndSend(
                RedisPubsubConst.BYTEDESK_PUBSUB_CHANNEL_STRING,
                JSON.toJSONString(messageObject));
    }

    public void sendQuestionMessage(String uid, String threadTopic, String kbUid, String question) {
        // 
        RedisPubsubMessageQa messageQa = RedisPubsubMessageQa.builder()
                .uid(uid)
                .threadTopic(threadTopic)
                .kbUid(kbUid)
                .question(question)
                .build();
        //
        RedisPubsubMessage messageObject = RedisPubsubMessage.builder()
                .type(RedisPubsubMessageType.QUESTION.name())
                .content(JSON.toJSONString(messageQa))
                .build();
        // 
        stringRedisTemplate.convertAndSend(
                RedisPubsubConst.BYTEDESK_PUBSUB_CHANNEL_STRING,
                JSON.toJSONString(messageObject));
    }




}
