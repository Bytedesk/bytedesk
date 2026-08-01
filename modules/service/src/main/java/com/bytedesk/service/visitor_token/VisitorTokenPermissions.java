package com.bytedesk.service.visitor_token;

import com.bytedesk.core.base.BasePermissions;

public class VisitorTokenPermissions extends BasePermissions {

    public static final String VISITOR_TOKEN_PREFIX = "VISITOR_TOKEN_";

    public static final String VISITOR_TOKEN_READ = "VISITOR_TOKEN_READ";
    public static final String VISITOR_TOKEN_CREATE = "VISITOR_TOKEN_CREATE";
    public static final String VISITOR_TOKEN_UPDATE = "VISITOR_TOKEN_UPDATE";
    public static final String VISITOR_TOKEN_DELETE = "VISITOR_TOKEN_DELETE";
    public static final String VISITOR_TOKEN_EXPORT = "VISITOR_TOKEN_EXPORT";

    public static final String HAS_VISITOR_TOKEN_READ = "hasAuthority('VISITOR_TOKEN_READ')";
    public static final String HAS_VISITOR_TOKEN_CREATE = "hasAuthority('VISITOR_TOKEN_CREATE')";
    public static final String HAS_VISITOR_TOKEN_UPDATE = "hasAuthority('VISITOR_TOKEN_UPDATE')";
    public static final String HAS_VISITOR_TOKEN_DELETE = "hasAuthority('VISITOR_TOKEN_DELETE')";
    public static final String HAS_VISITOR_TOKEN_EXPORT = "hasAuthority('VISITOR_TOKEN_EXPORT')";

}