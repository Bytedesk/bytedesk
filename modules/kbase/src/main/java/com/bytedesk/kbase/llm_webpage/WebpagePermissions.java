/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-08 13:40:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_webpage;

import com.bytedesk.core.base.BasePermissions;

public class WebpagePermissions extends BasePermissions {

    // 模块前缀
    public static final String WEBPAGE_PREFIX = "WEBPAGE_";

    // 统一权限（不区分层级）
    public static final String WEBPAGE_READ = "WEBPAGE_READ";
    public static final String WEBPAGE_CREATE = "WEBPAGE_CREATE";
    public static final String WEBPAGE_UPDATE = "WEBPAGE_UPDATE";
    public static final String WEBPAGE_DELETE = "WEBPAGE_DELETE";
    public static final String WEBPAGE_EXPORT = "WEBPAGE_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_WEBPAGE_READ = "hasAuthority('WEBPAGE_READ')";
    public static final String HAS_WEBPAGE_CREATE = "hasAuthority('WEBPAGE_CREATE')";
    public static final String HAS_WEBPAGE_UPDATE = "hasAuthority('WEBPAGE_UPDATE')";
    public static final String HAS_WEBPAGE_DELETE = "hasAuthority('WEBPAGE_DELETE')";
    public static final String HAS_WEBPAGE_EXPORT = "hasAuthority('WEBPAGE_EXPORT')";

}