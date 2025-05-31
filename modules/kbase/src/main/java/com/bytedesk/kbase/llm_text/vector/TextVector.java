/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 14:18:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-31 08:59:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text.vector;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.bytedesk.kbase.llm_text.TextEntity;

import org.springframework.data.elasticsearch.annotations.DateFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Text向量索引实体类
 * 用于在Elasticsearch中存储Text的向量表示
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bytedesk_text_vector")
public class TextVector {
    
    @Id
    private String uid;
    
    @Field(type = FieldType.Text)
    private String title;
    
    @Field(type = FieldType.Text)
    private String content;
    
    @Field(type = FieldType.Keyword)
    private String type;
    
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
    private float[] titleEmbedding;
    
    @Field(type = FieldType.Dense_Vector, dims = 1536)
    private float[] contentEmbedding;
    
    // 文档ID列表（存储在向量库中的ID）
    @Field(type = FieldType.Keyword)
    private List<String> docIdList;
    
    // 有效日期范围
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime startDate;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime endDate;
    
    // 状态字段
    @Field(type = FieldType.Keyword)
    private String status;
    
    @Field(type = FieldType.Keyword)
    private String vectorStatus;
    
    /**
     * 从TextEntity创建TextVector实体的静态方法
     * 注意：向量嵌入需要单独计算并设置
     */
    public static TextVector fromTextEntity(TextEntity text) {
        String kbUid = "";
        if (text.getKbase() != null) {
            kbUid = text.getKbase().getUid();
        }
        
        return TextVector.builder()
            .uid(text.getUid())
            .title(text.getTitle())
            .content(text.getContent())
            .type(text.getType())
            .tagList(text.getTagList())
            .orgUid(text.getOrgUid())
            .kbUid(kbUid)
            .categoryUid(text.getCategoryUid())
            .enabled(text.getEnabled())
            .startDate(text.getStartDate())
            .endDate(text.getEndDate())
            .status(text.getStatus())
            .vectorStatus(text.getVectorStatus())
            .docIdList(text.getDocIdList())
            .build();
    }
}
