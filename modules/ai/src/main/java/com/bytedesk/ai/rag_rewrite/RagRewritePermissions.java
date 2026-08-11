package com.bytedesk.ai.rag_rewrite;

import com.bytedesk.core.base.BasePermissions;

public class RagRewritePermissions extends BasePermissions {

    public static final String MODULE_NAME = "RAG_REWRITE";
    public static final String RAG_REWRITE_PREFIX = "RAG_REWRITE_";

    public static final String RAG_REWRITE_READ = RAG_REWRITE_PREFIX + "READ";
    public static final String RAG_REWRITE_CREATE = RAG_REWRITE_PREFIX + "CREATE";
    public static final String RAG_REWRITE_UPDATE = RAG_REWRITE_PREFIX + "UPDATE";
    public static final String RAG_REWRITE_DELETE = RAG_REWRITE_PREFIX + "DELETE";
    public static final String RAG_REWRITE_EXPORT = RAG_REWRITE_PREFIX + "EXPORT";

    public static final String HAS_RAG_REWRITE_READ = "hasAuthority('" + RAG_REWRITE_READ + "')";
    public static final String HAS_RAG_REWRITE_CREATE = "hasAuthority('" + RAG_REWRITE_CREATE + "')";
    public static final String HAS_RAG_REWRITE_UPDATE = "hasAuthority('" + RAG_REWRITE_UPDATE + "')";
    public static final String HAS_RAG_REWRITE_DELETE = "hasAuthority('" + RAG_REWRITE_DELETE + "')";
    public static final String HAS_RAG_REWRITE_EXPORT = "hasAuthority('" + RAG_REWRITE_EXPORT + "')";
}
