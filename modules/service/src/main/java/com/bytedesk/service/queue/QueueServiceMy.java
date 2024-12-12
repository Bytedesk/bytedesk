/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-05 17:07:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-07 12:20:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.utils.DateUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QueueServiceMy {

    private final QueueRestService visitorQueueRestService;
    
    @Transactional
    public QueueResponse enqueue(ThreadEntity threadEntity) {
        // 
        String threadTopic = threadEntity.getTopic();
        String day = DateUtils.formatDateNow();
        String orgUid = threadEntity.getOrgUid();
        // 
        QueueResponse queueResponse = visitorQueueRestService.getQueue(threadTopic, day, orgUid);


        return queueResponse;
    }

    @Transactional
    public Optional<QueueEntity> dequeue() {
        
        return null;
    }


}
