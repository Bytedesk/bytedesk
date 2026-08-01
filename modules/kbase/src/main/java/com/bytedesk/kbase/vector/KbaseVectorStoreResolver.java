/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-06-12 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-06-12 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.vector;

import org.springframework.ai.vectorstore.VectorStore;

import com.bytedesk.kbase.kbase.KbaseEntity;

public interface KbaseVectorStoreResolver {

    VectorStore resolveByKbase(KbaseEntity kbase);

    VectorStore resolveByKbUid(String kbUid);

    VectorStore resolveDefault();

    /**
     * 获取当前知识库使用的 embedding 配置信息（provider / model / dimensions）。
     * 默认返回 null，由具体实现覆盖。
     */
    default EmbeddingInfo getEmbeddingInfo(KbaseEntity kbase) {
        return null;
    }

    /** embedding 配置信息 */
    record EmbeddingInfo(String provider, String model, Integer dimensions) {}
}