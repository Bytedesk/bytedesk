/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-27 18:04:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply.vector;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 快捷回复-向量检索结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuickReplyVectorSearchResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;

    private String title;

    private String content;

    private String type;

    private String categoryUid;

    private String kbUid;

    private String agentUid;

    private Float score;
} 