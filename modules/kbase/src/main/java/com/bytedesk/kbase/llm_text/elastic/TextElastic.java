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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.util.StringUtils;

import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.kbase.llm_text.TextEntity;
import com.bytedesk.kbase.translation.KbaseTranslationEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bytedesk_kbase_text")
public class TextElastic {
    
    @Id
    private String uid;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Keyword)
    private List<String> tagList;

    @Field(type = FieldType.Boolean)
    private Boolean enabled;

    // @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    // private ZonedDateTime startDate;

    // @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    // private ZonedDateTime endDate;

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

        String kbUid = (entity.getKbase() != null) ? entity.getKbase().getUid() : null;
        String sourceLanguage = resolveSourceLanguage(entity);
        if (!StringUtils.hasText(kbUid)) {
            throw new IllegalArgumentException("kbUid is required for indexing text uid=" + entity.getUid());
        }
        
        return TextElastic.builder()
                .uid(entity.getUid())
                .title(entity.getTitle())
                .content(entity.getContent())
                .type(entity.getType())
                .status(entity.getElasticStatus())
                .tagList(entity.getTagList())
                .enabled(entity.getEnabled())
                // .startDate(entity.getStartDate())
                // .endDate(entity.getEndDate())
                .categoryUid(entity.getCategoryUid())
                .kbUid(kbUid)
                .language(sourceLanguage)
                .sourceUid(entity.getUid())
                .sourceLanguage(sourceLanguage)
                .sourceType("TEXT")
                .translated(false)
                .docIdList(entity.getDocIdList())
                .build();
    }

    public static TextElastic fromTranslation(TextEntity entity, KbaseTranslationEntity translation) {
        if (entity == null || translation == null) {
            return null;
        }

        String kbUid = (entity.getKbase() != null) ? entity.getKbase().getUid() : null;
        if (!StringUtils.hasText(kbUid)) {
            throw new IllegalArgumentException("kbUid is required for indexing translated text uid=" + entity.getUid());
        }

        String targetLanguage = StringUtils.hasText(translation.getTargetLanguage())
                ? translation.getTargetLanguage().trim().toUpperCase()
                : resolveSourceLanguage(entity);

        return TextElastic.builder()
                .uid(translation.getUid())
                .title(StringUtils.hasText(translation.getTitle()) ? translation.getTitle() : entity.getTitle())
                .content(StringUtils.hasText(translation.getContent()) ? translation.getContent() : translation.getSummary())
                .type(entity.getType())
                .status(entity.getElasticStatus())
                .tagList(translation.getTagList() == null || translation.getTagList().isEmpty() ? entity.getTagList() : translation.getTagList())
                .enabled(Boolean.TRUE.equals(translation.getEnabled()) && Boolean.TRUE.equals(entity.getEnabled()))
                .categoryUid(entity.getCategoryUid())
                .kbUid(kbUid)
                .language(targetLanguage)
                .sourceUid(entity.getUid())
                .sourceLanguage(resolveSourceLanguage(entity))
                .sourceType("TEXT")
                .translated(true)
                .docIdList(new ArrayList<>())
                .build();
    }

    private static String resolveSourceLanguage(TextEntity entity) {
        if (entity.getKbase() != null && StringUtils.hasText(entity.getKbase().getSourceLanguage())) {
            return entity.getKbase().getSourceLanguage();
        }
        if (entity.getKbase() != null && StringUtils.hasText(entity.getKbase().getLanguage())) {
            return entity.getKbase().getLanguage();
        }
        return LanguageEnum.ZH_CN.name();
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
