/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-11
 * @Description: Permission constants for quick buttons
 */
package com.bytedesk.kbase.quick_button;

import com.bytedesk.core.base.BasePermissions;

public class QuickButtonPermissions extends BasePermissions {

    public static final String QUICK_BUTTON_PREFIX = "QUICK_BUTTON_";

    // 统一权限（不区分层级）
    public static final String QUICK_BUTTON_READ = "QUICK_BUTTON_READ";
    public static final String QUICK_BUTTON_CREATE = "QUICK_BUTTON_CREATE";
    public static final String QUICK_BUTTON_UPDATE = "QUICK_BUTTON_UPDATE";
    public static final String QUICK_BUTTON_DELETE = "QUICK_BUTTON_DELETE";
    public static final String QUICK_BUTTON_EXPORT = "QUICK_BUTTON_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_QUICK_BUTTON_READ = "hasAuthority('QUICK_BUTTON_READ')";
    public static final String HAS_QUICK_BUTTON_CREATE = "hasAuthority('QUICK_BUTTON_CREATE')";
    public static final String HAS_QUICK_BUTTON_UPDATE = "hasAuthority('QUICK_BUTTON_UPDATE')";
    public static final String HAS_QUICK_BUTTON_DELETE = "hasAuthority('QUICK_BUTTON_DELETE')";
    public static final String HAS_QUICK_BUTTON_EXPORT = "hasAuthority('QUICK_BUTTON_EXPORT')";
}
