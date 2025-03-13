/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-13 17:02:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-13 17:02:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlackService {

    private final BlackRestService blackRestService;
    
    // 检查黑名单
    public boolean isBlackList(MessageProtobuf messageProtobuf) {
        String uid = messageProtobuf.getUser().getUid();
        MessageExtra extraObject = JSON.parseObject(messageProtobuf.getExtra(), MessageExtra.class);
        if (extraObject != null) {
            String orgUid = extraObject.getOrgUid();
            Optional<BlackEntity> blackOpt = blackRestService.findByVisitorUidAndOrgUid(uid, orgUid);
            if (blackOpt.isPresent()) {
                BlackEntity black = blackOpt.get();
                if (black.getEndTime() == null || black.getEndTime().isAfter(LocalDateTime.now())) {
                    return true;
                }
            }
        }
        return false;
    }
}
