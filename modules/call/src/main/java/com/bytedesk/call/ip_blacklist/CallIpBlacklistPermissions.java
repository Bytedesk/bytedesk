package com.bytedesk.call.ip_blacklist;

import com.bytedesk.core.base.BasePermissions;

public class CallIpBlacklistPermissions extends BasePermissions {

    public static final String CALL_IP_BLACKLIST_PREFIX = "CALL_IP_BLACKLIST_";

    public static final String MODULE_NAME = "CALL_IP_BLACKLIST";

    public static final String CALL_IP_BLACKLIST_READ = "CALL_IP_BLACKLIST_READ";
    public static final String CALL_IP_BLACKLIST_CREATE = "CALL_IP_BLACKLIST_CREATE";
    public static final String CALL_IP_BLACKLIST_UPDATE = "CALL_IP_BLACKLIST_UPDATE";
    public static final String CALL_IP_BLACKLIST_DELETE = "CALL_IP_BLACKLIST_DELETE";
    public static final String CALL_IP_BLACKLIST_EXPORT = "CALL_IP_BLACKLIST_EXPORT";

    public static final String HAS_CALL_IP_BLACKLIST_READ = "hasAuthority('" + CALL_IP_BLACKLIST_READ + "')";
    public static final String HAS_CALL_IP_BLACKLIST_CREATE = "hasAuthority('" + CALL_IP_BLACKLIST_CREATE + "')";
    public static final String HAS_CALL_IP_BLACKLIST_UPDATE = "hasAuthority('" + CALL_IP_BLACKLIST_UPDATE + "')";
    public static final String HAS_CALL_IP_BLACKLIST_DELETE = "hasAuthority('" + CALL_IP_BLACKLIST_DELETE + "')";
    public static final String HAS_CALL_IP_BLACKLIST_EXPORT = "hasAuthority('" + CALL_IP_BLACKLIST_EXPORT + "')";
}