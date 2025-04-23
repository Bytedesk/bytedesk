/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-22 16:49:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 19:02:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.qa;

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

/**
 * 用于Elasticsearch的Qa实体
 * 全文检索对应到elasticsearch的文档
 * @author jackning
 * 浏览器可视化插件管理 es-client: https://gitee.com/q2316367743/es-client
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bytedesk_kbase_llm_qa")
public class QaElastic {
    
    @Id
    private String uid;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String question;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String answer;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private List<String> questionList;
    
    @Field(type = FieldType.Keyword)
    private List<String> tagList;
    
    @Field(type = FieldType.Keyword)
    private String orgUid;
    
    @Field(type = FieldType.Keyword)
    private String kbUid;
    
    @Field(type = FieldType.Keyword)
    private String categoryUid;
    
    @Field(type = FieldType.Boolean)
    private boolean enabled;
    
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;
    
    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;
    
    @Field(type = FieldType.Integer)
    private int viewCount;
    
    @Field(type = FieldType.Integer)
    private int clickCount;
    
    @Field(type = FieldType.Integer)
    private int upCount;
    
    @Field(type = FieldType.Integer)
    private int downCount;
    
    // 从QaEntity创建QaElastic的静态方法
    public static QaElastic fromQaEntity(QaEntity qa) {
        String kbUid = "";
        if (qa.getKbase() != null) {
            kbUid = qa.getKbase().getUid();
        }
        
        return QaElastic.builder()
            .uid(qa.getUid())
            .question(qa.getQuestion())
            .answer(qa.getAnswer())
            .questionList(qa.getQuestionList())
            .tagList(qa.getTagList())
            .orgUid(qa.getOrgUid())
            .kbUid(kbUid)
            .categoryUid(qa.getCategoryUid())
            .enabled(qa.isEnabled())
            .createdAt(qa.getCreatedAt())
            .updatedAt(qa.getUpdatedAt())
            .viewCount(qa.getViewCount())
            .clickCount(qa.getClickCount())
            .upCount(qa.getUpCount())
            .downCount(qa.getDownCount())
            .build();
    }
}
