/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:16:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-22 09:22:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article_archive;

import com.bytedesk.kbase.kbase.KbaseTypeEnum;
import com.bytedesk.kbase.article.AbstractArticleEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 帮助文档: 文章归档, 历史版本
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@EntityListeners({ ArticleArchiveEntityListener.class })
@Table(name = "bytedesk_kbase_article_archive")
public class ArticleArchiveEntity extends AbstractArticleEntity {

    private static final long serialVersionUID = 1L;

    @PrePersist
    public void prePersist() {
        if (getType() == null) {
            setType(KbaseTypeEnum.HELPCENTER.name());
        }
        if (getElasticStatus() == null) {
            setElasticStatus(ArticleArchiveStatusEnum.DRAFT.name());
        }
        if (getAuditStatus() == null) {
            setAuditStatus(ArticleArchiveAuditStatusEnum.PENDING.name());
        }
    }
    
    @PostLoad
    public void postLoad() {
        if (getType() == null) {
            setType(KbaseTypeEnum.HELPCENTER.name());
        }
    }

    // public Document toDocument(@NonNull ArticleArchiveEntity article_archive) {
    //     return new Document(article_archive.getTitle() + article_archive.getContentMarkdown(),
    //             Map.of("categoryUid", article_archive.getCategoryUid(), "kbUid", article_archive.getKbUid()));
    // }

}
