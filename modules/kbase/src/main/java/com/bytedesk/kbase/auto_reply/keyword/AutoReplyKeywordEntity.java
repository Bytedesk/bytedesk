/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 07:29:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 15:24:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.keyword;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.message.MessageTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 自动回复-关键词匹配
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({AutoReplyKeywordEntityListener.class})
@Table(name = "bytedesk_kbase_auto_reply_keyword")
public class AutoReplyKeywordEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;


    // 可以存储多个关键词："关键词1|关键词2|关键词3"
    // 或者存储正则表达式："^.*(关键词1|关键词2|关键词3).*$"
    @Builder.Default
    @Convert(converter = AutoReplyKeywordListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> keywordList = new ArrayList<>();
    
    // 支持多个回答，随机返回一个，分隔符
    // TODO: 支持末尾添加随机字符，避免完全一样的重复回答
    @Builder.Default
    @Convert(converter = AutoReplyKeywordListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> replyList = new ArrayList<>();

    @Builder.Default
    private String matchType = AutoReplyKeywordMatchEnum.EXACT.name();

    @Builder.Default
    private String contentType = MessageTypeEnum.TEXT.name();

    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    @Builder.Default
    @Column(name = "is_transfer")
    private Boolean transfer = false; // 是否是 转人工 关键词

    // 回复次数
    @Builder.Default
    private Integer replyCount = 0;

    // 有效开始日期
    private ZonedDateTime startDate;

    // 有效结束日期
    private ZonedDateTime endDate;

    private String categoryUid; // 文章分类

    private String kbUid; // 对应知识库
}
