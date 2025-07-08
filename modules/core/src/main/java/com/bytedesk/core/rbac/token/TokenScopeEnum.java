/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-08 09:12:06
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-08 09:12:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.token;

public enum TokenScopeEnum {
    // 登录权限
    LOGIN,
    // 读权限
    READ,
    // 写权限
    WRITE,
    // 管理员权限
    ADMIN,
    // API访问权限
    API,
    // 用户管理权限
    USER_MANAGEMENT,
    // 文件上传权限
    FILE_UPLOAD,
    // 消息发送权限
    MESSAGE_SEND,
    // 消息读取权限
    MESSAGE_READ,
    // 系统配置权限
    SYSTEM_CONFIG,
    // 报表查看权限
    REPORT_VIEW,
    // 数据导出权限
    DATA_EXPORT,
    // 第三方集成权限
    THIRD_PARTY_INTEGRATION,
    // 客服权限
    CUSTOMER_SERVICE,
    // 访客权限
    VISITOR,
    // 组织管理权限
    ORGANIZATION_MANAGEMENT
}
