/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-03 13:57:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-24 14:06:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization;

import java.util.Optional;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class OrganizationEntityListener {

    // 这允许你执行一些后续操作，比如发布事件、更新缓存、触发其他业务逻辑等
    @PostPersist
    public void onPostCreate(OrganizationEntity organization) {
        log.info("onPostCreate: {}", organization);

        if (BytedeskConsts.DEFAULT_ORGANIZATION_UID.equals(organization.getUid())) {
            return;
        }

        // event listener order 
        // 1. member, 
        // 2. category, 
        // 3. faq, 
        // 4. quickbutton, 
        // 5. robot, 
        // 6. agent, 
        // 7. workgroup,
        // NOTE: 组织创建发生在事务内；如果异步发布事件，监听器可能在提交前查询不到组织记录。
        // 因此这里注册 afterCommit 回调，在提交后再发布事件。
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        OrganizationRepository organizationRepository = ApplicationContextHolder.getBean(OrganizationRepository.class);
        String orgUid = organization.getUid();

        Runnable publish = () -> {
            Optional<OrganizationEntity> orgOptional = organizationRepository.findByUid(orgUid);
            orgOptional.ifPresent(bytedeskEventPublisher::publishOrganizationCreateEvent);
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publish.run();
                }
            });
        } else {
            publish.run();
        }
    }

}
