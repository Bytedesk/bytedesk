/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-22 18:26:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-21 12:33:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo;

import java.util.List;
import java.util.Optional;

import com.bytedesk.core.message.MessageResponse;

/**
 * 企业版敏感词服务接口。
 *
 * modules/kbase 仅提供空实现；enterprise/kbase 提供真实实现。
 */
public interface TabooService {

    Boolean existsByContent(String content, String orgUid);

    List<String> listEnabledWordsWithSynonyms(String orgUid);

    Optional<String> resolveReplyForContent(String orgUid, String content);

    VisitorTabooCheckResult checkVisitorTabooBeforeSse(String messageJson, String fallbackOrgUid);

    record VisitorTabooCheckResult(
            boolean hit,
            String filteredContent,
            String reply,
            MessageResponse questionMessage,
            MessageResponse answerMessage) {
    }
} 
