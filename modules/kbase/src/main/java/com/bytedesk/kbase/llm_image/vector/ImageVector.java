/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 14:18:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 20:09:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_image.vector;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.bytedesk.kbase.llm_image.ImageEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Image向量索引实体类
 * 用于在Elasticsearch中存储Image的向量表示
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bytedesk_image_vector")
public class ImageVector {
    
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
    
    // 文件名
    @Field(type = FieldType.Keyword)
    private String fileName;
    
    // 文件链接
    @Field(type = FieldType.Keyword)
    private String fileUrl;
    
    // @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    // private ZonedDateTime startDate;

    // @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    // private ZonedDateTime endDate;
    
    // 状态字段
    @Field(type = FieldType.Keyword)
    private String status;
    
    @Field(type = FieldType.Keyword)
    private String vectorStatus;
    
    /**
     * 从ImageEntity创建ImageVector实体的静态方法
     * 注意：向量嵌入需要单独计算并设置
     */
    public static ImageVector fromImageEntity(ImageEntity image) {
        String kbUid = "";
        String fileUid = "";
        String fileName = "";
        String fileUrl = "";
        
        if (image.getKbase() != null) {
            kbUid = image.getKbase().getUid();
        }
        
        if (image.getFile() != null) {
            fileUid = image.getFile().getUid();
            fileName = image.getFile().getFileName();
            fileUrl = image.getFile().getFileUrl();
        }
        
        return ImageVector.builder()
            .uid(image.getUid())
            .name(image.getName())
            .content(image.getContent())
            .type(image.getType())
            .tagList(image.getTagList())
            .orgUid(image.getOrgUid())
            .kbUid(kbUid)
            .categoryUid(image.getCategoryUid())
            .enabled(image.getEnabled())
            .docId(image.getDocId())
            .fileUid(fileUid)
            .fileName(fileName)
            .fileUrl(fileUrl)
            // .startDate(image.getStartDate())
            // .endDate(image.getEndDate())
            .status(image.getElasticStatus())
            .vectorStatus(image.getVectorStatus())
            .build();
    }
}
