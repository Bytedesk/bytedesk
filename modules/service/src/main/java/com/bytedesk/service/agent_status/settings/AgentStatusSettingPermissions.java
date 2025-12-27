/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 11:45:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status.settings;

import com.bytedesk.core.base.BasePermissions;

public class AgentStatusSettingPermissions extends BasePermissions {

    // 模块前缀
    public static final String AGENT_STATUS_SETTING_PREFIX = "AGENT_STATUS_SETTING_";

    // 统一权限（不区分层级）
    public static final String AGENT_STATUS_SETTING_READ = "AGENT_STATUS_SETTING_READ";
    public static final String AGENT_STATUS_SETTING_CREATE = "AGENT_STATUS_SETTING_CREATE";
    public static final String AGENT_STATUS_SETTING_UPDATE = "AGENT_STATUS_SETTING_UPDATE";
    public static final String AGENT_STATUS_SETTING_DELETE = "AGENT_STATUS_SETTING_DELETE";
    public static final String AGENT_STATUS_SETTING_EXPORT = "AGENT_STATUS_SETTING_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_AGENT_STATUS_SETTING_READ = "hasAuthority('AGENT_STATUS_SETTING_READ')";
    public static final String HAS_AGENT_STATUS_SETTING_CREATE = "hasAuthority('AGENT_STATUS_SETTING_CREATE')";
    public static final String HAS_AGENT_STATUS_SETTING_UPDATE = "hasAuthority('AGENT_STATUS_SETTING_UPDATE')";
    public static final String HAS_AGENT_STATUS_SETTING_DELETE = "hasAuthority('AGENT_STATUS_SETTING_DELETE')";
    public static final String HAS_AGENT_STATUS_SETTING_EXPORT = "hasAuthority('AGENT_STATUS_SETTING_EXPORT')";

}
