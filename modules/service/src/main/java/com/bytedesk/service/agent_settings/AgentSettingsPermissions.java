package com.bytedesk.service.agent_settings;

import com.bytedesk.core.base.BasePermissions;

public class AgentSettingsPermissions extends BasePermissions {

    // 模块前缀
    public static final String AGENT_SETTINGS_PREFIX = "AGENT_SETTINGS_";

    // 统一权限（不区分层级）
    public static final String AGENT_SETTINGS_READ = "AGENT_SETTINGS_READ";
    public static final String AGENT_SETTINGS_CREATE = "AGENT_SETTINGS_CREATE";
    public static final String AGENT_SETTINGS_UPDATE = "AGENT_SETTINGS_UPDATE";
    public static final String AGENT_SETTINGS_DELETE = "AGENT_SETTINGS_DELETE";
    public static final String AGENT_SETTINGS_EXPORT = "AGENT_SETTINGS_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_AGENT_SETTINGS_READ = "hasAuthority('AGENT_SETTINGS_READ')";
    public static final String HAS_AGENT_SETTINGS_CREATE = "hasAuthority('AGENT_SETTINGS_CREATE')";
    public static final String HAS_AGENT_SETTINGS_UPDATE = "hasAuthority('AGENT_SETTINGS_UPDATE')";
    public static final String HAS_AGENT_SETTINGS_DELETE = "hasAuthority('AGENT_SETTINGS_DELETE')";
    public static final String HAS_AGENT_SETTINGS_EXPORT = "hasAuthority('AGENT_SETTINGS_EXPORT')";

}
