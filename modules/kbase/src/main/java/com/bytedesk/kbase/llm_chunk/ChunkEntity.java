/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 13:19:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.kbase.llm_file.FileEntity;
import com.bytedesk.kbase.kbase.KbaseEntity;

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
import com.bytedesk.core.utils.BdDateUtils;

/**
 * 拆分实体
 * 用于向量检索：文件、文本、网站等所有拆分内容全部存储在此
 * 向量检索内容全部放到且仅放在此表中
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ChunkEntityListener.class})
@Table(name = "bytedesk_kbase_llm_chunk")
public class ChunkEntity extends BaseEntity {

    private String name;

    // chunk 之后可能不需要这么长的 content，待优化
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    @Builder.Default
    @Column(name = "chunk_type")
    private String type = ChunkTypeEnum.FILE.name();

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    @Builder.Default
    private String elasticStatus = ChunkStatusEnum.NEW.name();

    @Builder.Default
    private String vectorStatus = ChunkStatusEnum.NEW.name();

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

    // 对应 拆分 document 的 id
    private String docId;

    // 所属分类
    private String categoryUid;

    // 替换kbUid为KbaseEntity
    @ManyToOne(fetch = FetchType.LAZY)
    private KbaseEntity kbase;

    @ManyToOne(fetch = FetchType.LAZY)
    private FileEntity file;

    // set Success
    public ChunkEntity setElasticSuccess() {
        this.setElasticStatus(ChunkStatusEnum.SUCCESS.name());
        return this;
    }

    // set Error
    public ChunkEntity setElasticError() {
        this.setElasticStatus(ChunkStatusEnum.ERROR.name());
        return this;
    }

    // set vector Success
    public ChunkEntity setVectorSuccess() {
        this.setVectorStatus(ChunkStatusEnum.SUCCESS.name());
        return this;
    }

    // set vector Error
    public ChunkEntity setVectorError() {
        this.setVectorStatus(ChunkStatusEnum.ERROR.name());
        return this;
    }

    /**
     * 判断内容是否有变化
     * 只有当关键内容发生变化时，才会触发更新向量索引
     * @param request ChunkRequest 请求对象
     * @return 如果关键内容有变化返回 true，否则返回 false
     */
    public Boolean hasChanged(ChunkRequest request) {
        // 比较名称是否变化
        if ((name == null && request.getName() != null) ||
            (name != null && !name.equals(request.getName()))) {
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
