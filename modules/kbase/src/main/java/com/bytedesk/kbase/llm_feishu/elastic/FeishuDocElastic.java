/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-20 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_feishu.elastic;

import org.springframework.data.annotation.Id;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.util.StringUtils;

import com.bytedesk.kbase.llm_feishu.FeishuDocEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bytedesk_kbase_feishu_doc")
public class FeishuDocElastic {

    @Id
    private String uid;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Keyword)
    private String orgUid;

    @Field(type = FieldType.Keyword)
    private String kbUid;

    @Field(type = FieldType.Keyword)
    private String categoryUid;

    @Field(type = FieldType.Keyword)
    private String sourceType;

    @Field(type = FieldType.Keyword)
    private String objType;

    @Field(type = FieldType.Keyword)
    private String sourceUid;

    @Field(type = FieldType.Keyword)
    private String sourceUrl;

    @Field(type = FieldType.Keyword)
    private String language;

    @Field(type = FieldType.Boolean)
    private Boolean enabled;

    public static FeishuDocElastic fromEntity(FeishuDocEntity entity) {
        if (entity == null) {
            return null;
        }
        String kbUid = entity.getKbUid();
        if (!StringUtils.hasText(kbUid)) {
            throw new IllegalArgumentException("kbUid is required for indexing feishu doc uid=" + entity.getUid());
        }
        return FeishuDocElastic.builder()
                .uid(entity.getUid())
                .title(entity.getTitle())
                .content(entity.getContent())
                .orgUid(entity.getOrgUid())
                .kbUid(kbUid)
                .categoryUid(entity.getCategoryUid())
                .sourceType("FEISHU")
                .objType(entity.getObjType())
                .sourceUid(entity.getUid())
                .sourceUrl(entity.getSourceUrl())
                // P0 不区分语言；language 为空时统一检索不过滤
                .language(null)
                .enabled(entity.getEnabled())
                .build();
    }
}
