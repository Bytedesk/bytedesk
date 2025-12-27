/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-03 09:40:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import com.bytedesk.core.base.BasePermissions;

public class QueueMemberPermissions extends BasePermissions {

	// 模块前缀
	public static final String QUEUE_MEMBER_PREFIX = "QUEUE_MEMBER_";

	// 统一权限（不区分层级）
	public static final String QUEUE_MEMBER_READ = "QUEUE_MEMBER_READ";
	public static final String QUEUE_MEMBER_CREATE = "QUEUE_MEMBER_CREATE";
	public static final String QUEUE_MEMBER_UPDATE = "QUEUE_MEMBER_UPDATE";
	public static final String QUEUE_MEMBER_DELETE = "QUEUE_MEMBER_DELETE";
	public static final String QUEUE_MEMBER_EXPORT = "QUEUE_MEMBER_EXPORT";

	// PreAuthorize 表达式 - 统一权限（不区分层级）
	public static final String HAS_QUEUE_MEMBER_READ = "hasAuthority('QUEUE_MEMBER_READ')";
	public static final String HAS_QUEUE_MEMBER_CREATE = "hasAuthority('QUEUE_MEMBER_CREATE')";
	public static final String HAS_QUEUE_MEMBER_UPDATE = "hasAuthority('QUEUE_MEMBER_UPDATE')";
	public static final String HAS_QUEUE_MEMBER_DELETE = "hasAuthority('QUEUE_MEMBER_DELETE')";
	public static final String HAS_QUEUE_MEMBER_EXPORT = "hasAuthority('QUEUE_MEMBER_EXPORT')";

}
