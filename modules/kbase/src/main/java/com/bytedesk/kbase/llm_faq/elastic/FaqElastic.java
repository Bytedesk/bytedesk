/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-22 17:02:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 20:05:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_faq.elastic;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.util.StringUtils;

import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.kbase.llm_faq.FaqEntity;
import com.bytedesk.kbase.translation.KbaseTranslationEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bytedesk_kbase_faq")
public class FaqElastic {
    
    @Id
    private String uid;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String question;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String answer;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private List<String> similarQuestions;
    
    @Field(type = FieldType.Keyword)
    private List<String> tagList;
    
    @Field(type = FieldType.Keyword)
    private String orgUid;
    
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
    private String categoryUid;
    
    @Field(type = FieldType.Boolean)
    private Boolean enabled;

    // @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    // private ZonedDateTime startDate;

    // @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    // private ZonedDateTime endDate;
    
    @Field(type = FieldType.Integer)
    private Integer viewCount;
    
    @Field(type = FieldType.Integer)
    private Integer clickCount;
    
    @Field(type = FieldType.Integer)
    private Integer upCount;
    
    @Field(type = FieldType.Integer)
    private Integer downCount;
    
    // 从FaqEntity创建FaqElastic的静态方法
    public static FaqElastic fromFaqEntity(FaqEntity faq) {
        String kbUid = (faq.getKbase() != null) ? faq.getKbase().getUid() : null;
        String sourceLanguage = resolveSourceLanguage(faq);
        if (!StringUtils.hasText(kbUid)) {
            throw new IllegalArgumentException("kbUid is required for indexing faq uid=" + faq.getUid());
        }
        
        return FaqElastic.builder()
            .uid(faq.getUid())
            .question(faq.getQuestion())
            .answer(faq.getAnswer())
            .similarQuestions(faq.getSimilarQuestions())
            .tagList(faq.getTagList())
            .orgUid(faq.getOrgUid())
            .kbUid(kbUid)
            .language(sourceLanguage)
            .sourceUid(faq.getUid())
            .sourceLanguage(sourceLanguage)
            .sourceType("FAQ")
            .translated(false)
            .categoryUid(faq.getCategoryUid())
            .enabled(faq.getEnabled())
            // .startDate(faq.getStartDate())
            // .endDate(faq.getEndDate())
            .viewCount(faq.getViewCount())
            .clickCount(faq.getClickCount())
            .upCount(faq.getUpCount())
            .downCount(faq.getDownCount())
            .build();
    }

        public static FaqElastic fromTranslation(FaqEntity faq, KbaseTranslationEntity translation) {
        String kbUid = (faq.getKbase() != null) ? faq.getKbase().getUid() : null;
        if (!StringUtils.hasText(kbUid)) {
            throw new IllegalArgumentException("kbUid is required for indexing translated faq uid=" + faq.getUid());
        }

        String targetLanguage = StringUtils.hasText(translation.getTargetLanguage())
            ? translation.getTargetLanguage().trim().toUpperCase()
            : resolveSourceLanguage(faq);
        String translatedQuestion = StringUtils.hasText(translation.getTitle()) ? translation.getTitle() : faq.getQuestion();
        String translatedAnswer = StringUtils.hasText(translation.getContent())
            ? translation.getContent()
            : translation.getSummary();

        return FaqElastic.builder()
            .uid(translation.getUid())
            .question(translatedQuestion)
            .answer(translatedAnswer)
            .similarQuestions(new ArrayList<>())
            .tagList(translation.getTagList() == null || translation.getTagList().isEmpty() ? faq.getTagList() : translation.getTagList())
            .orgUid(faq.getOrgUid())
            .kbUid(kbUid)
            .language(targetLanguage)
            .sourceUid(faq.getUid())
            .sourceLanguage(resolveSourceLanguage(faq))
            .sourceType("FAQ")
            .translated(true)
            .categoryUid(faq.getCategoryUid())
            .enabled(Boolean.TRUE.equals(translation.getEnabled()) && Boolean.TRUE.equals(faq.getEnabled()))
            .viewCount(faq.getViewCount())
            .clickCount(faq.getClickCount())
            .upCount(faq.getUpCount())
            .downCount(faq.getDownCount())
            .build();
        }

    private static String resolveSourceLanguage(FaqEntity faq) {
        if (faq.getKbase() != null && StringUtils.hasText(faq.getKbase().getSourceLanguage())) {
            return faq.getKbase().getSourceLanguage();
        }
        if (faq.getKbase() != null && StringUtils.hasText(faq.getKbase().getLanguage())) {
            return faq.getKbase().getLanguage();
        }
        return LanguageEnum.ZH_CN.name();
    }

}
