/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-21 10:00:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-17 15:01:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.io.Serializable;
import java.time.LocalDateTime;
// import java.util.Date;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadProtobuf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 所有字段跟message.proto中字段一一对应
 * 
 * response for visitor init/request thread
 * distinguish visitor message from agent view
 * 区分 访客端拉取的消息格式 和 客服端拉取到的消息格式
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class MessageProtobuf implements Serializable {

	private static final long serialVersionUID = 1L;

	private String uid;

	private MessageTypeEnum type;

	private String content;

	private MessageStatusEnum status;

	private LocalDateTime createdAt;

	private ClientEnum client;

	private ThreadProtobuf thread;

	private UserProtobuf user;

	private String extra;

	public static MessageProtobuf fromJson(String user) {
        return JSON.parseObject(user, MessageProtobuf.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

	// public static MessageProtobuf fromEntity(MessageEntity message) {
	// 	return MessageProtobuf.builder()
	// 			.uid(message.getUid())
	// 			.type(message.getType())
	// 			.content(message.getContent())
	// 			.status(message.getStatus())
	// 			.createdAt(message.getCreatedAt())
	// 			.client(message.getClient())
	// 			.thread(ThreadProtobuf.fromEntity(message.getThread()))
	// 			.user(UserProtobuf.fromEntity(message.getUser()))
	// 			.extra(message.getExtra())
	// 			.build();
	// }
}
