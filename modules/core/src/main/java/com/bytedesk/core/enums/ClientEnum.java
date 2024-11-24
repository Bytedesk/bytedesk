/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-25 13:07:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 14:24:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.enums;

public enum ClientEnum {
    SYSTEM,
    SYSTEM_AUTO, // auto reply
    SYSTEM_BOT, // robot reply
    // 
    WEB,
    WEB_PC, // pc端
    WEB_H5, // h5端
    WEB_VISITOR, // 访客端
    WEB_ADMIN, // 管理端
    // 
    IOS,
    ANDROID,
    ELECTRON,
    LINUX,
    MACOS,
    WINDOWS,
    // 
    FLUTTER,
    FLUTTER_WEB,
    FLUTTER_ANDROID,
    FLUTTER_IOS,
    // 
    UNIAPP,
    UNIAPP_WEB,
    UNIAPP_ANDROID,
    UNIAPP_IOS,
    // 
    WECHAT,
    WECHAT_MINI,
    WECHAT_MP,
    WECHAT_WORK,
    WECHAT_KEFU,
    WECHAT_CHANNEL,
    ;

    // 根据字符串查找对应的枚举常量
    public static ClientEnum fromValue(String value) {
        for (ClientEnum type : ClientEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with value: " + value);
    }
}
