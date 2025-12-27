package com.bytedesk.service.agent_status;

import com.bytedesk.core.base.BasePermissions;

public class AgentStatusPermissions extends BasePermissions {

    // 模块前缀
    public static final String AGENT_STATUS_PREFIX = "AGENT_STATUS_";

    // 统一权限（不区分层级）
    public static final String AGENT_STATUS_READ = "AGENT_STATUS_READ";
    public static final String AGENT_STATUS_CREATE = "AGENT_STATUS_CREATE";
    public static final String AGENT_STATUS_UPDATE = "AGENT_STATUS_UPDATE";
    public static final String AGENT_STATUS_DELETE = "AGENT_STATUS_DELETE";
    public static final String AGENT_STATUS_EXPORT = "AGENT_STATUS_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_AGENT_STATUS_READ = "hasAuthority('AGENT_STATUS_READ')";
    public static final String HAS_AGENT_STATUS_CREATE = "hasAuthority('AGENT_STATUS_CREATE')";
    public static final String HAS_AGENT_STATUS_UPDATE = "hasAuthority('AGENT_STATUS_UPDATE')";
    public static final String HAS_AGENT_STATUS_DELETE = "hasAuthority('AGENT_STATUS_DELETE')";
    public static final String HAS_AGENT_STATUS_EXPORT = "hasAuthority('AGENT_STATUS_EXPORT')";

}
