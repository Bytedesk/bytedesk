/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-03 13:57:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-27 13:03:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.utils.ApplicationContextHolder;

// import com.bytedesk.core.event.BytedeskEventPublisher;
// import com.bytedesk.core.utils.ApplicationContextHolder;

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
        OrganizationEntity clonedOrg = SerializationUtils.clone(organization);

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
        // 放在此处会报错，直接放到service Create中
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishOrganizationCreateEvent(clonedOrg);
    }

}
