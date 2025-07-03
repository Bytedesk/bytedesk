/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-31 16:10:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 12:55:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article.elastic;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.bytedesk.kbase.article.ArticleEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bytedesk_kbase_article")
public class ArticleElastic {
    
    @Id
    private String uid;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String summary;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String contentMarkdown;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String contentHtml;
    
    @Field(type = FieldType.Keyword)
    private List<String> tagList;
    
    // @Field(type = FieldType.Keyword)
    // private String type;
    
    @Field(type = FieldType.Keyword)
    private String status;
    
    @Field(type = FieldType.Keyword)
    private String auditStatus;
    
    @Field(type = FieldType.Boolean)
    private Boolean top;
    
    @Field(type = FieldType.Boolean)
    private Boolean published;
    
    @Field(type = FieldType.Boolean)
    private Boolean markdown;
    
    @Field(type = FieldType.Keyword)
    private String orgUid;
    
    @Field(type = FieldType.Keyword)
    private String kbUid;
    
    @Field(type = FieldType.Keyword)
    private String categoryUid;
    
    @Field(type = FieldType.Keyword)
    private String startDate;
    
    @Field(type = FieldType.Keyword)
    private String endDate;
    
    @Field(type = FieldType.Integer)
    private Integer readCount;
    
    @Field(type = FieldType.Integer)
    private Integer likeCount;
    
    // 从ArticleEntity创建ArticleElastic的静态方法
    public static ArticleElastic fromArticleEntity(ArticleEntity article) {
        String kbUid = article.getKbase() != null ? article.getKbase().getUid() : null;
        
        return ArticleElastic.builder()
            .uid(article.getUid())
            .title(article.getTitle())
            .summary(article.getSummary())
            .contentMarkdown(article.getContentMarkdown())
            .contentHtml(article.getContentHtml())
            .tagList(article.getTagList())
            // .type(article.getType())
            .status(article.getElasticStatus())
            .auditStatus(article.getAuditStatus())
            .top(article.getTop())
            .published(article.getPublished())
            .markdown(article.getMarkdown())
            .orgUid(article.getOrgUid())
            .kbUid(kbUid)
            .categoryUid(article.getCategoryUid())
            .startDate(article.getStartDate() != null ? article.getStartDate().format(DateTimeFormatter.ISO_DATE_TIME) : null)
            .endDate(article.getEndDate() != null ? article.getEndDate().format(DateTimeFormatter.ISO_DATE_TIME) : null)
            .readCount(article.getReadCount())
            .likeCount(article.getLikeCount())
            .build();
    }
}