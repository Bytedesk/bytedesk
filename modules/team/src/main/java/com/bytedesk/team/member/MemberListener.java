/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-15 09:46:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-24 15:15:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

import org.springframework.stereotype.Component;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MemberListener {

    // @PrePersist
    // public void prePersist(Member member) {
    //     log.info("prePersist {}", member.getUid());
    // }

    @PostPersist
    public void postPersist(Member member) {
        log.info("postPersist {}", member.getUid());
    }

    // @PreUpdate
    // public void preUpdate(Member member) {
    //     log.info("preUpdate {}", member.getUid());
    // }

    @PostUpdate
    public void postUpdate(Member member) {
        log.info("postUpdate {}", member.getUid());
    }

    // @PreRemove
    // public void preRemove(Member member) {
    //     log.info("preRemove {}", member.getUid());
    // }

    // @PostRemove
    // public void postRemove(Member member) {
    //     log.info("postRemove {}", member.getUid());
    // }
    
}
