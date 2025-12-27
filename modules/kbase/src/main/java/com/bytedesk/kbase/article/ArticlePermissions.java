/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

import com.bytedesk.core.base.BasePermissions;

public class ArticlePermissions extends BasePermissions {

    // 模块前缀
    public static final String ARTICLE_PREFIX = "ARTICLE_";

    // 统一权限（不区分层级）
    public static final String ARTICLE_READ = "ARTICLE_READ";
    public static final String ARTICLE_CREATE = "ARTICLE_CREATE";
    public static final String ARTICLE_UPDATE = "ARTICLE_UPDATE";
    public static final String ARTICLE_DELETE = "ARTICLE_DELETE";
    public static final String ARTICLE_EXPORT = "ARTICLE_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_ARTICLE_READ = "hasAuthority('ARTICLE_READ')";
    public static final String HAS_ARTICLE_CREATE = "hasAuthority('ARTICLE_CREATE')";
    public static final String HAS_ARTICLE_UPDATE = "hasAuthority('ARTICLE_UPDATE')";
    public static final String HAS_ARTICLE_DELETE = "hasAuthority('ARTICLE_DELETE')";
    public static final String HAS_ARTICLE_EXPORT = "hasAuthority('ARTICLE_EXPORT')";

}