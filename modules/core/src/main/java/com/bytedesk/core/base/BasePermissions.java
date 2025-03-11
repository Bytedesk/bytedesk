package com.bytedesk.core.base;

public class BasePermissions {

    protected static final String HAS_AUTHORITY = "hasAuthority('%s')";
    protected static final String HAS_ANY_AUTHORITY = "hasAnyAuthority(%s)";

    protected static String formatAuthority(String authority) {
        return String.format(HAS_AUTHORITY, authority);
    }

    protected static String formatAnyAuthority(String... authorities) {
        return String.format(HAS_ANY_AUTHORITY, String.join(", ", authorities));
    }
}