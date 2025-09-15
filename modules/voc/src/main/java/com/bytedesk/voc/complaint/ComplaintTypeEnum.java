/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-23 17:02:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 08:57:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.complaint;

/**
 * Complaint type enumeration
 * Defines different types of complaint sources and contexts
 */
public enum ComplaintTypeEnum {
    SERVICE("服务投诉"),
    PRODUCT("产品投诉"),
    AGENT("客服投诉"),
    WORKGROUP("技能组投诉"),
    SYSTEM("系统投诉"),
    BILLING("账单投诉"),
    TECHNICAL("技术投诉"),
    PRIVACY("隐私投诉"),
    GENERAL("通用投诉"),
    ;

    private final String description;

    ComplaintTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public static ComplaintTypeEnum fromString(String type) {
        try {
            return ComplaintTypeEnum.valueOf(type.toUpperCase());
        } catch (Exception e) {
            return GENERAL;
        }
    }
}
