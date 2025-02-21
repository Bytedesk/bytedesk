/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-03 12:10:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-15 15:07:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.thread_transfer;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTransferContent;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic.TopicService;
import com.bytedesk.team.member.MemberEntity;
import com.bytedesk.team.member.MemberResponseSimple;
import com.bytedesk.team.member.MemberRestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ThreadTransferEventListener {

    private final MemberRestService memberService;

    private final ModelMapper modelMapper;

    private final ThreadTransferService transferService;

    private final TopicService topicService;

    private final ThreadRestService threadService;

    @EventListener
    public void onMessageJsonEvent(MessageJsonEvent event) {
        // log.info("MessageJsonEvent {}", event.getJson());
        String messageJson = event.getJson();

        processMessage(messageJson);
    }

    private void processMessage(String messageJson) {
        MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
        MessageTypeEnum messageType = messageProtobuf.getType();
        if (messageType.equals(MessageTypeEnum.STREAM)) {
            // ai回答暂不处理
            return;
        }
        if (messageType != MessageTypeEnum.TRANSFER
                && messageType != MessageTypeEnum.TRANSFER_ACCEPT
                && messageType != MessageTypeEnum.TRANSFER_REJECT) {
            return;
        }
        log.info("transfer processMessage {}, messageType {}", messageProtobuf.getContent(), messageType);
        ThreadProtobuf threadProtobuf = messageProtobuf.getThread();
        if (threadProtobuf == null) {
            throw new RuntimeException("thread is null");
        }
        //
        String threadTopic = threadProtobuf.getTopic();
        String[] splits = threadTopic.split("/");
        if (splits.length != 4) {
            throw new RuntimeException("transfer thread topic format error");
        }
        // 增加转接记录
        if (messageType.equals(MessageTypeEnum.TRANSFER)) {
            log.info("transfer processMessage: add transfer record");
            String senderMemberUid = splits[2];
            Optional<MemberEntity> senderMemberOptional = memberService.findByUid(senderMemberUid);
            String receiverMemberUid = splits[3];
            Optional<MemberEntity> receiverMemberOptional = memberService.findByUid(receiverMemberUid);
            if (!senderMemberOptional.isPresent() || !receiverMemberOptional.isPresent()) {
                throw new RuntimeException("transfer member not found");
            }
            // 
            MessageTransferContent transferContent = JSONObject.parseObject(messageProtobuf.getContent(),
                    MessageTransferContent.class);
            ThreadEntity transferThread = transferContent.getThread();
            MessageExtra extraObject = JSONObject.parseObject(messageProtobuf.getExtra(), MessageExtra.class);
            String orgUid = extraObject.getOrgUid();
            // 
            MemberResponseSimple sender = modelMapper.map(senderMemberOptional.get(), MemberResponseSimple.class);
            MemberResponseSimple receiver = modelMapper.map(receiverMemberOptional.get(), MemberResponseSimple.class);
            // 
            ThreadTransferEntity transfer = ThreadTransferEntity.builder()
                    .sender(JSON.toJSONString(sender))
                    .receiver(JSON.toJSONString(receiver))
                    .note(transferContent.getNote())
                    .type(ThreadTransferTypeEnum.AGENT_TO_AGENT.name())
                    .status(MessageStatusEnum.SUCCESS.name())
                    .threadTopic(transferThread.getTopic())
                    .build();
            transfer.setUid(messageProtobuf.getUid());
            transfer.setOrgUid(orgUid);
            // 
            transferService.save(transfer);
        }
        // 更新转接记录状态
        if (messageType.equals(MessageTypeEnum.TRANSFER_ACCEPT)
                || messageType.equals(MessageTypeEnum.TRANSFER_REJECT)) {
            log.info("transfer processMessage: update transfer record status {}", messageType);

            MessageTransferContent transferContent = JSONObject.parseObject(messageProtobuf.getContent(),
                    MessageTransferContent.class);
            Optional<ThreadTransferEntity> transferOptional = transferService.findByUid(transferContent.getUid());
            if (!transferOptional.isPresent()) {
                throw new RuntimeException("transfer not found");
            }
            ThreadTransferEntity transfer = transferOptional.get();
            if (messageType.equals(MessageTypeEnum.TRANSFER_ACCEPT)) {
                transfer.setStatus(MessageStatusEnum.TRANSFER_ACCEPT.name());
            } else if (messageType.equals(MessageTypeEnum.TRANSFER_REJECT)) {
                transfer.setStatus(MessageStatusEnum.TRANSFER_REJECT.name());
            }
            transferService.save(transfer);
        }
        // 处理订阅
        if (messageType.equals(MessageTypeEnum.TRANSFER_ACCEPT)) {
            log.info("transfer processMessage: transfer accept");
            String senderMemberUid = splits[3];
            Optional<MemberEntity> senderMemberOptional = memberService.findByUid(senderMemberUid);
            String receiverMemberUid = splits[2];
            Optional<MemberEntity> receiverMemberOptional = memberService.findByUid(receiverMemberUid);
            if (!senderMemberOptional.isPresent() || !receiverMemberOptional.isPresent()) {
                throw new RuntimeException("transfer member not found");
            }
            // 
            MessageTransferContent transferContent = JSONObject.parseObject(messageProtobuf.getContent(),
                    MessageTransferContent.class);
            ThreadEntity transferThread = transferContent.getThread();
            
            // 接手客服添加订阅
            String receiverUid = receiverMemberOptional.get().getUser().getUid();
            topicService.create(transferThread.getTopic(), receiverUid);

            // 原客服取消订阅
            String senderUid = senderMemberOptional.get().getUser().getUid();
            topicService.remove(transferThread.getTopic(), senderUid);

            // 会话转移给转接客服, TODO: 需要保留原先客服痕迹，或方便还原给原先客服？
            transferThread.setOwner(receiverMemberOptional.get().getUser());
            threadService.save(transferThread);
        }
        
        

    }

}
