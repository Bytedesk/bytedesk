/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-12 17:18:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-28 12:42:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth;

// 验证类型
public enum AuthTypeEnum {
    MOBILE_REGISTER, // 手机号注册
    MOBILE_LOGIN, // 手机号登录
    MOBILE_RESET, // 手机号重置
    MOBILE_VERIFY, // 手机号验证
    // 
    EMAIL_REGISTER, // 邮箱注册
    EMAIL_LOGIN, // 邮箱登录
    EMAIL_RESET, // 邮箱重置
    EMAIL_VERIFY, // 邮箱验证
    // 
    USERNAME_REGISTER, // 用户名注册
    USERNAME_LOGIN, // 用户名登录
    USERNAME_RESET, // 用户名重置
    USERNAME_VERIFY, // 用户名验证
    // 
    SCAN_LOGIN, // 扫码登录
}
