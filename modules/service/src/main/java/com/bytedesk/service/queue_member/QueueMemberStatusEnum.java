/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-07 15:49:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-03 13:56:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

/**
 * 队列成员状态枚举
 */
public enum QueueMemberStatusEnum {

	QUEUING,
	ASSIGNED,
	TIMEOUT,
	CANCELLED;

	/**
	 * 是否处于终止状态
	 */
	public boolean isTerminal() {
		return this == ASSIGNED || this == TIMEOUT || this == CANCELLED;
	}

	public static QueueMemberStatusEnum fromValue(String value) {
		for (QueueMemberStatusEnum status : QueueMemberStatusEnum.values()) {
			if (status.name().equalsIgnoreCase(value)) {
				return status;
			}
		}
		throw new IllegalArgumentException("Invalid queue member status: " + value);
	}
} 