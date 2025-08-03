/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-27 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-03 09:42:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

/**
 * Message leave type enumeration
 * Defines different types of leave messages for categorization and routing
 */
public enum MessageLeaveTypeEnum {
    CONSULTATION("consultation", "咨询"),
    COMPLAINT("complaint", "投诉"),
    SUGGESTION("suggestion", "建议"),
    FEEDBACK("feedback", "反馈"),
    COOPERATION("cooperation", "合作"),
    RECRUITMENT("recruitment", "招聘"),
    TECHNICAL_SUPPORT("technical_support", "技术支持"),
    SALES_INQUIRY("sales_inquiry", "销售咨询"),
    BILLING("billing", "账单问题"),
    ACCOUNT("account", "账户问题"),
    FEATURE_REQUEST("feature_request", "功能需求"),
    BUG_REPORT("bug_report", "问题报告"),
    GENERAL("general", "一般留言"),
    OTHER("other", "其他");

    private String code;
    private String name;

    MessageLeaveTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * Get enum by code
     * @param code the code to search for
     * @return the matching enum or null if not found
     */
    public static MessageLeaveTypeEnum fromCode(String code) {
        for (MessageLeaveTypeEnum type : MessageLeaveTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Get enum by name
     * @param name the name to search for
     * @return the matching enum or null if not found
     */
    public static MessageLeaveTypeEnum fromName(String name) {
        for (MessageLeaveTypeEnum type : MessageLeaveTypeEnum.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Check if the given code is valid
     * @param code the code to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidCode(String code) {
        return fromCode(code) != null;
    }

    /**
     * Get default type
     * @return the default type (GENERAL)
     */
    public static MessageLeaveTypeEnum getDefault() {
        return GENERAL;
    }
} 