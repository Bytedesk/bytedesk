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

public final class ResourceI18nExceptions {

    private ResourceI18nExceptions() {
    }

    public static NotFoundException userNotFound() {
        return CommonI18nExceptions.notFound(I18Consts.I18N_USER_NOT_FOUND);
    }

    public static NotFoundException memberNotFound() {
        return CommonI18nExceptions.notFound(I18Consts.I18N_MEMBER_NOT_FOUND);
    }

    public static NotFoundException threadNotFound() {
        return CommonI18nExceptions.notFound(I18Consts.I18N_THREAD_NOT_FOUND);
    }

    public static NotFoundException threadNotFound(String uid) {
        return CommonI18nExceptions.notFound(I18Consts.I18N_THREAD_NOT_FOUND_WITH_UID, uid);
    }

    public static NotFoundException messageNotFound() {
        return CommonI18nExceptions.notFound(I18Consts.I18N_MESSAGE_NOT_FOUND);
    }
}