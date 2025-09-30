/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-28 15:40:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_file;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;

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

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({FileEntityListener.class})
@Table(name = "bytedesk_kbase_llm_file")
public class FileEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;


    private String fileName;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    // 对应 uploadEntity 的 fileUrl
    private String fileUrl;

    private String fileType;

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

    @Builder.Default
    private String elasticStatus = ChunkStatusEnum.NEW.name();

    @Builder.Default
    private String vectorStatus = ChunkStatusEnum.NEW.name();

    private String categoryUid; // 所属分类

    // 替换kbUid为KbaseEntity
    @ManyToOne(fetch = FetchType.LAZY)
    private KbaseEntity kbase;

    // 对应 uploadEntity 的 uid
    // private String uploadUid;
    // 多对一关联UploadEntity
    @ManyToOne(fetch = FetchType.LAZY)
    private UploadEntity upload;

    // vector store id
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> docIdList = new ArrayList<>();

    // set Success
    public FileEntity setElasticSuccess() {
        this.setElasticStatus(ChunkStatusEnum.SUCCESS.name());
        return this;
    }

    // set Error
    public FileEntity setElasticError() {
        this.setElasticStatus(ChunkStatusEnum.ERROR.name());
        return this;
    }

    // set vector Success
    public FileEntity setVectorSuccess() {
        this.setVectorStatus(ChunkStatusEnum.SUCCESS.name());
        return this;
    }

    // set vector Error
    public FileEntity setVectorError() {
        this.setVectorStatus(ChunkStatusEnum.ERROR.name());
        return this;
    }
    
    /**
     * 判断文件内容是否有变化
     * @param request FileRequest 请求
     * @return 如果文件内容或属性有变化返回 true，否则返回 false
     */
    public Boolean hasChanged(FileRequest request) {
        // 比较文件名是否变化
        if ((fileName == null && request.getFileName() != null) ||
            (fileName != null && !fileName.equals(request.getFileName()))) {
            return true;
        }
        
        // 比较内容是否变化
        if ((content == null && request.getContent() != null) ||
            (content != null && !content.equals(request.getContent()))) {
            return true;
        }
        
        // 比较文件URL是否变化
        if ((fileUrl == null && request.getFileUrl() != null) ||
            (fileUrl != null && !fileUrl.equals(request.getFileUrl()))) {
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
        if ((startDate == null && request.getStartDate() != null) ||
            (startDate != null && request.getStartDate() != null && !startDate.equals(request.getStartDate()))) {
            return true;
        }
        
        // EndDate
        if ((endDate == null && request.getEndDate() != null) ||
            (endDate != null && request.getEndDate() != null && !endDate.equals(request.getEndDate()))) {
            return true;
        }

        // 分类
        if ((categoryUid == null && request.getCategoryUid() != null) ||
            (categoryUid != null && !categoryUid.equals(request.getCategoryUid()))) {
            return true;
        }

        // 所有字段都没有变化
        return false;
    }
}
