/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-13 18:10:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 15:18:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TextEntity 转换为 TextElastic 的工具类
 */
public class TextEntityElasticConverter {

    /**
     * 将单个 TextEntity 转换为 TextElastic
     * 
     * @param entity TextEntity 实体
     * @return TextElastic 对象
     */
    public static TextElastic toElastic(TextEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return TextElastic.builder()
                .uid(entity.getUid())
                .title(entity.getTitle())
                .content(entity.getContent())
                .type(entity.getType())
                .status(entity.getStatus())
                .tagList(entity.getTagList())
                .enabled(entity.getEnabled())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .categoryUid(entity.getCategoryUid())
                .kbaseUid(entity.getKbase() != null ? entity.getKbase().getUid() : null)
                .docIdList(entity.getDocIdList())
                // .createdBy(entity.getCreatedBy())
                // .updatedBy(entity.getUpdatedBy())
                .build();
    }
    
    /**
     * 将 TextEntity 列表转换为 TextElastic 列表
     * 
     * @param entities TextEntity 实体列表
     * @return TextElastic 对象列表
     */
    public static List<TextElastic> toElasticList(List<TextEntity> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(TextEntityElasticConverter::toElastic)
                .collect(Collectors.toList());
    }
}
