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
package com.bytedesk.kbase.llm_chunk.elastic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.util.StringUtils;

import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.kbase.llm_chunk.ChunkEntity;
import com.bytedesk.kbase.translation.KbaseTranslationEntity;

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
@Document(indexName = "bytedesk_kbase_chunk")
public class ChunkElastic {
    
    @Id
    private String uid;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String name;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
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
    private String kbUid;

    @Field(type = FieldType.Keyword)
    private String language;

    @Field(type = FieldType.Keyword)
    private String sourceUid;

    @Field(type = FieldType.Keyword)
    private String sourceLanguage;

    @Field(type = FieldType.Keyword)
    private String sourceType;

    @Field(type = FieldType.Boolean)
    private Boolean translated;

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

        String kbUid = (entity.getKbase() != null) ? entity.getKbase().getUid() : null;
        String sourceLanguage = resolveSourceLanguage(entity);
        if (!StringUtils.hasText(kbUid)) {
            throw new IllegalArgumentException("kbUid is required for indexing chunk uid=" + entity.getUid());
        }
        
        return ChunkElastic.builder()
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
                .kbUid(kbUid)
                .language(sourceLanguage)
                .sourceUid(entity.getUid())
                .sourceLanguage(sourceLanguage)
                .sourceType("CHUNK")
                .translated(false)
                .build();
    }

    public static ChunkElastic fromTranslation(ChunkEntity entity, KbaseTranslationEntity translation) {
        if (entity == null || translation == null) {
            return null;
        }

        String kbUid = (entity.getKbase() != null) ? entity.getKbase().getUid() : null;
        if (!StringUtils.hasText(kbUid)) {
            throw new IllegalArgumentException("kbUid is required for indexing translated chunk uid=" + entity.getUid());
        }

        String targetLanguage = StringUtils.hasText(translation.getTargetLanguage())
                ? translation.getTargetLanguage().trim().toUpperCase()
                : resolveSourceLanguage(entity);

        return ChunkElastic.builder()
                .uid(translation.getUid())
                .name(StringUtils.hasText(translation.getTitle()) ? translation.getTitle() : entity.getName())
                .content(StringUtils.hasText(translation.getContent()) ? translation.getContent() : translation.getSummary())
                .type(entity.getType())
                .tagList(translation.getTagList() == null || translation.getTagList().isEmpty() ? entity.getTagList() : translation.getTagList())
                .enabled(Boolean.TRUE.equals(translation.getEnabled()) && Boolean.TRUE.equals(entity.getEnabled()))
                .docId(translation.getUid())
                .fileUid(entity.getFile() != null ? entity.getFile().getUid() : null)
                .fileName(entity.getFile() != null ? entity.getFile().getFileName() : null)
                .fileUrl(entity.getFile() != null ? entity.getFile().getFileUrl() : null)
                .categoryUid(entity.getCategoryUid())
                .kbUid(kbUid)
                .language(targetLanguage)
                .sourceUid(entity.getUid())
                .sourceLanguage(resolveSourceLanguage(entity))
                .sourceType("CHUNK")
                .translated(true)
                .build();
    }

    private static String resolveSourceLanguage(ChunkEntity entity) {
        if (entity.getKbase() != null && StringUtils.hasText(entity.getKbase().getSourceLanguage())) {
            return entity.getKbase().getSourceLanguage();
        }
        if (entity.getKbase() != null && StringUtils.hasText(entity.getKbase().getLanguage())) {
            return entity.getKbase().getLanguage();
        }
        return LanguageEnum.ZH_CN.name();
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
