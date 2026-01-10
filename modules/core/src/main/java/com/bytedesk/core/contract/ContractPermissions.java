/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 11:55:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.contract;

import com.bytedesk.core.base.BasePermissions;

public class ContractPermissions extends BasePermissions {

    // 模块前缀
    public static final String CONTRACT_PREFIX = "CONTRACT_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "CONTRACT";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String CONTRACT_READ = "CONTRACT_READ";
    public static final String CONTRACT_CREATE = "CONTRACT_CREATE";
    public static final String CONTRACT_UPDATE = "CONTRACT_UPDATE";
    public static final String CONTRACT_DELETE = "CONTRACT_DELETE";
    public static final String CONTRACT_EXPORT = "CONTRACT_EXPORT";

    // 新 PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_CONTRACT_READ = "hasAuthority('" + CONTRACT_READ + "')";
    public static final String HAS_CONTRACT_CREATE = "hasAuthority('" + CONTRACT_CREATE + "')";
    public static final String HAS_CONTRACT_UPDATE = "hasAuthority('" + CONTRACT_UPDATE + "')";
    public static final String HAS_CONTRACT_DELETE = "hasAuthority('" + CONTRACT_DELETE + "')";
    public static final String HAS_CONTRACT_EXPORT = "hasAuthority('" + CONTRACT_EXPORT + "')";

}
