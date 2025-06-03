/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:16:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 15:58:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

import com.bytedesk.kbase.kbase.KbaseTypeEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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

    @PrePersist
    public void prePersist() {
        if (getType() == null) {
            setType(KbaseTypeEnum.HELPCENTER.name());
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

    public ArticleEntity setElasticSuccess() {
        this.setElasticStatus(ArticleStatusEnum.SUCCESS.name());
        return this;
    }

    public ArticleEntity setElasticError() {
        this.setElasticStatus(ArticleStatusEnum.ERROR.name());
        return this;
    }

    public ArticleEntity setVectorSuccess() {
        this.setVectorStatus(ArticleStatusEnum.SUCCESS.name());
        return this;
    }
    
    public ArticleEntity setVectorError() {
        this.setVectorStatus(ArticleStatusEnum.ERROR.name());
        return this;
    }

    // elastic 索引状态
    public boolean isElasticStatusSuccess() {
        return "SUCCESS".equals(getElasticStatus());
    }

    /**
     * 检查文章是否已经完成向量索引
     * @return true if vectorStatus is SUCCESS
     */
    public boolean isVectorIndexed() {
        return "SUCCESS".equals(getVectorStatus());
    }
}
