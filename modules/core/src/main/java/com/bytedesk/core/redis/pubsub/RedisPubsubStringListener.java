/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-15 17:13:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-15 17:54:29
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

import java.util.LinkedList;
import java.util.Queue;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.event.GenericApplicationEvent;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageCache;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.redis.pubsub.message.RedisPubsubMessageFile;
import com.bytedesk.core.redis.pubsub.message.RedisPubsubMessageQa;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class RedisPubsubStringListener implements MessageListener {

    private final BytedeskEventPublisher eventPublisher;

    private final MessageCache messageCache;

    // private final BytedeskEventPublisher bytedeskEventPublisher;

    private final IMessageSendService messageSendService;

    private final Queue<RedisPubsubMessage> messageQueue = new LinkedList<>();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // log.info("onMessage,{}", message.toString());
        String channel = new String(message.getChannel());
        String messageContext = new String(message.getBody());
        //
        RedisPubsubMessage redisPubsubMessage = JSON.parseObject(messageContext, RedisPubsubMessage.class);
        log.info("redisPubsub receiveString: {}, type {}", channel, redisPubsubMessage.getType());
        //
        if (redisPubsubMessage.getType().equals(RedisPubsubMessageType.PARSE_FILE_SUCCESS.name())) {
            // 解析成功
            log.info("parse file success, content {}", redisPubsubMessage.getContent());
            RedisPubsubMessageFile messageFile = JSON.parseObject(redisPubsubMessage.getContent(),
                    RedisPubsubMessageFile.class);
            log.info("fileUid {}, docIds {}", messageFile.getFileUid(), messageFile.getDocIds());
            //
            eventPublisher.publishGenericApplicationEvent(new GenericApplicationEvent<RedisPubsubParseFileSuccessEvent>(
                    this, new RedisPubsubParseFileSuccessEvent(this, messageFile)));

        } else if (redisPubsubMessage.getType().equals(RedisPubsubMessageType.PARSE_FILE_ERROR.name())) {
            // 解析失败
            log.info("parse file error, content {}", redisPubsubMessage.getContent());
            RedisPubsubMessageFile messageFile = JSON.parseObject(redisPubsubMessage.getContent(),
                    RedisPubsubMessageFile.class);
            log.info("fileUid {}", messageFile.getFileUid());
            //
            eventPublisher.publishGenericApplicationEvent(new GenericApplicationEvent<RedisPubsubParseFileErrorEvent>(
                    this, new RedisPubsubParseFileErrorEvent(this, messageFile)));

        } else if (redisPubsubMessage.getType().equals(RedisPubsubMessageType.DELETE_FILE_SUCCESS.name())) {
            // TODO: 删除成功
            log.info("delete file success, content {}", redisPubsubMessage.getContent());

        } else if (redisPubsubMessage.getType().equals(RedisPubsubMessageType.DELETE_FILE_ERROR.name())) {
            // TODO: 删除失败
            log.info("delete file error, content {}", redisPubsubMessage.getContent());

        } else if (redisPubsubMessage.getType().equals(RedisPubsubMessageType.ANSWER.name())) {
            // 回答
            // answer {"threadTopic": "org/robot/df_rt_uid/1463055175142405", "kbUid":
            // "1461090177253570", "question": "\u4f60\u597d", "answer":
            // "\u53ef\u4ee5\u5e2e\u52a9", "model": "glm-4-flash", "created": 1725063232}
            log.info("answer {}", redisPubsubMessage.getContent());
            messageQueue.add(redisPubsubMessage); // 添加消息到队列
            // sendMessage(redisPubsubMessage);
            processMessageQueue();
            // 
        } else if (redisPubsubMessage.getType().equals(RedisPubsubMessageType.ANSWER_FINISHED.name())) {
            // TODO: 回答结束
            // answer finished {"threadTopic": "org/robot/df_rt_uid/1463055175142405",
            // "kbUid": "1461090177253570", "question": "\u4f60\u597d", "answer": "",
            // "model": "glm-4-flash", "created": 1725063232, "promptTokens": 525,
            // "completionTokens": 9, "totalTokens": 534}
            log.info("answer finished {}", redisPubsubMessage.getContent());
            messageQueue.add(redisPubsubMessage); // 添加消息到队列
            // sendMessage(redisPubsubMessage);
            processMessageQueue();
        }
    }

    // FIXME: 直接调用sendMessage会导致消息乱序，增加messageQueue，还是消息乱序，未解决
    private void processMessageQueue() {
        while (!messageQueue.isEmpty()) {
            RedisPubsubMessage redisPubsubMessage = messageQueue.poll();
            sendMessage(redisPubsubMessage);
        }
    }
    
    private void sendMessage(RedisPubsubMessage redisPubsubMessage) {
        log.info("sendMessage, messageQa content {}", redisPubsubMessage.getContent());
        RedisPubsubMessageQa messageQa = JSON.parseObject(redisPubsubMessage.getContent(),
                RedisPubsubMessageQa.class);

        log.info("sendMessage, messageQa Id {}", messageQa.getId());
        MessageProtobuf messageProtobuf = messageCache.get(messageQa.getUid());
        if (messageProtobuf == null) {
            log.error("message not found, uid {}", messageQa.getUid());
            return;
        }
        //
        messageProtobuf.setType(MessageTypeEnum.STREAM);
        messageProtobuf.setContent(messageQa.getAnswer());
        // MessageUtils.notifyUser(messageProtobuf);
        messageSendService.sendProtobufMessage(messageProtobuf);
    }
}
