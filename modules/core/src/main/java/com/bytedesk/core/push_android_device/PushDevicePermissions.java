/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-22 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-22 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms of the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push_android_device;

/**
 * 设备推送绑定权限定义
 *
 * <p>仅超级管理员/平台级使用（推送设备对账为平台运维视角数据），
 * 由 PushDeviceInitializer 通过 authorityRestService.createForPlatform 注册。
 */
public class PushDevicePermissions {

    // 模块前缀
    public static final String PUSH_DEVICE_PREFIX = "PUSH_DEVICE_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "PUSH_DEVICE";

    // 统一权限（不在权限字符串中编码层级）
    public static final String PUSH_DEVICE_READ = "PUSH_DEVICE_READ";
    public static final String PUSH_DEVICE_CREATE = "PUSH_DEVICE_CREATE";
    public static final String PUSH_DEVICE_UPDATE = "PUSH_DEVICE_UPDATE";
    public static final String PUSH_DEVICE_DELETE = "PUSH_DEVICE_DELETE";
    public static final String PUSH_DEVICE_EXPORT = "PUSH_DEVICE_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_PUSH_DEVICE_READ = "hasAuthority('" + PUSH_DEVICE_READ + "')";
    public static final String HAS_PUSH_DEVICE_CREATE = "hasAuthority('" + PUSH_DEVICE_CREATE + "')";
    public static final String HAS_PUSH_DEVICE_UPDATE = "hasAuthority('" + PUSH_DEVICE_UPDATE + "')";
    public static final String HAS_PUSH_DEVICE_DELETE = "hasAuthority('" + PUSH_DEVICE_DELETE + "')";
    public static final String HAS_PUSH_DEVICE_EXPORT = "hasAuthority('" + PUSH_DEVICE_EXPORT + "')";
}
