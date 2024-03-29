/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-01 17:07:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-01 22:51:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.department;

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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
@RepositoryEventHandler(Department.class)
public class DepartmentEventHandler {

    @HandleBeforeCreate
    public void beforeCreate(Department department) throws Exception {
        log.debug("beforeCreate:", department.toString());
    }

    @HandleAfterCreate
    public void afterCreate(Department department) {
        log.debug("afterCreate");

    }

    @HandleBeforeSave
    public void beforeSave(Department department) {
        log.debug("beforeSave");

    }

    @HandleAfterSave
    public void afterSave(Department department) {
        log.debug("afterSave");

    }

    @HandleBeforeLinkSave
    public void beforeLinkSave(Department department) {
        log.debug("beforeLinkSave");

    }

    @HandleAfterLinkSave
    public void afterLinkSave(Department department) {
        log.debug("afterLinkSave");

    }

    @HandleBeforeDelete
    public void beforeDelete(Department department) {
        log.debug("beforeDelete");

    }

    @HandleAfterDelete
    public void afterDelete(Department department) {
        log.debug("afterDelete");

    }

}
