package com.bytedesk.ai.chat_memory;

import com.bytedesk.core.base.BasePermissions;

public class ChatMemoryPermissions extends BasePermissions {

    // 模块前缀
    public static final String CHAT_MEMORY_PREFIX = "CHAT_MEMORY_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "CHAT_MEMORY";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String CHAT_MEMORY_READ = CHAT_MEMORY_PREFIX + "READ";
    public static final String CHAT_MEMORY_CREATE = CHAT_MEMORY_PREFIX + "CREATE";
    public static final String CHAT_MEMORY_UPDATE = CHAT_MEMORY_PREFIX + "UPDATE";
    public static final String CHAT_MEMORY_DELETE = CHAT_MEMORY_PREFIX + "DELETE";
    public static final String CHAT_MEMORY_EXPORT = CHAT_MEMORY_PREFIX + "EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_CHAT_MEMORY_READ = "hasAuthority('" + CHAT_MEMORY_READ + "')";
    public static final String HAS_CHAT_MEMORY_CREATE = "hasAuthority('" + CHAT_MEMORY_CREATE + "')";
    public static final String HAS_CHAT_MEMORY_UPDATE = "hasAuthority('" + CHAT_MEMORY_UPDATE + "')";
    public static final String HAS_CHAT_MEMORY_DELETE = "hasAuthority('" + CHAT_MEMORY_DELETE + "')";
    public static final String HAS_CHAT_MEMORY_EXPORT = "hasAuthority('" + CHAT_MEMORY_EXPORT + "')";
}
