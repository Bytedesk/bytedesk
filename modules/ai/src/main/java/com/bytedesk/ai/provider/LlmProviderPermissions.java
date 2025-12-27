package com.bytedesk.ai.provider;

import com.bytedesk.core.base.BasePermissions;

public class LlmProviderPermissions extends BasePermissions {

    // 模块前缀
    public static final String LLM_PROVIDER_PREFIX = "LLM_PROVIDER_";

    // 统一权限（不区分层级）
    public static final String LLM_PROVIDER_READ = "LLM_PROVIDER_READ";
    public static final String LLM_PROVIDER_CREATE = "LLM_PROVIDER_CREATE";
    public static final String LLM_PROVIDER_UPDATE = "LLM_PROVIDER_UPDATE";
    public static final String LLM_PROVIDER_DELETE = "LLM_PROVIDER_DELETE";
    public static final String LLM_PROVIDER_EXPORT = "LLM_PROVIDER_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_LLM_PROVIDER_READ = "hasAuthority('LLM_PROVIDER_READ')";
    public static final String HAS_LLM_PROVIDER_CREATE = "hasAuthority('LLM_PROVIDER_CREATE')";
    public static final String HAS_LLM_PROVIDER_UPDATE = "hasAuthority('LLM_PROVIDER_UPDATE')";
    public static final String HAS_LLM_PROVIDER_DELETE = "hasAuthority('LLM_PROVIDER_DELETE')";
    public static final String HAS_LLM_PROVIDER_EXPORT = "hasAuthority('LLM_PROVIDER_EXPORT')";

}
