/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-01 09:22:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-06 13:56:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.organization;

import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterLinkSave;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkSave;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.bytedesk.core.auth.AuthService;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * https://docs.spring.io/spring-data/rest/reference/events.html
 */
@Slf4j
@Component
@AllArgsConstructor
@RepositoryEventHandler(Organization.class)
public class OrganizationEventHandler {

    private AuthService authService;

    private UserService userService;

    @HandleBeforeCreate
    public void beforeCreate(Organization organization) {
        log.debug("beforeCreate");
        User user = authService.getCurrentUser();
        organization.setOid(Utils.getUid());
        organization.setUser(user);
        //
        user.getOrganizations().add(organization.getOid());
        userService.save(user);
    }

    @HandleAfterCreate
    public void afterCreate(Organization organization) {
        log.debug("afterCreate");

    }

    @HandleBeforeSave
    public void beforeSave(Organization organization) {
        log.debug("beforeSave");

    }

    @HandleAfterSave
    public void afterSave(Organization organization) {
        log.debug("afterSave");

    }

    @HandleBeforeLinkSave
    public void beforeLinkSave(Organization organization) {
        log.debug("beforeLinkSave");

    }

    @HandleAfterLinkSave
    public void afterLinkSave(Organization organization) {
        log.debug("afterLinkSave");

    }

    @HandleBeforeDelete
    public void beforeDelete(Organization organization) {
        log.debug("beforeDelete");

    }

    @HandleAfterDelete
    public void afterDelete(Organization organization) {
        log.debug("afterDelete");

    }

}
