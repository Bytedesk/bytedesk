/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-23 16:13:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-28 12:21:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.local.listener;

import java.io.IOException;

import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.event.MessageJsonEvent;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.socket.protobuf.model.MessageProto;
import com.bytedesk.socket.redis.RedisMessageCacheProtobufService;
import com.bytedesk.socket.utils.MessageConvertUtils;

// import com.bytedesk.socket.service.MessageSocketService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class MessageJsonListener implements ApplicationListener<MessageJsonEvent> {

    // private final MessageSocketService messageSocketService;

    private final RedisMessageCacheProtobufService redisMessageCacheProtobufService;

    @Override
    public void onApplicationEvent(@NonNull MessageJsonEvent event) {
        log.info("MessageJsonListener {}", event.getJson());

        // TODO: 拦截被拉黑/屏蔽用户消息，并给与提示

        // TODO: 过滤敏感词，将敏感词替换为*

        // String filterJson = TabooUtil.replaceSensitiveWord(json, '*');
        String filterJson = event.getJson();
        // log.info("json {}, \nfilterJson {}, \nsize {}", json, filterJson, TabooUtil.sensitiveWordMap.size());

        // 转换为protobuf格式，转发到rabbitmq，发送给mqtt客户端
        JSONObject messageObject = JSON.parseObject(filterJson);
        // JSONObject userObject = messageObject.getJSONObject("user");
        // 替换掉客户端时间戳，统一各个客户端时间戳，防止出现因为客户端时间戳不一致导致的消息乱序
        String timestamp = BdDateUtils.formatDatetimeNow();
        messageObject.put("timestamp", timestamp);
        //
        JSONObject threadObject = messageObject.getJSONObject("thread");
        threadObject.put("timestamp", timestamp);
        // 替换时间戳之后的stomp消息
        String newStompJson = messageObject.toJSONString();
        String type = messageObject.getString("type");
        log.info("newStompJson {} type {} timestamp {}", newStompJson, type, timestamp);

        try {
            MessageProto.Message message = MessageConvertUtils.toProtoBean(MessageProto.Message.newBuilder(),
                    newStompJson);
                
            // TODO: 自动回复

            // TODO: 离线推送

            // TODO: 缓存消息
            redisMessageCacheProtobufService.push(message.toByteArray());

            // TODO: webhook
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
    }

}
