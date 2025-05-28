/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-13 15:10:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-13 15:10:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ChunkEntity 转换为 ChunkElastic 的工具类
 */
public class ChunkEntityElasticConverter {

    /**
     * 将单个 ChunkEntity 转换为 ChunkElastic
     * 
     * @param entity ChunkEntity 实体
     * @return ChunkElastic 对象
     */
    public static ChunkElastic toElastic(ChunkEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return ChunkElastic.builder()
                .uid(entity.getUid())
                .name(entity.getName())
                .content(entity.getContent())
                .type(entity.getType())
                .tagList(entity.getTagList())
                .enabled(entity.getEnabled())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .docId(entity.getDocId())
                .fileUid(entity.getFile() != null ? entity.getFile().getUid() : null)
                .categoryUid(entity.getCategoryUid())
                .kbaseUid(entity.getKbase() != null ? entity.getKbase().getUid() : null)
                // .createdAt(entity.getCreatedAt())
                // .updatedAt(entity.getUpdatedAt())
                // .createdBy(entity.getCreatedBy())
                // .updatedBy(entity.getUpdatedBy())
                .build();
    }
    
    /**
     * 将 ChunkEntity 列表转换为 ChunkElastic 列表
     * 
     * @param entities ChunkEntity 实体列表
     * @return ChunkElastic 对象列表
     */
    public static List<ChunkElastic> toElasticList(List<ChunkEntity> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(ChunkEntityElasticConverter::toElastic)
                .collect(Collectors.toList());
    }
}
