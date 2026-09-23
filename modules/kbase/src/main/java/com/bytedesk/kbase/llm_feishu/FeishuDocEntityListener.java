/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-20 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_feishu;

import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.llm_feishu.event.FeishuDocCreateEvent;
import com.bytedesk.kbase.llm_feishu.event.FeishuDocDeleteEvent;
import com.bytedesk.kbase.llm_feishu.event.FeishuDocUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

import lombok.extern.slf4j.Slf4j;

/**
 * 注意：索引状态的 bulk 更新（@Modifying）不触发本监听器，因此状态回写不会产生索引消息循环。
 * UpdateEvent 不用于触发索引（与 FAQ 的 FaqUpdateEvent 同策略），内容变更的重索引走 UpdateDocEvent。
 */
@Slf4j
@Component
public class FeishuDocEntityListener {

    @PostPersist
    public void onPostPersist(FeishuDocEntity doc) {
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new FeishuDocCreateEvent(doc));
    }

    @PostUpdate
    public void onPostUpdate(FeishuDocEntity doc) {
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        if (doc.isDeleted()) {
            publisher.publishEvent(new FeishuDocDeleteEvent(doc));
        } else {
            publisher.publishEvent(new FeishuDocUpdateEvent(doc));
        }
    }
}
