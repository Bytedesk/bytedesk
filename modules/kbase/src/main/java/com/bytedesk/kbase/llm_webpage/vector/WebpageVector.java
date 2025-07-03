/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-31 09:50:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 12:56:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_webpage.vector;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.bytedesk.kbase.llm_webpage.WebpageEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网页向量索引实体类
 * 用于在Elasticsearch中存储网页的向量表示
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bytedesk_webpage_vector")
public class WebpageVector {
    
    @Id
    private String uid;
    
    @Field(type = FieldType.Text)
    private String title;
    
    @Field(type = FieldType.Text)
    private String url;
    
    @Field(type = FieldType.Text)
    private String description;
    
    @Field(type = FieldType.Text)
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

    // 向量嵌入存储
    @Field(type = FieldType.Dense_Vector, dims = 1536)
    private float[] titleEmbedding;
    
    @Field(type = FieldType.Dense_Vector, dims = 1536)
    private float[] contentEmbedding;
    
    // 文档ID列表（存储在向量库中的ID）
    @Field(type = FieldType.Keyword)
    private List<String> docIdList;
    
    // 有效日期范围
    @Field(type = FieldType.Keyword)
    private String startDate;

    @Field(type = FieldType.Keyword)
    private String endDate;
    
    // 统计数据
    @Field(type = FieldType.Integer)
    private Integer viewCount;
    
    @Field(type = FieldType.Integer)
    private Integer clickCount;
    
    /**
     * 从WebpageEntity创建WebpageVector实体的静态方法
     * 注意：向量嵌入需要单独计算并设置
     */
    public static WebpageVector fromWebpageEntity(WebpageEntity webpage) {
        String kbUid = "";
        if (webpage.getKbase() != null) {
            kbUid = webpage.getKbase().getUid();
        }
        
        return WebpageVector.builder()
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
            .startDate(webpage.getStartDate() != null ? webpage.getStartDate().format(DateTimeFormatter.ISO_DATE_TIME) : null)
            .endDate(webpage.getEndDate() != null ? webpage.getEndDate().format(DateTimeFormatter.ISO_DATE_TIME) : null)
            .viewCount(webpage.getViewCount())
            .clickCount(webpage.getClickCount())
            .docIdList(webpage.getDocIdList())
            .build();
    }
}
