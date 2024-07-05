/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-16 18:04:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-05 11:04:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MessagePersistService {

    private final MessageService messageService;

    private final ModelMapper modelMapper;

    public void persist(String messageJSON) {
        log.info("persist: {}", messageJSON);
        MessageProtobuf messageProtobuf = JSON.parseObject(messageJSON, MessageProtobuf.class);
        // 
        MessageTypeEnum type = messageProtobuf.getType();
        String threadTopic = messageProtobuf.getThread().getTopic();
        MessageExtra extraObject = JSONObject.parseObject(messageProtobuf.getExtra(), MessageExtra.class);
        String orgUid = extraObject.getOrgUid();
        log.info("orgUid: {}", orgUid);

        // 返回true表示该消息是系统通知，不应该保存到数据库
        if (dealWithMessageNotification(type, messageProtobuf)) {
            log.info("message should not be saved uid {}, type {}", messageProtobuf.getUid(), type);
            return;
        }
        //
        String uid = messageProtobuf.getUid();
        if (messageService.existsByUid(uid)) {
            log.info("message already exists, uid: {}", uid);
            return;
        }
        //
        Message message = modelMapper.map(messageProtobuf, Message.class);
        //
        if (messageProtobuf.getStatus().equals(MessageStatusEnum.SENDING)) {
            message.setStatus(MessageStatusEnum.SUCCESS);
        }
        // message.getThreads().add(thread);
        message.setThreadTopic(threadTopic);
        message.setUser(JSON.toJSONString(messageProtobuf.getUser()));
        message.setOrgUid(orgUid);

        messageService.save(message);
    }

    // 处理消息通知，已处理的消息返回true，未处理的消息返回false
    private boolean dealWithMessageNotification(MessageTypeEnum type, MessageProtobuf messageProtobuf) {
        log.info("dealWithMessageNotification: {}", type);

        // 正在输入/消息预知 - 不保存
        if (type.equals(MessageTypeEnum.TYPING)
                || type.equals(MessageTypeEnum.PREVIEW)) {
            return true;
        }

        // 继续会话 - 不保存
        if (type.equals(MessageTypeEnum.CONTINUE)) {
            return true;
        }

        // 消息撤回 - 从数据库中删除
        if (type.equals(MessageTypeEnum.RECALL)) {
            dealWithMessageRecall(messageProtobuf);
            return true;
        }

        // 消息送达回执 - 处理
        if (type.equals(MessageTypeEnum.DELIVERED)) {
            dealWithMessageReceipt(type, messageProtobuf);
            return true;
        }

        // 消息已读回执 - 处理
        if (type.equals(MessageTypeEnum.READ)) {
            dealWithMessageReceipt(type, messageProtobuf);
            return true;
        }

        return false;
    }

    // 处理消息回执
    private boolean dealWithMessageReceipt(MessageTypeEnum type, @Nonnull MessageProtobuf message) {
        log.info("dealWithMessageReceipt: {}", type);

        // 回执消息内容存储被回执消息的uid
        // 当status已经为read时，不处理。防止deliverd在后面更新read消息
        Optional<Message> messageOpt = messageService.findByUid(message.getContent());
        if (messageOpt.isPresent() && messageOpt.get().getStatus() != MessageStatusEnum.READ) {
            Message messageEntity = messageOpt.get();
            if (type.equals(MessageTypeEnum.READ)) {
                messageEntity.setStatus(MessageStatusEnum.READ);
            } else if (type.equals(MessageTypeEnum.DELIVERED)) {
                messageEntity.setStatus(MessageStatusEnum.DELIVERED);
            }
            messageService.save(messageEntity);
        }

        return true;
    }

    // 消息撤回，从数据库中删除消息
    private boolean dealWithMessageRecall(MessageProtobuf message) {
        log.info("dealWithMessageRecall");
        messageService.deleteByUid(message.getUid());

        return true;
    }

}
