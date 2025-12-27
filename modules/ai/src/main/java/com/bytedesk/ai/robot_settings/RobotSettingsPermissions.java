package com.bytedesk.ai.robot_settings;

import com.bytedesk.core.base.BasePermissions;

public class RobotSettingsPermissions extends BasePermissions {

    // 模块前缀
    public static final String ROBOT_SETTINGS_PREFIX = "ROBOT_SETTINGS_";

    // 统一权限（不区分层级）
    public static final String ROBOT_SETTINGS_READ = "ROBOT_SETTINGS_READ";
    public static final String ROBOT_SETTINGS_CREATE = "ROBOT_SETTINGS_CREATE";
    public static final String ROBOT_SETTINGS_UPDATE = "ROBOT_SETTINGS_UPDATE";
    public static final String ROBOT_SETTINGS_DELETE = "ROBOT_SETTINGS_DELETE";
    public static final String ROBOT_SETTINGS_EXPORT = "ROBOT_SETTINGS_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_ROBOT_SETTINGS_READ = "hasAuthority('ROBOT_SETTINGS_READ')";
    public static final String HAS_ROBOT_SETTINGS_CREATE = "hasAuthority('ROBOT_SETTINGS_CREATE')";
    public static final String HAS_ROBOT_SETTINGS_UPDATE = "hasAuthority('ROBOT_SETTINGS_UPDATE')";
    public static final String HAS_ROBOT_SETTINGS_DELETE = "hasAuthority('ROBOT_SETTINGS_DELETE')";
    public static final String HAS_ROBOT_SETTINGS_EXPORT = "hasAuthority('ROBOT_SETTINGS_EXPORT')";

}
