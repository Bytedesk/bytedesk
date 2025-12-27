/*
 * @Author: jackning 270580156@qq.com
 * @Description: Token permissions
 */
package com.bytedesk.core.rbac.token;

import com.bytedesk.core.base.BasePermissions;

public class TokenPermissions extends BasePermissions {

    public static final String TOKEN_PREFIX = "TOKEN_";

    public static final String TOKEN_READ = "TOKEN_READ";
    public static final String TOKEN_CREATE = "TOKEN_CREATE";
    public static final String TOKEN_UPDATE = "TOKEN_UPDATE";
    public static final String TOKEN_DELETE = "TOKEN_DELETE";
    public static final String TOKEN_EXPORT = "TOKEN_EXPORT";

    public static final String HAS_TOKEN_READ = "hasAuthority('TOKEN_READ')";
    public static final String HAS_TOKEN_CREATE = "hasAuthority('TOKEN_CREATE')";
    public static final String HAS_TOKEN_UPDATE = "hasAuthority('TOKEN_UPDATE')";
    public static final String HAS_TOKEN_DELETE = "hasAuthority('TOKEN_DELETE')";
    public static final String HAS_TOKEN_EXPORT = "hasAuthority('TOKEN_EXPORT')";

}
