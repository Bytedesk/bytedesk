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
package com.bytedesk.kbase.llm_file;

import com.bytedesk.core.base.BasePermissions;

public class FilePermissions extends BasePermissions {

    // 模块前缀
    public static final String FILE_PREFIX = "FILE_";

    // 统一权限（不区分层级）
    public static final String FILE_READ = "FILE_READ";
    public static final String FILE_CREATE = "FILE_CREATE";
    public static final String FILE_UPDATE = "FILE_UPDATE";
    public static final String FILE_DELETE = "FILE_DELETE";
    public static final String FILE_EXPORT = "FILE_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_FILE_READ = "hasAuthority('FILE_READ')";
    public static final String HAS_FILE_CREATE = "hasAuthority('FILE_CREATE')";
    public static final String HAS_FILE_UPDATE = "hasAuthority('FILE_UPDATE')";
    public static final String HAS_FILE_DELETE = "hasAuthority('FILE_DELETE')";
    public static final String HAS_FILE_EXPORT = "hasAuthority('FILE_EXPORT')";

}