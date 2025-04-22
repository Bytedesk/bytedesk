/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-22 17:03:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 17:06:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

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
@Document(indexName = "bytedesk_kbase_article")
public class ArticleElastic {
    
    @Id
    private String id;
    
    // 标题
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;
    
    // 内容
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;
    
    // 摘要
    @Field(type = FieldType.Text)
    private String summary;
    
    // 标签
    @Field(type = FieldType.Keyword)
    private List<String> tags;
    
    // 分类ID
    @Field(type = FieldType.Keyword)
    private String categoryId;
    
    // 知识库类型：帮助中心、产品文档、问答等
    @Field(type = FieldType.Keyword)
    private String type;
    
    // 状态：草稿、已发布、已删除
    @Field(type = FieldType.Keyword)
    private String status;
    
    // 审核状态：待审核、已通过、已拒绝
    @Field(type = FieldType.Keyword)
    private String auditStatus;
    
    // 作者ID
    @Field(type = FieldType.Keyword)
    private String authorId;
    
    // 作者名称
    @Field(type = FieldType.Keyword)
    private String authorNickname;
    
    // 浏览次数
    @Field(type = FieldType.Long)
    private Long viewCount;
    
    // 点赞次数
    @Field(type = FieldType.Long)
    private Long likeCount;
    
    // 收藏次数
    @Field(type = FieldType.Long)
    private Long favoriteCount;
    
    // 评论次数
    @Field(type = FieldType.Long)
    private Long commentCount;
    
    // 发布时间
    @Field(type = FieldType.Date)
    private LocalDateTime publishedAt;
    
    // 创建时间
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;
    
    // 更新时间
    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;
}
