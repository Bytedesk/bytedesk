/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-10 11:45:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-11 14:43:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.flow.model.block.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BlockType {
    TEXT("text"),
    TEXT_INPUT("text_input"),
    EMAIL_INPUT("email_input"),
    CHOICE_INPUT("choice input"),
    PICTURE_CHOICE_INPUT("picture_choice_input"),
    RATING_INPUT("rating_input"),
    NUMBER_INPUT("number_input"),
    CONDITION("condition"),
    WEBHOOK("webhook"),
    SCRIPT("script"),
    INTEGRATION("integration"),
    EMAIL("email"),
    CHAT("chat"),
    WAIT("wait"),
    REDIRECT("redirect"),
    FILE("file"),
    VIDEO("video"),
    AUDIO("audio"),
    IMAGE("image"),
    AB_TEST("ab_test"),
    PAYMENT_INPUT("payment_input"),
    SET_VARIABLE("set_variable"),
    TYPEBOT_LINK("typebot_link"),
    JUMP("jump"),
    OPENAI("openai"),
    MISTRAL("mistral");

    private final String value;

    BlockType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static BlockType fromValue(String value) {
        for (BlockType type : BlockType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown BlockType value: " + value);
    }
}
