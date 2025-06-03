/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-31 17:13:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 14:34:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article.vector;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.bytedesk.kbase.article.ArticleEntity;

import org.springframework.data.elasticsearch.annotations.DateFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章向量索引实体类
 * 用于在Elasticsearch中存储文章的向量表示
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bytedesk_article_vector")
public class ArticleVector {
    
    @Id
    private String uid;
    
    @Field(type = FieldType.Text)
    private String title;
    
    @Field(type = FieldType.Text)
    private String summary;
    
    @Field(type = FieldType.Text)
    private String contentMarkdown;
    
    @Field(type = FieldType.Text)
    private String contentHtml;
    
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
    
    @Field(type = FieldType.Boolean)
    private Boolean top;

    // 向量嵌入存储
    @Field(type = FieldType.Dense_Vector, dims = 1536)
    private float[] titleEmbedding;
    
    @Field(type = FieldType.Dense_Vector, dims = 1536)
    private float[] contentEmbedding;
    
    // 有效日期范围
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime startDate;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime endDate;
    
    // 统计数据
    @Field(type = FieldType.Integer)
    private Integer readCount;
    
    @Field(type = FieldType.Integer)
    private Integer likeCount;

    /**
     * 从ArticleEntity创建ArticleVector实体的静态方法
     * 注意：向量嵌入需要单独计算并设置
     */
    public static ArticleVector fromArticleEntity(ArticleEntity article) {
        String kbUid = article.getKbase().getUid() != null ? article.getKbase().getUid() : "";
        
        return ArticleVector.builder()
            .uid(article.getUid())
            .title(article.getTitle())
            .summary(article.getSummary())
            .contentMarkdown(article.getContentMarkdown())
            .contentHtml(article.getContentHtml())
            .tagList(article.getTagList())
            .orgUid(article.getOrgUid())
            .kbUid(kbUid)
            .categoryUid(article.getCategoryUid())
            .enabled(article.getPublished()) // 使用published字段作为enabled
            .top(article.getTop())
            .startDate(article.getStartDate())
            .endDate(article.getEndDate())
            .readCount(article.getReadCount())
            .likeCount(article.getLikeCount())
            // .docIdList(article.getDocIdList())
            .build();
    }
}
