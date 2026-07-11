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

public final class OrganizationI18nExceptions {

    private OrganizationI18nExceptions() {
    }

    public static ForbiddenException organizationAccessDenied() {
        return CommonI18nExceptions.forbidden(I18Consts.I18N_ORGANIZATION_ACCESS_DENIED);
    }

    public static ForbiddenException superAdminRequired() {
        return CommonI18nExceptions.forbidden(I18Consts.I18N_SUPER_ADMIN_REQUIRED);
    }

    public static ForbiddenException superUserOrganizationDisableDenied() {
        return CommonI18nExceptions.forbidden(I18Consts.I18N_ORGANIZATION_SUPER_USER_DISABLE_DENIED);
    }

    public static ForbiddenException defaultOrganizationDisableDenied() {
        return CommonI18nExceptions.forbidden(I18Consts.I18N_ORGANIZATION_DEFAULT_DISABLE_DENIED);
    }

    public static ForbiddenException defaultOrganizationDeleteDenied() {
        return CommonI18nExceptions.forbidden(I18Consts.I18N_ORGANIZATION_DEFAULT_DELETE_DENIED);
    }

    public static ForbiddenException superUserDisableDenied() {
        return CommonI18nExceptions.forbidden(I18Consts.I18N_USER_SUPER_DISABLE_DENIED);
    }

    public static ExistsException organizationNameExists(String name) {
        return CommonI18nExceptions.exists(I18Consts.I18N_ORGANIZATION_NAME_EXISTS, name);
    }

    public static ExistsException organizationCodeExists(String code) {
        return CommonI18nExceptions.exists(I18Consts.I18N_ORGANIZATION_CODE_EXISTS, code);
    }

    public static ExistsException organizationCreateConstraintFailed() {
        return CommonI18nExceptions.exists(I18Consts.I18N_ORGANIZATION_CREATE_CONSTRAINT_FAILED);
    }

    public static NotFoundException organizationNotFound(String uid) {
        return CommonI18nExceptions.notFound(I18Consts.I18N_ORGANIZATION_NOT_FOUND_WITH_UID, uid);
    }
}