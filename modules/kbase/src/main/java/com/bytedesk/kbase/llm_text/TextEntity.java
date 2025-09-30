/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-03 09:29:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.utils.BdDateUtils;

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

    private static final long serialVersionUID = 1L;


    private String title;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    // 文本类型，TODO: 后期支持OCR提取图片文字、音频转录、视频文字提取等
    @Builder.Default
    @Column(name = "text_type")
    private String type = MessageTypeEnum.TEXT.name();

    @Builder.Default
    private String elasticStatus = ChunkStatusEnum.NEW.name();

    @Builder.Default
    private String vectorStatus = ChunkStatusEnum.NEW.name();

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    // 是否启用，状态：启用/禁用
    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    // 有效开始日期
    @Builder.Default
    private ZonedDateTime startDate = BdDateUtils.now();

    // 有效结束日期
    // 当前 + 100 年
    @Builder.Default
    private ZonedDateTime endDate = BdDateUtils.now().plusYears(100);

    private String categoryUid; // 所属分类

    // 替换kbUid为KbaseEntity
    @ManyToOne(fetch = FetchType.LAZY)
    private KbaseEntity kbase;

    // vector store id
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> docIdList = new ArrayList<>();


    // set Success
    public TextEntity setElasticSuccess() {
        this.setElasticStatus(ChunkStatusEnum.SUCCESS.name());
        return this;
    }

    // set Error
    public TextEntity setElasticError() {
        this.setElasticStatus(ChunkStatusEnum.ERROR.name());
        return this;
    }

    // set Vector Success
    public TextEntity setVectorSuccess() {
        this.setVectorStatus(ChunkStatusEnum.SUCCESS.name());
        return this;
    }

    // set Vector Error
    public TextEntity setVectorError() {
        this.setVectorStatus(ChunkStatusEnum.ERROR.name());
        return this;
    }


    /**
     * 判断内容是否有变化
     * 只有当关键内容发生变化时，才会触发更新向量索引
     * @param request TextRequest 请求对象
     * @return 如果关键内容有变化返回 true，否则返回 false
     */
    public Boolean hasChanged(TextRequest request) {
        // 比较标题是否变化
        if ((title == null && request.getTitle() != null) ||
            (title != null && !title.equals(request.getTitle()))) {
            return true;
        }
        
        // 比较内容是否变化
        if ((content == null && request.getContent() != null) ||
            (content != null && request.getContent() != null && !content.equals(request.getContent()))) {
            return true;
        }
        
        // 比较标签列表是否变化
        if ((tagList == null && request.getTagList() != null && !request.getTagList().isEmpty()) ||
            (tagList != null && request.getTagList() == null) ||
            (tagList != null && request.getTagList() != null && !tagList.equals(request.getTagList()))) {
            return true;
        }

        // enabled
        if (enabled != request.getEnabled()) {
            return true;
        }
        
        // StartDate
        if (startDate != null && request.getStartDate() != null && !startDate.equals(request.getStartDate())) {
            return true;
        }
        
        // EndDate
        if (endDate != null && request.getEndDate() != null && !endDate.equals(request.getEndDate())) {
            return true;
        }

        // 所有字段都没有变化
        return false;
    }
}
