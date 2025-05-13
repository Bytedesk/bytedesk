/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-19 11:54:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-09 08:56:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

public enum MessageLeaveStatusEnum {
    PENDING("待处理"),
    PROCESSING("处理中"),
    READ("已读"),
    REPLIED("已回复"),
    TRANSFERRED("已转接"),
    ESCALATED("已升级"),
    CLOSED("已关闭"),
    SPAM("垃圾留言"),
    INVALID("无效留言"),
    CONFIRMED("已确认"), // 访客确认留言有效
    REJECTED("已拒绝"), // 访客拒绝留言有效
    ;

    private final String description;

    MessageLeaveStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public static MessageLeaveStatusEnum fromString(String status) {
        try {
            return MessageLeaveStatusEnum.valueOf(status.toUpperCase());
        } catch (Exception e) {
            return PENDING;
        }
    }
}
