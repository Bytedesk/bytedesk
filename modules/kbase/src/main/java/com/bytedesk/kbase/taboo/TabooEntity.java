/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:51:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-21 09:17:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
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
 * 敏感词
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({TabooEntityListener.class})
@Table(name = "bytedesk_kbase_taboo")
public class TabooEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String content;

    // 同义词synonym list
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> synonymList = new ArrayList<>();

    // reply
    @Builder.Default
    private String reply = I18Consts.I18N_CANT_ANSWER;

    @Builder.Default
    @Column(name = "taboo_type")
    private String type = MessageTypeEnum.TEXT.name();

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    // 匹配次数
    @Builder.Default
    private Integer matchCount = 0;

    // 有效开始日期
    private ZonedDateTime startDate;

    // 有效结束日期
    private ZonedDateTime endDate;

    private String categoryUid;

    private String kbUid; // 对应知识库
}
