/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 07:29:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-06 12:31:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.keyword;

import com.bytedesk.ai.robot.Robot;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.category.Category;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.message.MessageTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ai_keyword")
public class Keyword extends BaseEntity {

    // 可以存储多个关键词："关键词1|关键词2|关键词3"
    // 或者存储正则表达式："^.*(关键词1|关键词2|关键词3).*$"
    private String keyword;

    // 多个关键词可以匹配到同一个回复
    // TODO: 支持多个回答，随机返回一个，分隔符："[or]"
    // TODO: 支持末尾添加随机字符，避免完全一样的重复回答
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String reply;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private KeywordMatchEnum matchType = KeywordMatchEnum.EXACT;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private MessageTypeEnum contentType = MessageTypeEnum.TEXT;

    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Robot robot;
    
}
