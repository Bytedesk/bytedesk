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
package com.bytedesk.core.task_list;

import com.bytedesk.core.base.BasePermissions;

public class TaskListPermissions extends BasePermissions {

	// 模块前缀
	public static final String TASK_LIST_PREFIX = "TASK_LIST_";

	// 模块名称，用于权限检查
	public static final String MODULE_NAME = "TASK_LIST";

	// 统一权限（不区分层级）
	public static final String TASK_LIST_READ = "TASK_LIST_READ";
	public static final String TASK_LIST_CREATE = "TASK_LIST_CREATE";
	public static final String TASK_LIST_UPDATE = "TASK_LIST_UPDATE";
	public static final String TASK_LIST_DELETE = "TASK_LIST_DELETE";
	public static final String TASK_LIST_EXPORT = "TASK_LIST_EXPORT";

	// 新 PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
	public static final String HAS_TASK_LIST_READ = "hasAuthority('" + TASK_LIST_READ + "')";
	public static final String HAS_TASK_LIST_CREATE = "hasAuthority('" + TASK_LIST_CREATE + "')";
	public static final String HAS_TASK_LIST_UPDATE = "hasAuthority('" + TASK_LIST_UPDATE + "')";
	public static final String HAS_TASK_LIST_DELETE = "hasAuthority('" + TASK_LIST_DELETE + "')";
	public static final String HAS_TASK_LIST_EXPORT = "hasAuthority('" + TASK_LIST_EXPORT + "')";

}
