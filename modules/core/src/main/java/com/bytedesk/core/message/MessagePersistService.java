/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-16 18:04:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-07 16:23:29
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
import org.springframework.util.StringUtils;

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
        // log.info("persist: {}", messageJSON);
        MessageProtobuf messageProtobuf = JSON.parseObject(messageJSON, MessageProtobuf.class);
        //
        MessageTypeEnum type = messageProtobuf.getType();
        String threadTopic = messageProtobuf.getThread().getTopic();
        // log.info("orgUid: {}", orgUid);

        // 返回true表示该消息是系统通知，不应该保存到数据库
        if (dealWithMessageNotification(type, messageProtobuf)) {
            // log.info("message should not be saved uid {}, type {}", messageProtobuf.getUid(), type);
            return;
        }
        //
        String uid = messageProtobuf.getUid();
        if (messageService.existsByUid(uid)) {
            // 流式消息单独处理下
            if (type.equals(MessageTypeEnum.STREAM)) {
                // 更新消息内容
                Optional<Message> message = messageService.findByUid(uid);
                if (message.isPresent()) {
                    Message m = message.get();
                    m.setContent(m.getContent() + messageProtobuf.getContent());
                    messageService.save(m);
                }
                return;
            }
            log.info("message already exists, uid: {}", uid);
            //
            return;
        }
        //
        Message message = modelMapper.map(messageProtobuf, Message.class);
        //
        if (messageProtobuf.getStatus().equals(MessageStatusEnum.SENDING)) {
            message.setStatus(MessageStatusEnum.SUCCESS.name());
        }
        message.setThreadTopic(threadTopic);
        message.setUser(JSON.toJSONString(messageProtobuf.getUser()));
        // 
        MessageExtra extraObject = JSONObject.parseObject(messageProtobuf.getExtra(), MessageExtra.class);
        if (extraObject != null) {
            String orgUid = extraObject.getOrgUid();
            message.setOrgUid(orgUid);
        }
        //
        messageService.save(message);
    }

    // 处理消息通知，已处理的消息返回true，未处理的消息返回false
    private boolean dealWithMessageNotification(MessageTypeEnum type, MessageProtobuf messageProtobuf) {
        log.info("dealWithMessageNotification: {}", type);

        // 正在输入/消息预知 - 不保存
        if (type.equals(MessageTypeEnum.TYPING)
                || type.equals(MessageTypeEnum.PROCESSING)
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

        //
        if (type.equals(MessageTypeEnum.RATE_SUBMIT)
                || type.equals(MessageTypeEnum.RATE_CANCEL)) {
            // 如果是客服邀请评价/主动评价，则content为邀请/主动评价消息的uid
            if (StringUtils.hasText(messageProtobuf.getContent())) {
                dealWithRateMessage(type, messageProtobuf);
                return true;
            }
        }

        //
        if (type.equals(MessageTypeEnum.LEAVE_MSG_SUBMIT)) {
            // content为留言提示消息的uid
            if (StringUtils.hasText(messageProtobuf.getContent())) {
                dealWithLeaveMsg(type, messageProtobuf);
                return true;
            }
        }

        //
        if (type.equals(MessageTypeEnum.FAQ_UP)
                || type.equals(MessageTypeEnum.FAQ_DOWN)) {
            // content为被评价的faq消息的uid
            if (StringUtils.hasText(messageProtobuf.getContent())) {
                dealWithFaqRateMessage(type, messageProtobuf);
                return true;
            }
        }

        //
        if (type.equals(MessageTypeEnum.ROBOT_UP)
                || type.equals(MessageTypeEnum.ROBOT_DOWN)) {
            // content为邀请评价消息的uid
            if (StringUtils.hasText(messageProtobuf.getContent())) {
                dealWithRobotRateMessage(type, messageProtobuf);
                return true;
            }
        }

        //
        if (type.equals(MessageTypeEnum.TRANSFER_ACCEPT)
                || type.equals(MessageTypeEnum.TRANSFER_REJECT)) {
            // content为转接消息的uid
            if (StringUtils.hasText(messageProtobuf.getContent())) {
                dealWithTransferMessage(type, messageProtobuf);
                return true;
            }
        }

        return false;
    }

    // 处理消息回执
    private void dealWithMessageReceipt(MessageTypeEnum type, @Nonnull MessageProtobuf message) {
        log.info("dealWithMessageReceipt: {}", type);
        // 回执消息内容存储被回执消息的uid
        // 当status已经为read时，不处理。防止deliverd在后面更新read消息
        Optional<Message> messageOpt = messageService.findByUid(message.getContent());
        if (messageOpt.isPresent() && messageOpt.get().getStatus() != MessageStatusEnum.READ.name()) {
            Message messageEntity = messageOpt.get();
            if (type.equals(MessageTypeEnum.READ)) {
                messageEntity.setStatus(MessageStatusEnum.READ.name());
            } else if (type.equals(MessageTypeEnum.DELIVERED)) {
                messageEntity.setStatus(MessageStatusEnum.DELIVERED.name());
            }
            messageService.save(messageEntity);
        }
    }

    // 消息撤回，从数据库中删除消息
    private void dealWithMessageRecall(MessageProtobuf message) {
        // log.info("dealWithMessageRecall");
        messageService.deleteByUid(message.getUid());
    }

    private void dealWithRateMessage(MessageTypeEnum type, MessageProtobuf message) {
        // log.info("dealWithMessageRateSubmit");
        // 如果是客服邀请评价，则content为邀请评价消息的uid，否则为空
        Optional<Message> messageOpt = messageService.findByUid(message.getContent());
        if (messageOpt.isPresent()) {
            Message messageEntity = messageOpt.get();
            if (type.equals(MessageTypeEnum.RATE_SUBMIT)) {
                messageEntity.setStatus(MessageStatusEnum.RATE_SUBMIT.name());
                messageEntity.setContent(message.getExtra());
            } else if (type.equals(MessageTypeEnum.RATE_CANCEL)) {
                messageEntity.setStatus(MessageStatusEnum.RATE_CANCEL.name());
            }
            messageService.save(messageEntity);
        }
    }

    private void dealWithLeaveMsg(MessageTypeEnum type, MessageProtobuf message) {
        // log.info("dealWithLeaveMsg");
        Optional<Message> messageOpt = messageService.findByUid(message.getContent());
        if (messageOpt.isPresent()) {
            Message messageEntity = messageOpt.get();
            if (type.equals(MessageTypeEnum.LEAVE_MSG_SUBMIT)) {
                messageEntity.setStatus(MessageStatusEnum.LEAVE_MSG_SUBMIT.name());
                messageEntity.setContent(message.getExtra());
                messageService.save(messageEntity);
            }
        }
    }

    private void dealWithFaqRateMessage(MessageTypeEnum type, MessageProtobuf message) {
        // log.info("dealWithFaqRateMessage");
        Optional<Message> messageOpt = messageService.findByUid(message.getContent());
        if (messageOpt.isPresent()) {
            Message messageEntity = messageOpt.get();
            if (type.equals(MessageTypeEnum.FAQ_UP)) {
                messageEntity.setStatus(MessageStatusEnum.RATE_UP.name());
            } else if (type.equals(MessageTypeEnum.FAQ_DOWN)) {
                messageEntity.setStatus(MessageStatusEnum.RATE_DOWN.name());
            }
            messageService.save(messageEntity);
        }
    }

    private void dealWithRobotRateMessage(MessageTypeEnum type, MessageProtobuf message) {
        // log.info("dealWithRobotRateMessage");
        //
        Optional<Message> messageOpt = messageService.findByUid(message.getContent());
        if (messageOpt.isPresent()) {
            Message messageEntity = messageOpt.get();
            if (type.equals(MessageTypeEnum.ROBOT_UP)) {
                messageEntity.setStatus(MessageStatusEnum.RATE_UP.name());
            } else if (type.equals(MessageTypeEnum.ROBOT_DOWN)) {
                messageEntity.setStatus(MessageStatusEnum.RATE_DOWN.name());
            }
            messageService.save(messageEntity);
        }
    }

    private void dealWithTransferMessage(MessageTypeEnum type, MessageProtobuf message) {
        // log.info("dealWithTransferMessage");
        MessageTransferContent transferContentObject = JSONObject.parseObject(message.getContent(),
                MessageTransferContent.class);
        //
        Optional<Message> messageOpt = messageService.findByUid(transferContentObject.getUid());
        if (messageOpt.isPresent()) {
            Message messageEntity = messageOpt.get();
            if (type.equals(MessageTypeEnum.TRANSFER_ACCEPT)) {
                messageEntity.setStatus(MessageStatusEnum.TRANSFER_ACCEPT.name());
            } else if (type.equals(MessageTypeEnum.TRANSFER_REJECT)) {
                messageEntity.setStatus(MessageStatusEnum.TRANSFER_REJECT.name());
            }
            messageService.save(messageEntity);
        }
    }

}
