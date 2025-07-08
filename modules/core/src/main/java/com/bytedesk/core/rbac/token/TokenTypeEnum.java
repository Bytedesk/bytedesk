/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-16 10:19:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-08 09:57:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.token;

public enum TokenTypeEnum {
    // 登录token
    BEARER,
    // API访问token
    API,
    // 刷新token
    REFRESH,
    // 访问token
    ACCESS,
    // 第三方授权token（微信、QQ等）
    THIRD_PARTY,
    // 临时token
    TEMPORARY,
    // 设备token
    DEVICE,
    // 会话token
    SESSION,
    // 验证token（邮箱验证、手机验证等）
    VERIFICATION,
    // 重置密码token
    PASSWORD_RESET,
    // 邀请token
    INVITATION,
    // 单点登录token
    SSO,
    // 授权码token
    AUTHORIZATION_CODE,
    // 客户端凭证token
    CLIENT_CREDENTIALS,
    // 隐式授权token
    IMPLICIT,
    // 资源拥有者密码凭证token
    RESOURCE_OWNER_PASSWORD,
    // 自定义
    CUSTOM
}
