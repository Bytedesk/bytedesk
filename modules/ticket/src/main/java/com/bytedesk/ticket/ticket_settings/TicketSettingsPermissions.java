/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 11:55:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket_settings;

import com.bytedesk.core.base.BasePermissions;

public class TicketSettingsPermissions extends BasePermissions {

	// 模块前缀
	public static final String TICKET_SETTINGS_PREFIX = "TICKET_SETTINGS_";

	// 统一权限（不区分层级）
	public static final String TICKET_SETTINGS_READ = "TICKET_SETTINGS_READ";
	public static final String TICKET_SETTINGS_CREATE = "TICKET_SETTINGS_CREATE";
	public static final String TICKET_SETTINGS_UPDATE = "TICKET_SETTINGS_UPDATE";
	public static final String TICKET_SETTINGS_DELETE = "TICKET_SETTINGS_DELETE";
	public static final String TICKET_SETTINGS_EXPORT = "TICKET_SETTINGS_EXPORT";

	// PreAuthorize 表达式 - 统一权限（不区分层级）
	public static final String HAS_TICKET_SETTINGS_READ = "hasAuthority('TICKET_SETTINGS_READ')";
	public static final String HAS_TICKET_SETTINGS_CREATE = "hasAuthority('TICKET_SETTINGS_CREATE')";
	public static final String HAS_TICKET_SETTINGS_UPDATE = "hasAuthority('TICKET_SETTINGS_UPDATE')";
	public static final String HAS_TICKET_SETTINGS_DELETE = "hasAuthority('TICKET_SETTINGS_DELETE')";
	public static final String HAS_TICKET_SETTINGS_EXPORT = "hasAuthority('TICKET_SETTINGS_EXPORT')";

}
