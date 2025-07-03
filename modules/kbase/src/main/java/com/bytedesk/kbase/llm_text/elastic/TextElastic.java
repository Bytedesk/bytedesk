/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-22 17:03:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 17:02:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text.elastic;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.bytedesk.kbase.llm_text.TextEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bytedesk_kbase_llm_text")
public class TextElastic {
    
    @Id
    private String uid;

    @Field(type = FieldType.Keyword)
    private String title;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Keyword)
    private List<String> tagList;

    @Field(type = FieldType.Boolean)
    private Boolean enabled;

    @Field(type = FieldType.Date)
    private String startDate;

    @Field(type = FieldType.Date)
    private String endDate;

    @Field(type = FieldType.Keyword)
    private String categoryUid;

    @Field(type = FieldType.Keyword)
    private String kbaseUid;

    @Field(type = FieldType.Keyword)
    private List<String> docIdList;

    /**
     * 将单个 TextEntity 转换为 TextElastic
     * 
     * @param entity TextEntity 实体
     * @return TextElastic 对象
     */
    public static TextElastic fromEntity(TextEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return TextElastic.builder()
                .uid(entity.getUid())
                .title(entity.getTitle())
                .content(entity.getContent())
                .type(entity.getType())
                .status(entity.getElasticStatus())
                .tagList(entity.getTagList())
                .enabled(entity.getEnabled())
                .startDate(entity.getStartDate() != null ? entity.getStartDate().format(DateTimeFormatter.ISO_DATE_TIME) : null)
                .endDate(entity.getEndDate() != null ? entity.getEndDate().format(DateTimeFormatter.ISO_DATE_TIME) : null)
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
    public static List<TextElastic> fromEntityList(List<TextEntity> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(TextElastic::fromEntity)
                .collect(Collectors.toList());
    }

}
