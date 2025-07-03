/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-13 15:03:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 17:03:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk.elastic;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.bytedesk.kbase.llm_chunk.ChunkEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chunk实体的Elasticsearch版本
 * 用于全文搜索
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bytedesk_kbase_llm_chunk")
public class ChunkElastic {
    
    @Id
    private String uid;
    
    @Field(type = FieldType.Keyword)
    private String name;
    
    @Field(type = FieldType.Text)
    private String content;
    
    @Field(type = FieldType.Keyword)
    private String type;
    
    @Field(type = FieldType.Keyword)
    private List<String> tagList;
    
    @Field(type = FieldType.Boolean)
    private Boolean enabled;
    
    @Field(type = FieldType.Date)
    private String startDate;
    
    @Field(type = FieldType.Date)
    private String endDate;
    
    @Field(type = FieldType.Keyword)
    private String docId;
    
    @Field(type = FieldType.Keyword)
    private String fileUid;
    
    @Field(type = FieldType.Keyword)
    private String categoryUid;
    
    @Field(type = FieldType.Keyword)
    private String kbaseUid;


    /**
     * 将单个 ChunkEntity 转换为 ChunkElastic
     * 
     * @param entity ChunkEntity 实体
     * @return ChunkElastic 对象
     */
    public static ChunkElastic fromEntity(ChunkEntity entity) {
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
                .startDate(entity.getStartDate() != null ? entity.getStartDate().format(DateTimeFormatter.ISO_DATE_TIME) : null)
                .endDate(entity.getEndDate() != null ? entity.getEndDate().format(DateTimeFormatter.ISO_DATE_TIME) : null)
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
    public static List<ChunkElastic> fromEntityList(List<ChunkEntity> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(ChunkElastic::fromEntity)
                .collect(Collectors.toList());
    }
}
