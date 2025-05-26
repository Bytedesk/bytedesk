/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-13 17:02:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 07:22:42
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

import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlackService {

    private final BlackRestService blackRestService;

    public Boolean isBlackList(String visitorUid, String orgUid) {
        Optional<BlackEntity> blackOpt = blackRestService.findByVisitorUidAndOrgUid(visitorUid, orgUid);
        if (blackOpt.isPresent()) {
            BlackEntity black = blackOpt.get();
            if (black.getEndTime() == null || black.getEndTime().isAfter(LocalDateTime.now())) {
                log.info("1. User {} is in the blacklist for org {}", visitorUid, orgUid);
                return true;
            }
        }
        return false;
    }
    
    // 检查黑名单
    public Boolean isBlackList(MessageProtobuf messageProtobuf) {
        // String uid = messageProtobuf.getUser().getUid();
        String userUid = messageProtobuf.getUser().getUid();
        if (userUid == null) {
            return false;
        }
        MessageExtra extraObject = MessageExtra.fromJson(messageProtobuf.getExtra());
         //JSON.parseObject(messageProtobuf.getExtra(), MessageExtra.class);
        if (extraObject != null) {
            String orgUid = extraObject.getOrgUid();
            if (orgUid == null) {
                return false;
            }
            // Check if the user is in the blacklist
            Optional<BlackEntity> blackOpt = blackRestService.findByVisitorUidAndOrgUid(userUid, orgUid);
            if (blackOpt.isPresent()) {
                BlackEntity black = blackOpt.get();
                if (black.getEndTime() == null || black.getEndTime().isAfter(LocalDateTime.now())) {
                    log.info("2. User {} is in the blacklist for org {}", userUid, orgUid);
                    return true;
                }
            }
        }
        return false;
    }
}
