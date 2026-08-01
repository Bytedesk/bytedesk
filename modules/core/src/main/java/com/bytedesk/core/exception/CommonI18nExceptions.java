/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-04-22 18:45:00
 * @LastEditors: GitHub Copilot
 * @LastEditTime: 2026-04-22 18:45:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.exception;

import com.bytedesk.core.constant.I18Consts;

public final class CommonI18nExceptions {

    private CommonI18nExceptions() {
    }

    public static NotLoginException loginRequired() {
        return new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
    }

    public static ForbiddenException forbidden(String key, Object... args) {
        return new ForbiddenException(withArgs(key, args));
    }

    public static ExistsException exists(String key, Object... args) {
        return new ExistsException(withArgs(key, args));
    }

    public static NotFoundException notFound(String key, Object... args) {
        return new NotFoundException(withArgs(key, args));
    }

    public static RuntimeException createFailed() {
        return new RuntimeException(I18Consts.I18N_CREATE_FAILED);
    }

    public static RuntimeException updateFailed() {
        return new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
    }

    private static String withArgs(String key, Object... args) {
        return args == null || args.length == 0 ? key : I18Consts.withArgs(key, args);
    }
}