/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-22 17:02:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 17:05:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
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
@Document(indexName = "bytedesk_kbase_faq")
public class FaqElastic {
    
    @Id
    private String uid;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String question;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private List<String> questionList;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String answer;
    
    @Field(type = FieldType.Keyword)
    private String type;
    
    @Field(type = FieldType.Keyword)
    private String status;
    
    @Field(type = FieldType.Keyword)
    private List<String> tagList;
    
    @Field(type = FieldType.Integer)
    private int viewCount;
    
    @Field(type = FieldType.Integer)
    private int clickCount;
    
    @Field(type = FieldType.Integer)
    private int upCount;
    
    @Field(type = FieldType.Integer)
    private int downCount;
    
    @Field(type = FieldType.Boolean)
    private boolean enabled;
    
    @Field(type = FieldType.Date)
    private LocalDateTime startDate;
    
    @Field(type = FieldType.Date)
    private LocalDateTime endDate;
    
    @Field(type = FieldType.Keyword)
    private String categoryUid;
    
    @Field(type = FieldType.Keyword)
    private String kbaseUid;
    
    @Field(type = FieldType.Keyword)
    private String fileUid;
    
    @Field(type = FieldType.Keyword)
    private List<String> docIdList;
    
    @Field(type = FieldType.Keyword)
    private String createdBy;
    
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;
    
    @Field(type = FieldType.Keyword)
    private String updatedBy;
    
    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;
}
