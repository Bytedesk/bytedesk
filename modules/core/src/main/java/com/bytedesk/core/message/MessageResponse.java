/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-21 10:00:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-27 13:08:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
public class MessageResponse extends BaseResponse {

	private static final long serialVersionUID = 1L;

	// private String type;
	private MessageTypeEnum type;

	private String content;

	// private String status;
	private MessageStatusEnum status;

	// private String client;
	private ClientEnum client;

    private ThreadResponse thread;

	private UserProtobuf user;

    // extra格式不固定，前端需要根据type字段来解析，所以此处不能使用MessageExtra
	// private MessageExtra extra;
    private String extra;

	// 有帮助、没帮助
    private String helpful;

    // 通过解析user字段中的type字段来判断 type=robot则为机器人，否则为访客
    public boolean isRobot() {
        // 忽略大小写
        return user.getType().toLowerCase().contains("robot");
    }

    // 通过解析user字段中的type字段来判断 type=visitor则为访客，否则为客服
    public boolean isVisitor() {
        // 忽略大小写
        return user.getType().toLowerCase().contains("visitor");
    }

    // 是否系统消息
    public boolean isSystem() {
        // 忽略大小写
        return user.getType().toLowerCase().contains("system");
    }

}
