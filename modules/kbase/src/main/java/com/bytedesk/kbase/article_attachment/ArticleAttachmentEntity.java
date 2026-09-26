/*
 * @Author: jackning 270580156@qq.com
 * @Description: 文章附件实体（参考 TicketAttachmentEntity 设计）：文章正文之外的独立附件
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 * 联系：270580156@qq.com
 */
package com.bytedesk.kbase.article_attachment;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.kbase.article.ArticleEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Article attachment entity - 文章附件
 * 关联文章与上传文件，支持正文之外的独立附件（如截图、文件）
 *
 * 注：attachments 集合不能上提到 AbstractArticleEntity —— @MappedSuperclass 不能作为关联目标，
 * mappedBy 属主类型校验要求关联目标必须是具体实体（@Entity）。
 *
 * Database Table: bytedesk_kbase_article_attachment
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true, exclude = { "article", "upload" })
@ToString(callSuper = true, exclude = { "article", "upload" })
@Entity(name = "bytedesk_kbase_article_attachment")
@NoArgsConstructor
@AllArgsConstructor
public class ArticleAttachmentEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 防止 Jackson 序列化时 article -> attachments -> article 循环引用
     * （ArticleEntity 有 Redis JSON 缓存，必须断开反向引用）
     *
     * 注意：不配置 cascade PERSIST —— ArticleRestService.update 无事务，
     * 文章实体在保存附件时已处于 detached 状态，级联 persist 会抛
     * "detached entity passed to persist"；附件一律通过 Repository 直接保存
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private ArticleEntity article;

    /**
     * upload 保持默认 EAGER 加载（与 TicketAttachmentEntity 一致）：
     * ArticleEntity 走 Redis JSON 缓存，未初始化的懒加载代理会被序列化为 null，
     * EAGER 可确保附件集合加载时 upload 一并就绪，缓存反序列化后仍可正常转换
     */
    @ManyToOne
    private UploadEntity upload;
}
