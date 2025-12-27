package com.bytedesk.service.workgroup_settings;

import com.bytedesk.core.base.BasePermissions;

public class WorkgroupSettingsPermissions extends BasePermissions {

    // 模块前缀
    public static final String WORKGROUP_SETTINGS_PREFIX = "WORKGROUP_SETTINGS_";

    // 统一权限（不区分层级）
    public static final String WORKGROUP_SETTINGS_READ = "WORKGROUP_SETTINGS_READ";
    public static final String WORKGROUP_SETTINGS_CREATE = "WORKGROUP_SETTINGS_CREATE";
    public static final String WORKGROUP_SETTINGS_UPDATE = "WORKGROUP_SETTINGS_UPDATE";
    public static final String WORKGROUP_SETTINGS_DELETE = "WORKGROUP_SETTINGS_DELETE";
    public static final String WORKGROUP_SETTINGS_EXPORT = "WORKGROUP_SETTINGS_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_WORKGROUP_SETTINGS_READ = "hasAuthority('WORKGROUP_SETTINGS_READ')";
    public static final String HAS_WORKGROUP_SETTINGS_CREATE = "hasAuthority('WORKGROUP_SETTINGS_CREATE')";
    public static final String HAS_WORKGROUP_SETTINGS_UPDATE = "hasAuthority('WORKGROUP_SETTINGS_UPDATE')";
    public static final String HAS_WORKGROUP_SETTINGS_DELETE = "hasAuthority('WORKGROUP_SETTINGS_DELETE')";
    public static final String HAS_WORKGROUP_SETTINGS_EXPORT = "hasAuthority('WORKGROUP_SETTINGS_EXPORT')";

}
