package com.bytedesk.disk.permission;

import com.bytedesk.core.base.BasePermissions;

/**
 * 网盘模块权限定义
 */
public class DiskPermissions extends BasePermissions {

    public static final String DISK_PREFIX = "DISK_";

    public static final String MODULE_NAME = "DISK";

    public static final String DISK_READ = DISK_PREFIX + "READ";
    public static final String DISK_CREATE = DISK_PREFIX + "CREATE";
    public static final String DISK_UPDATE = DISK_PREFIX + "UPDATE";
    public static final String DISK_DELETE = DISK_PREFIX + "DELETE";
    public static final String DISK_EXPORT = DISK_PREFIX + "EXPORT";

    public static final String HAS_DISK_READ = "hasAuthority('" + DISK_READ + "')";
    public static final String HAS_DISK_CREATE = "hasAuthority('" + DISK_CREATE + "')";
    public static final String HAS_DISK_UPDATE = "hasAuthority('" + DISK_UPDATE + "')";
    public static final String HAS_DISK_DELETE = "hasAuthority('" + DISK_DELETE + "')";
    public static final String HAS_DISK_EXPORT = "hasAuthority('" + DISK_EXPORT + "')";
}
