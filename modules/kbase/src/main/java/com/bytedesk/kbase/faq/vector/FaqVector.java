/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 14:13:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 16:49:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq.vector;

import java.time.LocalDateTime;
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

/**
 * FAQ向量索引实体类
 * 用于在Elasticsearch中存储FAQ的向量表示
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bytedesk_faq_vector")
public class FaqVector {
    
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

    // 向量嵌入存储
    @Field(type = FieldType.Dense_Vector, dims = 1536)
    private float[] questionEmbedding;
    
    @Field(type = FieldType.Dense_Vector, dims = 1536)
    private float[] answerEmbedding;
    
    // 文档ID列表（存储在向量库中的ID）
    @Field(type = FieldType.Keyword)
    private List<String> docIdList;
    
    // 有效日期范围
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime startDate;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime endDate;
    
    // 统计数据
    @Field(type = FieldType.Integer)
    private Integer viewCount;
    
    @Field(type = FieldType.Integer)
    private Integer clickCount;
    
    @Field(type = FieldType.Integer)
    private Integer upCount;
    
    @Field(type = FieldType.Integer)
    private Integer downCount;
    
    /**
     * 从FaqEntity创建FaqVector实体的静态方法
     * 注意：向量嵌入需要单独计算并设置
     */
    public static FaqVector fromFaqEntity(FaqEntity faq) {
        String kbUid = "";
        if (faq.getKbase() != null) {
            kbUid = faq.getKbase().getUid();
        }
        
        return FaqVector.builder()
            .uid(faq.getUid())
            .question(faq.getQuestion())
            .answer(faq.getAnswer())
            .similarQuestions(faq.getSimilarQuestions())
            .tagList(faq.getTagList())
            .orgUid(faq.getOrgUid())
            .kbUid(kbUid)
            .categoryUid(faq.getCategoryUid())
            .enabled(faq.getEnabled())
            .startDate(faq.getStartDate() != null ? faq.getStartDate().toLocalDateTime() : null)
            .endDate(faq.getEndDate() != null ? faq.getEndDate().toLocalDateTime() : null)
            .viewCount(faq.getViewCount())
            .clickCount(faq.getClickCount())
            .upCount(faq.getUpCount())
            .downCount(faq.getDownCount())
            .docIdList(faq.getDocIdList())
            .build();
    }
}
