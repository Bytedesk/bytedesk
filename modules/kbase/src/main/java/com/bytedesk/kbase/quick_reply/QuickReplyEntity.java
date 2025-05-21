/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:12:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-24 15:11:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.message.MessageTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 常用语-快捷回复
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_kbase_quick_reply")
public class QuickReplyEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String title;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    // 快捷键
    private String shortCut;

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    @Builder.Default
    @Column(name = "is_enabled")
    private boolean enabled = true;

    @Builder.Default
    @Column(name = "reply_type", nullable = false)
    private String type = MessageTypeEnum.TEXT.name();

    // 是否开启自动同步到llm_qa问答
    @Builder.Default
    @Column(name = "is_auto_sync_llm_qa")
    private boolean autoSyncLlmQa = false;

    // 是否已经同步llm问答
    @Builder.Default
    @Column(name = "is_llm_qa_synced")
    private boolean llmQaSynced = false;

    // 同步到llm qa kbUid 
    @Column(name = "llm_kb_uid")
    private String llmQaKbUid;

    // 同步到llm qa uid
    @Column(name = "llm_qa_uid")
    private String llmQaUid;

    // 被点击次数
    @Builder.Default
    private Integer clickCount = 0;

    // 有效开始日期
    private LocalDateTime startDate;

    // 有效结束日期
    private LocalDateTime endDate;

    private String categoryUid; // 文章分类

    private String kbUid; // 对应知识库

    // 某人工客服快捷回复知识库
    private String agentUid;

}
