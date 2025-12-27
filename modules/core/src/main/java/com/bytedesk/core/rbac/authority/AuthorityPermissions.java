/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-04 21:13:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 16:36:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.authority;

import com.bytedesk.core.base.BasePermissions;

public class AuthorityPermissions extends BasePermissions {

	// 模块前缀
	public static final String AUTHORITY_PREFIX = "AUTHORITY_";

	// 统一权限（不再在权限字符串中编码层级）
	public static final String AUTHORITY_READ = "AUTHORITY_READ";
	public static final String AUTHORITY_CREATE = "AUTHORITY_CREATE";
	public static final String AUTHORITY_UPDATE = "AUTHORITY_UPDATE";
	public static final String AUTHORITY_DELETE = "AUTHORITY_DELETE";
	public static final String AUTHORITY_EXPORT = "AUTHORITY_EXPORT";

	// PreAuthorize 表达式
	public static final String HAS_AUTHORITY_READ = "hasAuthority('AUTHORITY_READ')";
	public static final String HAS_AUTHORITY_CREATE = "hasAuthority('AUTHORITY_CREATE')";
	public static final String HAS_AUTHORITY_UPDATE = "hasAuthority('AUTHORITY_UPDATE')";
	public static final String HAS_AUTHORITY_DELETE = "hasAuthority('AUTHORITY_DELETE')";
	public static final String HAS_AUTHORITY_EXPORT = "hasAuthority('AUTHORITY_EXPORT')";

}
