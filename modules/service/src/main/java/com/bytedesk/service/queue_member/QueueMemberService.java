/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-05 18:27:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-05 18:27:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import org.springframework.stereotype.Service;

@Service
public class QueueMemberService {

    // agentUid 代表 技能组uid/客服uid
    // public QueueMemberResponse getNumber(String orgUid, String topic, String visitor) {
    //     Optional<QueueMemberEntity> counterOptional = findFirstByTopic(topic);
    //     if (counterOptional.isPresent()) {
    //         QueueMemberEntity counter = counterOptional.get();
    //         counter.increaseSerialNumber();
    //         //
    //         QueueMemberEntity savedQueueMemberEntity = save(counter);
    //         if (savedQueueMemberEntity == null) {
    //             throw new RuntimeException("save counter failed");
    //         }
    //         return convertToResponse(savedQueueMemberEntity);
    //     } else {
    //         QueueMemberRequest request = QueueMemberRequest.builder()
    //                 .topic(topic)
    //                 .build();
    //         request.setOrgUid(orgUid);
    //         return create(request);
    //     }
    // }
    
}
