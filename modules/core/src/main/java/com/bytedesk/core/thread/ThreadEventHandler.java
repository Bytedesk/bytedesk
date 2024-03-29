/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 10:40:06
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-22 11:11:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

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
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * https://spring.io/guides/tutorials/react-and-spring-data-rest/
 * https://docs.spring.io/spring-data/rest/reference/events.html
 */
@Slf4j
@Component
@AllArgsConstructor
@RepositoryEventHandler(Thread.class)
public class ThreadEventHandler {

    private AuthService authService;

    @HandleBeforeCreate
    public void beforeCreate(Thread thread) {
        log.debug("beforeCreate");
        //
        thread.setTid(Utils.getUid());
        thread.setUser(authService.getCurrentUser());

        return;
    }

    @HandleAfterCreate
    public void afterCreate(Thread thread) {
        log.debug("afterCreate");

    }

    @HandleBeforeSave
    public void beforeSave(Thread thread) {
        log.debug("beforeSave");

    }

    @HandleAfterSave
    public void afterSave(Thread thread) {
        log.debug("afterSave");

    }

    @HandleBeforeLinkSave
    public void beforeLinkSave(Thread thread) {
        log.debug("beforeLinkSave");

    }

    @HandleAfterLinkSave
    public void afterLinkSave(Thread thread) {
        log.debug("afterLinkSave");

    }

    @HandleBeforeDelete
    public void beforeDelete(Thread thread) {
        log.debug("beforeDelete");

    }

    @HandleAfterDelete
    public void afterDelete(Thread thread) {
        log.debug("afterDelete");

    }

}
