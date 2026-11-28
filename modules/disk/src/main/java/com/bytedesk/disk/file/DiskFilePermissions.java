/*
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com. All Rights Reserved. 
 */
package com.bytedesk.disk.file;

import com.bytedesk.core.base.BasePermissions;

public class DiskFilePermissions extends BasePermissions {

    // 模块前缀
    public static final String DISK_FILE_PREFIX = "DISK_FILE_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "DISK_FILE";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String DISK_FILE_READ = "DISK_FILE_READ";
    public static final String DISK_FILE_CREATE = "DISK_FILE_CREATE";
    public static final String DISK_FILE_UPDATE = "DISK_FILE_UPDATE";
    public static final String DISK_FILE_DELETE = "DISK_FILE_DELETE";
    public static final String DISK_FILE_EXPORT = "DISK_FILE_EXPORT";

    // PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_DISK_FILE_READ = "hasAuthority('" + DISK_FILE_READ + "')";
    public static final String HAS_DISK_FILE_CREATE = "hasAuthority('" + DISK_FILE_CREATE + "')";
    public static final String HAS_DISK_FILE_UPDATE = "hasAuthority('" + DISK_FILE_UPDATE + "')";
    public static final String HAS_DISK_FILE_DELETE = "hasAuthority('" + DISK_FILE_DELETE + "')";
    public static final String HAS_DISK_FILE_EXPORT = "hasAuthority('" + DISK_FILE_EXPORT + "')";

}
