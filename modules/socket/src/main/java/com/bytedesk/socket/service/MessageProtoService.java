/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-16 18:02:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 10:44:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.service;

import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.constant.StatusConsts;
import com.bytedesk.core.constant.ThreadTypeConsts;
import com.bytedesk.core.message.MessageService;
import com.bytedesk.core.rbac.user.UserResponseSimple;
import com.bytedesk.core.thread.ThreadService;
import com.bytedesk.socket.protobuf.model.MessageProto;
import com.bytedesk.socket.protobuf.model.ThreadProto;
import com.bytedesk.socket.protobuf.model.UserProto;
import com.google.protobuf.InvalidProtocolBufferException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.bytedesk.core.thread.Thread;
import com.bytedesk.core.message.Message;

@Slf4j
@Service
@AllArgsConstructor
public class MessageProtoService {

    private final MessageService messageService;

    private final ThreadService threadService;
     
    @Async
    public void saveToDb(@NonNull byte[] messageBytes) {
        //
        try {
            MessageProto.Message messageProto = MessageProto.Message.parseFrom(messageBytes);
            //
            String type = messageProto.getType();
            log.info("saveToDb {}", type);

            // 处理消息回执
            dealWithMessageReceipt(type, messageProto);

            // 消息撤回，从数据库中删除消息
            dealWithMessageRecall(type, messageProto);

            // 流式输出，根据mid替换掉旧消息内容content
            // dealWithMessageStream(messageProto);

            // 消息所属的会话
            Thread thread = getThread(messageProto);
            if (thread == null) {
                log.info("thread not exists, uid: {}", messageProto.getThread().getUid());
                return;
            }

            Message message = getMessage(thread, messageProto);

            threadService.save(thread);
            //
            messageService.save(message);

            log.info("save proto msg mid {}, uid {}", message.getUid(), thread.getUid());

            // 以json格式存储到message archive里面，方便搜索
            // String messageJson = ProtobufJsonUtil.toJson(messageProto);
            // messageArchiveService.save(messageJson, type, sendUser.getSubDomain());

        } catch (InvalidProtocolBufferException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private Thread getThread(MessageProto.Message messageProto) {

        ThreadProto.Thread threadProto = messageProto.getThread();
        String uid = threadProto.getUid();
        // log.info("uid: {}, threadType {}", uid, threadType);
        Thread thread = threadService.findByTid(uid).orElse(null);
        if (thread == null) {
            log.info("thread not exists, uid: {}", uid);
            return null;
        }
        // thread.setContent(threadProto.getContent());
        return thread;
    }

    private Message getMessage(Thread thread, MessageProto.Message messageProto) {
        //
        String type = messageProto.getType();
        // 消息发送者
        UserProto.User userProto = messageProto.getUser();
        String uid = userProto.getUid();
        String avatar = userProto.getAvatar();
        String nickname = userProto.getNickname();
        log.debug("uid: {}, avatar {}, nickname {}", uid, nickname, avatar);
        //
        String mid = messageProto.getUid();
        // 持久化消息
        Message message = new Message();
        message.setUid(mid);
        message.setType(type);
        message.setStatus(StatusConsts.MESSAGE_STATUS_STORED);
        message.setClient(messageProto.getClient());
        // 
        UserResponseSimple user = UserResponseSimple.builder().nickname(nickname).avatar(avatar).build();
        user.setUid(uid);
        message.setUser(JSON.toJSONString(user));
        // message.setThread(thread);
        message.getThreads().add(thread);
        if (thread.getType().equals(ThreadTypeConsts.MEMBER)) {
            Thread reverseThread = threadService.getReverse(thread);
            message.getThreads().add(reverseThread);
        }
        //
        // 设置为消息体内的时间戳，但是数据库中还是存储由@CreatedDate生成的时间戳
        // message.setCreatedAt(BdDateUtils.formatStringToDateTime(messageProto.getCreatedAt()));
        // message.setUpdatedAt(BdDateUtils.formatStringToDateTime(messageProto.getCreatedAt()));
        // 暂定：所有消息类型均放到text里面处理，复杂类型存储json
        String content = messageProto.getContent();
        message.setContent(content);
        message.setOrgUid(thread.getOrgUid());
        // 
        return message;
    }


    /**
     * 处理消息回执
     * 
     * @param type
     * @param messageProto
     */
    private void dealWithMessageReceipt(String type, MessageProto.Message messageProto) {

    }

    /**
     * 消息撤回，从数据库中删除消息
     * 
     * @param type
     * @param messageProto
     */
    private void dealWithMessageRecall(String type, MessageProto.Message messageProto) {
        //
    }

    /**
     * 流式输出，根据mid替换掉旧消息内容content
     * 
     * @param type
     * @param messageProto
     */
    // private void dealWithMessageStream(MessageProto.Message messageProto) {
    //     String mid = messageProto.getMid();
    //     Optional<Message> messageOptional = messageService.findByMid(mid);
    //     if (messageOptional.isPresent()) {
    //         //
    //         String content = messageProto.getContent();
    //         messageOptional.get().setContent(content);
    //         messageService.save(messageOptional.get());
    //         return;
    //     }
    // }
    
}
