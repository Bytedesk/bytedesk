/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-16 18:04:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-10 15:57:02
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

import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.thread.Thread;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.StatusConsts;
import com.bytedesk.core.message.Message;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message.MessageService;
import com.bytedesk.core.message.MessageTypeConsts;
import com.bytedesk.core.thread.ThreadService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MessageJsonService {

    private final MessageService messageService;

    private final ThreadService threadService;

    private final ModelMapper modelMapper;

    @Async
    public void saveToDb(String messageJSON) {
        // log.info("saveToDb: {}", messageJSON);
        MessageResponse messageResponse = JSON.parseObject(messageJSON, MessageResponse.class);
        //
        String type = messageResponse.getType();
        //
        if (dealWithMessageNotification(type, messageResponse)) {
            return;
        }
        //
        String uid = messageResponse.getUid();
        if (messageService.existsByUid(uid)) {
            log.info("message already exists, uid: {}", uid);
            return;
        }
        //
        Thread thread = getThread(messageResponse);
        if (thread == null) {
            log.info("thread not exists, tid: {}", messageResponse.getThread().getUid());
            return;
        }

        Message message = getMessage(thread, messageResponse);

        threadService.save(thread);

        messageService.save(message);

        // log.info("save json msg {}", messageJSON);
    }

    private Thread getThread(MessageResponse messageResponse) {

        String uid = messageResponse.getThread().getUid();
        Thread thread = threadService.findByUid(uid).orElse(null);
        if (thread == null) {
            log.info("thread not exists, uid: {}", uid);
            return null;
        }
        thread.setContent(messageResponse.getContent());
        if (messageResponse.getType().equals(MessageTypeConsts.IMAGE)) {
            thread.setContent(I18Consts.I18N_THREAD_CONTENT_IMAGE);
        } else if (messageResponse.getType().equals(MessageTypeConsts.FILE)) {
            thread.setContent(I18Consts.I18N_THREAD_CONTENT_FILE);
        }

        return thread;
    }

    private Message getMessage(Thread thread, MessageResponse messageResponse) {

        Message message = modelMapper.map(messageResponse, Message.class);
        message.setStatus(StatusConsts.MESSAGE_STATUS_STORED);
        message.getThreads().add(thread);
        message.setUser(JSON.toJSONString(messageResponse.getUser()));
        message.setOrgUid(thread.getOrgUid());

        return message;
    }


    // 处理消息通知
    private boolean dealWithMessageNotification(String type, MessageResponse messageResponse) {
        log.info("dealWithMessageNotification: {}", type);

        // 正在输入- 不保存
        if (type.equals(MessageTypeConsts.NOTIFICATION_TYPING)) {
            return true;
        }

        // 消息撤回- 从数据库中删除
        if (type.equals(MessageTypeConsts.NOTIFICATION_RECALL) && dealWithMessageRecall(type, messageResponse)) {
            return true;
        }

        // 消息回执- 处理
        if (type.equals(MessageTypeConsts.NOTIFICATION_RECEIPT) && dealWithMessageReceipt(type, messageResponse)) {
            return true;
        }

        return false;
    }


    // 处理消息回执
    private boolean dealWithMessageReceipt(String type, MessageResponse message) {
        log.info("dealWithMessageReceipt: {}", type);

        return true;
    }

    // 消息撤回，从数据库中删除消息
    private boolean dealWithMessageRecall(String type, MessageResponse message) {
        log.info("dealWithMessageRecall: {}", type);

        return true;
    }

}
