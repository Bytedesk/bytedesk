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
package com.bytedesk.kbase.llm_feishu.event;

import com.bytedesk.kbase.llm_feishu.FeishuDocEntity;

/**
 * 内容变更触发的重索引事件（对齐 FAQ 的 FaqUpdateDocEvent）：
 * 由 RestService.update/ingest 在内容 hash 变化时显式发布。
 */
public class FeishuDocUpdateDocEvent extends AbstractFeishuDocEvent {

    public FeishuDocUpdateDocEvent(FeishuDocEntity doc) {
        super(doc);
    }
}
