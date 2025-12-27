/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 11:41:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.process;

import com.bytedesk.core.base.BasePermissions;

public class ProcessPermissions extends BasePermissions {

	// 模块前缀
	public static final String PROCESS_PREFIX = "PROCESS_";

	// 统一权限（不区分层级）
	public static final String PROCESS_READ = "PROCESS_READ";
	public static final String PROCESS_CREATE = "PROCESS_CREATE";
	public static final String PROCESS_UPDATE = "PROCESS_UPDATE";
	public static final String PROCESS_DELETE = "PROCESS_DELETE";
	public static final String PROCESS_EXPORT = "PROCESS_EXPORT";

	// PreAuthorize 表达式 - 统一权限（不区分层级）
	public static final String HAS_PROCESS_READ = "hasAuthority('PROCESS_READ')";
	public static final String HAS_PROCESS_CREATE = "hasAuthority('PROCESS_CREATE')";
	public static final String HAS_PROCESS_UPDATE = "hasAuthority('PROCESS_UPDATE')";
	public static final String HAS_PROCESS_DELETE = "hasAuthority('PROCESS_DELETE')";
	public static final String HAS_PROCESS_EXPORT = "hasAuthority('PROCESS_EXPORT')";
}