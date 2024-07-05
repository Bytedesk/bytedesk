/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-25 13:07:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-26 17:06:43
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
    SYSTEM("system"),
    SYSTEM_AUTO("system_auto"), // auto reply
    SYSTEM_BOT("system_bot"), // robot reply
    WEB("web"),
    H5("h5"),
    IOS("ios"),
    ANDROID("android"),
    ELECTRON("electron"),
    LINUX("linux"),
    MACOS("macos"),
    WINDOWS("windows"),
    FLUTTER("flutter"),
    FLUTTER_WEB("flutter_web"),
    FLUTTER_ANDROID("flutter_android"),
    FLUTTER_IOS("flutter_ios"),
    UNI("uni"),
    UNI_WEB("uni_web"),
    UNI_ANDROID("uni_android"),
    UNI_IOS("uni_ios"),
    WECHAT_MINI("wechat_mini"),
    WECHAT_MP("wechat_mp"),
    WECHAT_WORK("wechat_work"),
    WECHAT_KEFU("wechat_kefu"),
    ;
            
    private final String value;

    ClientEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 根据字符串查找对应的枚举常量
    public static ClientEnum fromValue(String value) {
        for (ClientEnum type : ClientEnum.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with value: " + value);
    }
}
