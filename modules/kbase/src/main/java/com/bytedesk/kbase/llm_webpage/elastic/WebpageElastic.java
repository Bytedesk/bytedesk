/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-31 09:15:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-31 09:15:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_webpage.elastic;

import java.util.List;

import org.springframework.data.annotation.Id;

import com.bytedesk.core.utils.BdDateUtils;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.bytedesk.kbase.llm_webpage.WebpageEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网页全文搜索实体类
 * 用于在Elasticsearch中存储网页文档
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bytedesk_kbase_webpage")
public class WebpageElastic {
    
    @Id
    private String uid;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;
    
    @Field(type = FieldType.Text)
    private String url;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String description;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;
    
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
    
    @Field(type = FieldType.Keyword)
    private String startDate;
    
    @Field(type = FieldType.Keyword)
    private String endDate;
    
    @Field(type = FieldType.Integer)
    private Integer viewCount;
    
    @Field(type = FieldType.Integer)
    private Integer clickCount;
    
    // 从WebpageEntity创建WebpageElastic的静态方法
    public static WebpageElastic fromWebpageEntity(WebpageEntity webpage) {
        String kbUid = "";
        if (webpage.getKbase() != null) {
            kbUid = webpage.getKbase().getUid();
        }
        
        return WebpageElastic.builder()
            .uid(webpage.getUid())
            .title(webpage.getTitle())
            .url(webpage.getUrl())
            .description(webpage.getDescription())
            .content(webpage.getContent())
            .tagList(webpage.getTagList())
            .orgUid(webpage.getOrgUid())
            .kbUid(kbUid)
            .categoryUid(webpage.getCategoryUid())
            .enabled(webpage.getEnabled())
            .startDate(webpage.getStartDate() != null ? BdDateUtils.formatDatetimeToString(webpage.getStartDate()) : null)
            .endDate(webpage.getEndDate() != null ? BdDateUtils.formatDatetimeToString(webpage.getEndDate()) : null)
            .viewCount(webpage.getViewCount())
            .clickCount(webpage.getClickCount())
            .build();
    }
}
