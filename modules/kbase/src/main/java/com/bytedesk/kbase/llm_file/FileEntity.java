/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 10:54:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_file;

import java.time.LocalDateTime;
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

    private String fileName;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    // 对应 uploadEntity 的 fileUrl
    private String fileUrl;

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    // 是否启用，状态：启用/禁用
    @Builder.Default
    @Column(name = "is_enabled")
    private boolean enabled = true;

     // 有效开始日期
    @Builder.Default
    private LocalDateTime startDate = LocalDateTime.now();

    // 有效结束日期
    // 当前 + 100 年
    @Builder.Default
    private LocalDateTime endDate = LocalDateTime.now().plusYears(100);

    // 是否开启自动生成llm问答
    // @Builder.Default
    // @Column(name = "is_auto_generate_llm_qa")
    // private boolean autoGenerateLlmQa = false;

    // // 是否已经生成llm问答
    // @Builder.Default
    // @Column(name = "is_llm_qa_generated")
    // private boolean llmQaGenerated = false;

    // // 是否开启自动删除llm问答
    // @Builder.Default
    // @Column(name = "is_auto_delete_llm_qa")
    // private boolean autoDeleteLlmQa = false;

    // // 是否已经删除llm问答
    // @Builder.Default
    // @Column(name = "is_llm_qa_deleted")
    // private boolean llmQaDeleted = false;

    // // 是否开启自动llm Chunk切块
    // @Builder.Default
    // @Column(name = "is_auto_llm_Chunk")
    // private boolean autoLlmChunk = false;

    // // 是否已经自动llm Chunk切块
    // @Builder.Default
    // @Column(name = "is_llm_Chunked")
    // private boolean llmChunked = false;

    // // 是否开启自动删除llm Chunk切块
    // @Builder.Default
    // @Column(name = "is_auto_delete_llm_Chunk")
    // private boolean autoDeleteLlmChunk = false;

    // // 是否已经删除llm Chunk切块
    // @Builder.Default
    // @Column(name = "is_llm_Chunk_deleted")
    // private boolean llmChunkDeleted = false;

    @Builder.Default
    private String status = ChunkStatusEnum.NEW.name();

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
    public FileEntity setSuccess() {
        this.setStatus(ChunkStatusEnum.SUCCESS.name());
        return this;
    }

    // set Error
    public FileEntity setError() {
        this.setStatus(ChunkStatusEnum.ERROR.name());
        return this;
    }
}
