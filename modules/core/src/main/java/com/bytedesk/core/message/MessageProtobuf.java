/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-21 10:00:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 14:26:05
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
// import java.time.ZonedDateTime;
// import java.util.Date;
import java.time.ZonedDateTime;

import com.alibaba.fastjson2.JSON;
// import com.alibaba.fastjson2.annotation.JSONField;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.utils.BdDateUtils;

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

	private ZonedDateTime createdAt;

	private ChannelEnum channel;

	private ThreadProtobuf thread;

	private UserProtobuf user;

	private String extra;

	/**
	 * 获取格式化的创建时间字符串，用于前端解析
	 * @return 格式化的时间字符串 (yyyy-MM-dd HH:mm:ss)
	 */
	public String getCreatedAt() {
		return BdDateUtils.formatDatetimeToString(createdAt);
	}

	/**
	 * 获取原始的创建时间
	 * @return ZonedDateTime 原始时间对象
	 */
	public ZonedDateTime getCreatedAtDateTime() {
		return createdAt;
	}

	public static MessageProtobuf fromJson(String json) {
        return JSON.parseObject(json, MessageProtobuf.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

	// 将createdAt转换为时间戳
    public Long getTimestamp() {
        return BdDateUtils.toTimestamp(this.createdAt);
    }
}
