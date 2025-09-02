/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-02 23:15:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-02 23:15:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.gitee;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Gitee AI 模型信息实体类
 * 基于 Gitee AI API (/v1/models) 返回的数据结构创建
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiteeModel {

    /**
     * 模型ID (例如: "DeepSeek-V3_1", "Baichuan-M2-32B", "GLM-4_5V")
     */
    private String id;

    /**
     * 对象类型，通常为 "model"
     */
    private String object;

    /**
     * 模型创建时间戳
     */
    private Long created;

    /**
     * 模型拥有者/组织 (例如: "deepseek-ai", "baichuan-inc", "zai-org")
     */
    private String ownedBy;

    /**
     * 从Map转换为GiteeModel对象
     */
    public static GiteeModel fromMap(Map<String, Object> map) {
        GiteeModel model = new GiteeModel();
        
        model.setId((String) map.get("id"));
        model.setObject((String) map.get("object"));
        
        // 处理时间戳，可能是Long或Integer
        Object createdObj = map.get("created");
        if (createdObj instanceof Number) {
            model.setCreated(((Number) createdObj).longValue());
        }
        
        model.setOwnedBy((String) map.get("owned_by"));

        return model;
    }
}
