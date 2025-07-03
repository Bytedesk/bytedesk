/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-22 17:03:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-31 08:59:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text.elastic;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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

     // startDate
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private ZonedDateTime startDate;

    // endDate
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private ZonedDateTime endDate;
    
    // @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    // private ZonedDateTime createdAt;
    
    // @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    // private ZonedDateTime updatedAt;

    @Field(type = FieldType.Keyword)
    private String categoryUid;

    @Field(type = FieldType.Keyword)
    private String kbaseUid;

    @Field(type = FieldType.Keyword)
    private List<String> docIdList;

    // @Field(type = FieldType.Date)
    // private ZonedDateTime createdAt;

    // @Field(type = FieldType.Date)
    // private ZonedDateTime updatedAt;

    // @Field(type = FieldType.Keyword)
    // private String createdBy;

    // @Field(type = FieldType.Keyword)
    // private String updatedBy;


}
