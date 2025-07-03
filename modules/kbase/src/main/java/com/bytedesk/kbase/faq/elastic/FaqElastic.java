/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-22 17:02:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 19:31:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq.elastic;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.bytedesk.kbase.faq.FaqEntity;

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
    
    @Field(type = FieldType.Text)
    private String question;
    
    @Field(type = FieldType.Text)
    private String answer;
    
    @Field(type = FieldType.Text)
    private List<String> similarQuestions;
    
    @Field(type = FieldType.Keyword)
    private List<String> tagList;
    
    @Field(type = FieldType.Keyword)
    private String orgUid;
    
    @Field(type = FieldType.Keyword)
    private String kbUid;
    
    @Field(type = FieldType.Keyword)
    private String categoryUid;
    
    @Field(type = FieldType.Boolean)
    private Boolean enabled;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private ZonedDateTime startDate;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private ZonedDateTime endDate;
    
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
        String kbUid = "";
        if (faq.getKbase() != null) {
            kbUid = faq.getKbase().getUid();
        }
        
        return FaqElastic.builder()
            .uid(faq.getUid())
            .question(faq.getQuestion())
            .answer(faq.getAnswer())
            .similarQuestions(faq.getSimilarQuestions())
            .tagList(faq.getTagList())
            .orgUid(faq.getOrgUid())
            .kbUid(kbUid)
            .categoryUid(faq.getCategoryUid())
            .enabled(faq.getEnabled())
            .startDate(faq.getStartDate())
            .endDate(faq.getEndDate())
            .viewCount(faq.getViewCount())
            .clickCount(faq.getClickCount())
            .upCount(faq.getUpCount())
            .downCount(faq.getDownCount())
            .build();
    }

}
