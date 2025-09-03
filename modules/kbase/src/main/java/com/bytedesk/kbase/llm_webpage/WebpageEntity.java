/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-03 09:30:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_webpage;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;
import com.bytedesk.core.utils.BdDateUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
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
@Table(name = "bytedesk_kbase_llm_webpage")
public class WebpageEntity extends BaseEntity {

    // 网页标题
    private String title;

    // 网站地址
    @Column(name = "webpage_url")
    private String url;

    // 网站描述
    private String description;

    // 抓取内容
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    // 网站类型
    // @Builder.Default
    // @Column(name = "webpage_type")
    // private String type = MessageTypeEnum.TEXT.name();

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
    
    // 页面浏览统计
    @Builder.Default
    private Integer viewCount = 0;
    
    // 点击统计
    @Builder.Default
    private Integer clickCount = 0;


    // set Success
    public WebpageEntity setElasticSuccess() {
        this.setElasticStatus(ChunkStatusEnum.SUCCESS.name());
        return this;
    }

    // set Error
    public WebpageEntity setElasticError() {
        this.setElasticStatus(ChunkStatusEnum.ERROR.name());
        return this;
    }

    // set Vector Success
    public WebpageEntity setVectorSuccess() {
        this.setVectorStatus(ChunkStatusEnum.SUCCESS.name());
        return this;
    }

    // set Vector Error
    public WebpageEntity setVectorError() {
        this.setVectorStatus(ChunkStatusEnum.ERROR.name());
        return this;
    }

    
}
