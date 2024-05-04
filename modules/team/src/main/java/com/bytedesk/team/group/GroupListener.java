/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-15 09:44:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 10:27:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.group;

import org.springframework.stereotype.Component;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GroupListener {

    @PrePersist
    public void prePersist(Group group) {
        log.info("prePersist {}", group.getUid());
    }

    @PostPersist
    public void postPersist(Group group) {
        log.info("postPersist {}", group.getUid());
    }

    @PreUpdate
    public void preUpdate(Group group) {
        log.info("preUpdate {}", group.getUid());
    }

    @PostUpdate
    public void postUpdate(Group group) {
        log.info("postUpdate {}", group.getUid());
    }

    @PreRemove
    public void preRemove(Group group) {
        log.info("preRemove {}", group.getUid());
    }

    @PostRemove
    public void postRemove(Group group) {
        log.info("postRemove {}", group.getUid());
    }

    
}
