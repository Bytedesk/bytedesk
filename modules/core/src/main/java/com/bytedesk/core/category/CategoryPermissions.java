package com.bytedesk.core.category;

public class CategoryPermissions {

    public static final String CATEGORY_PREFIX = "CATEGORY_";
    // Category permissions
    public static final String CATEGORY_CREATE = "hasAuthority('CATEGORY_CREATE')";
    public static final String CATEGORY_READ = "hasAuthority('CATEGORY_READ')";
    public static final String CATEGORY_UPDATE = "hasAuthority('CATEGORY_UPDATE')";
    public static final String CATEGORY_DELETE = "hasAuthority('CATEGORY_DELETE')";
    public static final String CATEGORY_EXPORT = "hasAuthority('CATEGORY_EXPORT')";
}