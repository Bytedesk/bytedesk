/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-13 15:03:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 15:15:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_image.elastic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.bytedesk.kbase.llm_image.ImageEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Image实体的Elasticsearch版本
 * 用于全文搜索
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bytedesk_kbase_llm_image")
public class ImageElastic {
    
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
    
    // @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    // private ZonedDateTime startDate;

    // @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    // private ZonedDateTime endDate;
    
    @Field(type = FieldType.Keyword)
    private String docId;
    
    @Field(type = FieldType.Keyword)
    private String fileUid;
    
    @Field(type = FieldType.Keyword)
    private String fileName;
    
    @Field(type = FieldType.Keyword)
    private String fileUrl;
    
    @Field(type = FieldType.Keyword)
    private String categoryUid;
    
    @Field(type = FieldType.Keyword)
    private String kbaseUid;


    /**
     * 将单个 ImageEntity 转换为 ImageElastic
     * 
     * @param entity ImageEntity 实体
     * @return ImageElastic 对象
     */
    public static ImageElastic fromEntity(ImageEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return ImageElastic.builder()
                .uid(entity.getUid())
                .name(entity.getName())
                .content(entity.getContent())
                .type(entity.getType())
                .tagList(entity.getTagList())
                .enabled(entity.getEnabled())
                // .startDate(entity.getStartDate())
                // .endDate(entity.getEndDate())
                .docId(entity.getDocId())
                .fileUid(entity.getFile() != null ? entity.getFile().getUid() : null)
                .fileName(entity.getFile() != null ? entity.getFile().getFileName() : null)
                .fileUrl(entity.getFile() != null ? entity.getFile().getFileUrl() : null)
                .categoryUid(entity.getCategoryUid())
                .kbaseUid(entity.getKbase() != null ? entity.getKbase().getUid() : null)
                .build();
    }
    
    /**
     * 将 ImageEntity 列表转换为 ImageElastic 列表
     * 
     * @param entities ImageEntity 实体列表
     * @return ImageElastic 对象列表
     */
    public static List<ImageElastic> fromEntityList(List<ImageEntity> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(ImageElastic::fromEntity)
                .collect(Collectors.toList());
    }
}
