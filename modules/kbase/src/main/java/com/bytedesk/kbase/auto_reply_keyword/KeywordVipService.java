/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 19:36:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-22 22:23:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply_keyword;

import java.util.List;
import java.util.Random;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class KeywordVipService {

    private final KeywordRepository keywordRepository;

    public String getKeywordReply(String keyword, String kbUid, String orgUid) {
        KeywordRequest request = KeywordRequest.builder().build();
        request.getKeywordList().add(keyword);
        request.setKbUid(kbUid);
        request.setOrgUid(orgUid);
        //
        Specification<KeywordEntity> spec = KeywordSpecification.search(request);
        List<KeywordEntity> keywordObjects = keywordRepository.findAll(spec);
        if (keywordObjects.isEmpty()) {
            return null;
        }
        // 使用第一条返回结果, TODO: 后续需要优化
        KeywordEntity keywordObject = keywordObjects.get(0);
        //
        // 随机选择一个回复
        Random random = new Random();
        String reply = null;
        if (!keywordObject.getReplyList().isEmpty()) {
            int randomIndex = random.nextInt(keywordObject.getReplyList().size());
            reply = keywordObject.getReplyList().get(randomIndex);
        }
        // 
        return reply;
    }

}
