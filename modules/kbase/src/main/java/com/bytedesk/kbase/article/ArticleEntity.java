/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:16:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-22 09:21:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.kbase.kbase.KbaseTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;

/**
 * 帮助文档
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@EntityListeners({ ArticleEntityListener.class })
@Table(name = "bytedesk_kbase_article")
public class ArticleEntity extends AbstractArticleEntity {

    private static final long serialVersionUID = 1L;

    // public Document toDocument(@NonNull ArticleEntity article) {
    //     return new Document(article.getTitle() + article.getContentMarkdown(),
    //             Map.of("categoryUid", article.getCategoryUid(), "kbUid", article.getKbUid()));
    // }

    @PrePersist
    public void prePersist() {
        if (getType() == null) {
            setType(KbaseTypeEnum.HELPCENTER.name());
        }
        if (getStatus() == null) {
            setStatus(ArticleStatusEnum.DRAFT.name());
        }
        if (getAuditStatus() == null) {
            setAuditStatus(ArticleAuditStatusEnum.PENDING.name());
        }
    }
    
    @PostLoad
    public void postLoad() {
        if (getType() == null) {
            setType(KbaseTypeEnum.HELPCENTER.name());
        }
    }
}
