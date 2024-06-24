/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-21 10:00:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-05 22:34:42
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

import java.io.Serializable;
import java.util.Date;

import com.bytedesk.core.rbac.user.UserResponseSimple;
import com.bytedesk.core.thread.ThreadResponseSimpleWithoutExtra;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * response for visitor init/request thread
 * distinguish visitor message from agent view
 * 区分 访客端拉取的消息格式 和 客服端拉取到的消息格式
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class MessageNotify implements Serializable {

	private static final long serialVersionUID = 1L;	

	private String uid;

	// private String type;
	private MessageTypeEnum type;

	private String content;

	private String status;

	private String client;

	private Date createdAt;

    /** */
	private ThreadResponseSimpleWithoutExtra thread;

    /** */
	private UserResponseSimple user;

}
