/*
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com. All Rights Reserved. 
 */
package com.bytedesk.ticket.form;

import com.bytedesk.core.base.BasePermissions;

public class TicketFormPermissions extends BasePermissions {

    // 模块前缀
    public static final String TICKET_FORM_PREFIX = "TICKET_FORM_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "TICKET_FORM";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String TICKET_FORM_READ = "TICKET_FORM_READ";
    public static final String TICKET_FORM_CREATE = "TICKET_FORM_CREATE";
    public static final String TICKET_FORM_UPDATE = "TICKET_FORM_UPDATE";
    public static final String TICKET_FORM_DELETE = "TICKET_FORM_DELETE";
    public static final String TICKET_FORM_EXPORT = "TICKET_FORM_EXPORT";

    // PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_TICKET_FORM_READ = "hasAuthority('" + TICKET_FORM_READ + "')";
    public static final String HAS_TICKET_FORM_CREATE = "hasAuthority('" + TICKET_FORM_CREATE + "')";
    public static final String HAS_TICKET_FORM_UPDATE = "hasAuthority('" + TICKET_FORM_UPDATE + "')";
    public static final String HAS_TICKET_FORM_DELETE = "hasAuthority('" + TICKET_FORM_DELETE + "')";
    public static final String HAS_TICKET_FORM_EXPORT = "hasAuthority('" + TICKET_FORM_EXPORT + "')";

}
