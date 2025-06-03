/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-27 18:04:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply.vector;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.kbase.quick_reply.QuickReplyEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 快捷回复-向量检索
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "quick_reply_vector")
public class QuickReplyVector extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Keyword)
    private String shortCut;

    @Field(type = FieldType.Keyword)
    private List<String> tagList;

    @Field(type = FieldType.Boolean)
    private Boolean enabled;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Integer)
    private Integer clickCount;

    @Field(type = FieldType.Date)
    private LocalDateTime startDate;

    @Field(type = FieldType.Date)
    private LocalDateTime endDate;

    @Field(type = FieldType.Keyword)
    private String categoryUid;

    @Field(type = FieldType.Keyword)
    private String kbUid;

    @Field(type = FieldType.Keyword)
    private String agentUid;

    @Field(type = FieldType.Dense_Vector, dims = 1536, index = true, similarity = "cosine")
    private float[] embedding;

    /**
     * 从QuickReplyEntity创建QuickReplyVector的静态方法
     */
    public static QuickReplyVector fromQuickReplyEntity(QuickReplyEntity quickReply) {
        return QuickReplyVector.builder()
                .uid(quickReply.getUid())
                .title(quickReply.getTitle())
                .content(quickReply.getContent())
                .shortCut(quickReply.getShortCut())
                .tagList(quickReply.getTagList())
                .enabled(quickReply.getEnabled())
                .type(quickReply.getType())
                .clickCount(quickReply.getClickCount())
                .startDate(quickReply.getStartDate())
                .endDate(quickReply.getEndDate())
                .categoryUid(quickReply.getCategoryUid())
                .kbUid(quickReply.getKbUid())
                .agentUid(quickReply.getAgentUid())
                .orgUid(quickReply.getOrgUid())
                .platform(quickReply.getPlatform())
                .level(quickReply.getLevel())
                .build();
    }
} 