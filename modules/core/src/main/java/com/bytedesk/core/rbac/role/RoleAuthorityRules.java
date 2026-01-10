package com.bytedesk.core.rbac.role;

import java.util.Set;

import org.springframework.util.StringUtils;

/**
 * Centralized RBAC role/authority rules used by RoleInitializer,
 * RoleEventListener, and RoleRestService.
 */
public final class RoleAuthorityRules {

    private RoleAuthorityRules() {
    }

    // kbase read permission prefixes (modules/kbase)
    // Keep as literals to avoid core -> kbase module dependency.
    public static final String KBASE_PREFIX = "KBASE_";
    public static final String ARTICLE_PREFIX = "ARTICLE_";
    public static final String ARTICLE_ARCHIVE_PREFIX = "ARTICLE_ARCHIVE_";
    public static final String MATERIAL_PREFIX = "MATERIAL_";
    public static final String TABOO_PREFIX = "TABOO_";
    public static final String TABOO_MESSAGE_PREFIX = "TABOO_MESSAGE_";
    public static final String QUICK_REPLY_PREFIX = "QUICK_REPLY_";
    public static final String AUTO_REPLY_KEYWORD_PREFIX = "AUTO_REPLY_KEYWORD_";
    public static final String AUTO_REPLY_FIXED_PREFIX = "AUTO_REPLY_FIXED_";
    public static final String WEBSITE_PREFIX = "WEBSITE_";
    public static final String FILE_PREFIX = "FILE_";
    public static final String CHUNK_PREFIX = "CHUNK_";
    public static final String TEXT_PREFIX = "TEXT_";
    public static final String FAQ_PREFIX = "FAQ_";
    public static final String WEBPAGE_PREFIX = "WEBPAGE_";
    public static final String COMMENT_PREFIX = "COMMENT_";
    public static final String KBASE_STATISTIC_PREFIX = "KBASE_STATISTIC_"; // enterprise/kbase

    public static final Set<String> KBASE_READ_PREFIXES = Set.of(
            KBASE_PREFIX,
            ARTICLE_PREFIX,
            ARTICLE_ARCHIVE_PREFIX,
            MATERIAL_PREFIX,
            TABOO_PREFIX,
            TABOO_MESSAGE_PREFIX,
            QUICK_REPLY_PREFIX,
            AUTO_REPLY_KEYWORD_PREFIX,
            AUTO_REPLY_FIXED_PREFIX,
            WEBSITE_PREFIX,
            FILE_PREFIX,
            CHUNK_PREFIX,
            TEXT_PREFIX,
            FAQ_PREFIX,
            WEBPAGE_PREFIX,
            COMMENT_PREFIX,
            KBASE_STATISTIC_PREFIX);

    public static final String SETTINGS_CREATE = "SETTINGS_CREATE";
    public static final String SETTINGS_UPDATE = "SETTINGS_UPDATE";
    public static final String SETTINGS_READ = "SETTINGS_READ";
    public static final String SETTINGS_DELETE = "SETTINGS_DELETE";
    public static final String SETTINGS_EXPORT = "SETTINGS_EXPORT";

    // core (modules/core)
    // Keep as literals to avoid spreading string values across the codebase.
    public static final String USER_READ = "USER_READ";
    public static final String USER_UPDATE = "USER_UPDATE";

    public static final String MESSAGE_READ = "MESSAGE_READ";
    public static final String MESSAGE_CREATE = "MESSAGE_CREATE";

    public static final String THREAD_READ = "THREAD_READ";
    public static final String THREAD_CREATE = "THREAD_CREATE";
    public static final String THREAD_UPDATE = "THREAD_UPDATE";

    public static final String ORGANIZATION_CREATE = "ORGANIZATION_CREATE";
    public static final String ORGANIZATION_READ = "ORGANIZATION_READ";
    public static final String ORGANIZATION_UPDATE = "ORGANIZATION_UPDATE";

    public static final String MEMBER_READ = "MEMBER_READ";
    public static final String MEMBER_UPDATE = "MEMBER_UPDATE";

    public static final String GROUP_READ = "GROUP_READ";
    public static final String GROUP_CREATE = "GROUP_CREATE";
    public static final String GROUP_UPDATE = "GROUP_UPDATE";
    public static final String GROUP_DELETE = "GROUP_DELETE";

    public static final String DEPARTMENT_READ = "DEPARTMENT_READ";

    // ticket (modules/ticket)
    // Keep as literals to avoid core -> ticket module dependency.
    public static final String TICKET_READ = "TICKET_READ";
    public static final String TICKET_CREATE = "TICKET_CREATE";
    public static final String TICKET_UPDATE = "TICKET_UPDATE";
    public static final String TICKET_DELETE = "TICKET_DELETE";

    // ticket category (modules/ticket)
    // Keep as literals to avoid core -> ticket module dependency.
    public static final String CATEGORY_READ = "CATEGORY_READ";
    public static final String CATEGORY_CREATE = "CATEGORY_CREATE";
    public static final String CATEGORY_UPDATE = "CATEGORY_UPDATE";
    public static final String CATEGORY_DELETE = "CATEGORY_DELETE";

    // message_leave (modules/service)
    // Keep as literals to avoid core -> service module dependency.
    public static final String MESSAGE_LEAVE_READ = "MESSAGE_LEAVE_READ";
    public static final String MESSAGE_LEAVE_UPDATE = "MESSAGE_LEAVE_UPDATE";

    // message_rating (modules/service)
    // Keep as literals to avoid core -> service module dependency.
    public static final String MESSAGE_RATING_READ = "MESSAGE_RATING_READ";
    public static final String MESSAGE_RATING_UPDATE = "MESSAGE_RATING_UPDATE";

    // agent (modules/service)
    // Keep as literals to avoid core -> service module dependency.
    public static final String AGENT_READ = "AGENT_READ";
    public static final String AGENT_UPDATE = "AGENT_UPDATE";

    // agent_status (modules/service)
    // Keep as literals to avoid core -> service module dependency.
    public static final String AGENT_STATUS_READ = "AGENT_STATUS_READ";
    public static final String AGENT_STATUS_CREATE = "AGENT_STATUS_CREATE";

    // queue (modules/service)
    // Keep as literals to avoid core -> service module dependency.
    public static final String QUEUE_READ = "QUEUE_READ";

    // workgroup (modules/service)
    // Keep as literals to avoid core -> service module dependency.
    public static final String WORKGROUP_READ = "WORKGROUP_READ";

    // kbase (modules/kbase)
    // Keep as literals to avoid core -> kbase module dependency.
    public static final String KBASE_READ = "KBASE_READ";
    public static final String FAQ_READ = "FAQ_READ";
    public static final String QUICK_REPLY_CREATE = "QUICK_REPLY_CREATE";
    public static final String QUICK_REPLY_UPDATE = "QUICK_REPLY_UPDATE";
    public static final String QUICK_REPLY_DELETE = "QUICK_REPLY_DELETE";

    public static final Set<String> DEFAULT_ROLE_USER_AUTHORITY_VALUES = Set.of(
            USER_READ,
            USER_UPDATE,
            //
            MESSAGE_READ,
            MESSAGE_CREATE,
            //
            THREAD_READ,
            THREAD_CREATE,
            THREAD_UPDATE,
            
            // ticket category
            CATEGORY_READ,
            CATEGORY_CREATE,
            CATEGORY_UPDATE,
            CATEGORY_DELETE,
            //
            TICKET_READ,
            TICKET_CREATE,
            TICKET_UPDATE,
            TICKET_DELETE,
            //
            ORGANIZATION_CREATE,
            ORGANIZATION_READ,
            ORGANIZATION_UPDATE,
            // 
            DEPARTMENT_READ,
            //
            MEMBER_READ,
            MEMBER_UPDATE,
            //
            GROUP_READ,
            GROUP_CREATE,
            GROUP_UPDATE,
            GROUP_DELETE
    );

    /**
     * ROLE_AGENT: 额外默认权限（非知识库 read 前缀匹配范围内的权限）
     */
    public static final Set<String> DEFAULT_ROLE_AGENT_EXTRA_AUTHORITY_VALUES = Set.of(
            MESSAGE_LEAVE_READ,
            MESSAGE_LEAVE_UPDATE,
            MESSAGE_RATING_READ,
            MESSAGE_RATING_UPDATE,
            AGENT_READ,
            AGENT_UPDATE,
            AGENT_STATUS_READ,
            AGENT_STATUS_CREATE,
            QUEUE_READ,
            WORKGROUP_READ,

            // 快捷回复：ROLE_AGENT 默认需要增删改
            QUICK_REPLY_CREATE,
            QUICK_REPLY_UPDATE,
            QUICK_REPLY_DELETE);

    public static boolean isKbaseReadPermission(String permissionValue) {
        if (!StringUtils.hasText(permissionValue)) {
            return false;
        }
        if (!permissionValue.endsWith("_READ")) {
            return false;
        }
        return KBASE_READ_PREFIXES.stream().anyMatch(permissionValue::startsWith);
    }

    public static boolean isAdminExcludedPermission(String permissionValue) {
        return SETTINGS_CREATE.equals(permissionValue) || SETTINGS_UPDATE.equals(permissionValue)
                || SETTINGS_DELETE.equals(permissionValue);
    }
}
