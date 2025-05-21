/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 14:18:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 14:18:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.DateFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chunk向量索引实体类
 * 用于在Elasticsearch中存储Chunk的向量表示
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bytedesk_chunk_vector")
public class ChunkVector {
    
    @Id
    private String uid;
    
    @Field(type = FieldType.Text)
    private String name;
    
    @Field(type = FieldType.Text)
    private String content;
    
    @Field(type = FieldType.Keyword)
    private String type;
    
    @Field(type = FieldType.Keyword)
    private List<String> tagList;
    
    @Field(type = FieldType.Keyword)
    private String orgUid;
    
    @Field(type = FieldType.Keyword)
    private String kbUid;
    
    @Field(type = FieldType.Keyword)
    private String categoryUid;
    
    @Field(type = FieldType.Boolean)
    private Boolean enabled;

    // 向量嵌入存储
    @Field(type = FieldType.Dense_Vector, dims = 1536)
    private float[] contentEmbedding;
    
    // 文档ID
    @Field(type = FieldType.Keyword)
    private String docId;

    // 文件ID
    @Field(type = FieldType.Keyword)
    private String fileUid;
    
    // 有效日期范围
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime startDate;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime endDate;
    
    // 状态字段
    @Field(type = FieldType.Keyword)
    private String status;
    
    @Field(type = FieldType.Keyword)
    private String vectorStatus;
    
    /**
     * 从ChunkEntity创建ChunkVector实体的静态方法
     * 注意：向量嵌入需要单独计算并设置
     */
    public static ChunkVector fromChunkEntity(ChunkEntity chunk) {
        String kbUid = "";
        String fileUid = "";
        
        if (chunk.getKbase() != null) {
            kbUid = chunk.getKbase().getUid();
        }
        
        if (chunk.getFile() != null) {
            fileUid = chunk.getFile().getUid();
        }
        
        return ChunkVector.builder()
            .uid(chunk.getUid())
            .name(chunk.getName())
            .content(chunk.getContent())
            .type(chunk.getType())
            .tagList(chunk.getTagList())
            .orgUid(chunk.getOrgUid())
            .kbUid(kbUid)
            .categoryUid(chunk.getCategoryUid())
            .enabled(chunk.getEnabled())
            .docId(chunk.getDocId())
            .fileUid(fileUid)
            .startDate(chunk.getStartDate())
            .endDate(chunk.getEndDate())
            .status(chunk.getStatus())
            .vectorStatus(chunk.getVectorStatus())
            .build();
    }
}
