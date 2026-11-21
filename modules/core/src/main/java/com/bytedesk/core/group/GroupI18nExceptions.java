/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *   contact: 270580156@qq.com 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group;

import com.bytedesk.core.exception.ForbiddenException;

/**
 * 群组领域异常（消息用 i18n key，由全局异常处理解析）
 */
public final class GroupI18nExceptions {

    private GroupI18nExceptions() {
    }

    public static ForbiddenException forbiddenAdminRequired() {
        return new ForbiddenException("group.admin.required");
    }

    public static ForbiddenException forbiddenJoinPrivate() {
        return new ForbiddenException("group.join.private.forbidden");
    }

    public static ForbiddenException forbiddenApply() {
        return new ForbiddenException("group.apply.forbidden");
    }

    public static RuntimeException groupFull() {
        return new RuntimeException("group.members.exceeded");
    }

    public static RuntimeException conflictApplyState() {
        return new RuntimeException("group.apply.state.conflict");
    }
}
