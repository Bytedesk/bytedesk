/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 21:42:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo_message;

public class TabooMessagePermissions {

    public static final String TABOO_MESSAGE_PREFIX = "TABOO_MESSAGE_";

    // 统一权限（不区分层级）
    public static final String TABOO_MESSAGE_READ = "TABOO_MESSAGE_READ";
    public static final String TABOO_MESSAGE_CREATE = "TABOO_MESSAGE_CREATE";
    public static final String TABOO_MESSAGE_UPDATE = "TABOO_MESSAGE_UPDATE";
    public static final String TABOO_MESSAGE_DELETE = "TABOO_MESSAGE_DELETE";
    public static final String TABOO_MESSAGE_EXPORT = "TABOO_MESSAGE_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_TABOO_MESSAGE_READ = "hasAuthority('TABOO_MESSAGE_READ')";
    public static final String HAS_TABOO_MESSAGE_CREATE = "hasAuthority('TABOO_MESSAGE_CREATE')";
    public static final String HAS_TABOO_MESSAGE_UPDATE = "hasAuthority('TABOO_MESSAGE_UPDATE')";
    public static final String HAS_TABOO_MESSAGE_DELETE = "hasAuthority('TABOO_MESSAGE_DELETE')";
    public static final String HAS_TABOO_MESSAGE_EXPORT = "hasAuthority('TABOO_MESSAGE_EXPORT')";

}