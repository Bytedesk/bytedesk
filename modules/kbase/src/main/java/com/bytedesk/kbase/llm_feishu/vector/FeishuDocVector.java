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
package com.bytedesk.kbase.llm_feishu.vector;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 飞书文档向量检索结果 DTO（从向量库 metadata 还原，非 ES @Document 映射）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeishuDocVector {

    private String uid;

    private String title;

    private String content;

    private String kbUid;

    private String sourceUid;

    private String categoryUid;

    private String orgUid;

    private String sourceUrl;

    private String objType;

    private String sourceType;

    private String language;
}
