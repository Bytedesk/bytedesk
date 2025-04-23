/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 17:09:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.text;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.llm.split.SplitStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({TextEntityListener.class})    
@Table(name = "bytedesk_kbase_llm_text")
public class TextEntity extends BaseEntity {

    private String name;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    // 文本类型，TODO: 后期支持OCR提取图片文字、音频转录、视频文字提取等
    @Builder.Default
    @Column(name = "text_type")
    private String type = MessageTypeEnum.TEXT.name();

    @Builder.Default
    private String status = SplitStatusEnum.NEW.name();

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    // 是否启用，状态：启用/禁用
    @Builder.Default
    @Column(name = "is_enabled")
    private boolean enabled = true;

    // 是否开启自动生成llm问答
    @Builder.Default
    @Column(name = "is_auto_generate_llm_qa")
    private boolean autoGenerateLlmQa = false;

    // 是否已经生成llm问答
    @Builder.Default
    @Column(name = "is_llm_qa_generated")
    private boolean llmQaGenerated = false;

    // is auto delete llm qa
    @Builder.Default
    @Column(name = "is_auto_delete_llm_qa")
    private boolean autoDeleteLlmQa = false;

    // 是否已经删除llm问答
    @Builder.Default
    @Column(name = "is_llm_qa_deleted")
    private boolean llmQaDeleted = false;

    // 是否开启自动llm split切块
    @Builder.Default
    @Column(name = "is_auto_llm_split")
    private boolean autoLlmSplit = false;

    // 是否已经自动llm split切块
    @Builder.Default
    @Column(name = "is_llm_splitted")
    private boolean llmSplitted = false;

    // is auto delete llm split
    @Builder.Default
    @Column(name = "is_auto_delete_llm_split")
    private boolean autoDeleteLlmSplit = false;

    // 是否已经删除llm split切块
    @Builder.Default
    @Column(name = "is_llm_split_deleted")
    private boolean llmSplitDeleted = false;

    // 有效开始日期
    @Builder.Default
    private LocalDateTime startDate = LocalDateTime.now();

    // 有效结束日期
    // 当前 + 100 年
    @Builder.Default
    private LocalDateTime endDate = LocalDateTime.now().plusYears(100);

    private String categoryUid; // 所属分类

    // 替换kbUid为KbaseEntity
    @ManyToOne(fetch = FetchType.LAZY)
    private KbaseEntity kbaseEntity;

    // vector store id
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> docIdList = new ArrayList<>();

    

}
