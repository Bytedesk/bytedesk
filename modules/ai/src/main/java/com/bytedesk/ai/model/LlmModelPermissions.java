package com.bytedesk.ai.model;

import com.bytedesk.core.base.BasePermissions;

public class LlmModelPermissions extends BasePermissions {

    // 模块前缀
    public static final String LLM_MODEL_PREFIX = "LLM_MODEL_";

    // 统一权限（不区分层级）
    public static final String LLM_MODEL_READ = "LLM_MODEL_READ";
    public static final String LLM_MODEL_CREATE = "LLM_MODEL_CREATE";
    public static final String LLM_MODEL_UPDATE = "LLM_MODEL_UPDATE";
    public static final String LLM_MODEL_DELETE = "LLM_MODEL_DELETE";
    public static final String LLM_MODEL_EXPORT = "LLM_MODEL_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_LLM_MODEL_READ = "hasAuthority('LLM_MODEL_READ')";
    public static final String HAS_LLM_MODEL_CREATE = "hasAuthority('LLM_MODEL_CREATE')";
    public static final String HAS_LLM_MODEL_UPDATE = "hasAuthority('LLM_MODEL_UPDATE')";
    public static final String HAS_LLM_MODEL_DELETE = "hasAuthority('LLM_MODEL_DELETE')";
    public static final String HAS_LLM_MODEL_EXPORT = "hasAuthority('LLM_MODEL_EXPORT')";

}
