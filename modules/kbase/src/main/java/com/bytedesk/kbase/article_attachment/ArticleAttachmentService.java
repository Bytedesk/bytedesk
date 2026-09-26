/*
 * @Author: jackning 270580156@qq.com
 * @Description: 文章附件共享服务：统一文章/归档两侧的附件组装与差量更新逻辑（复用，避免两处复制粘贴）
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 * 联系：270580156@qq.com
 */
package com.bytedesk.kbase.article_attachment;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.kbase.article.ArticleEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 文章附件服务
 *
 * 约束（与 TicketAttachmentEntity 设计一致）：
 * 1. create 场景：附件必须在文章首次 save 之前组装进集合，借助 OneToMany cascade ALL 随文章落库，
 *    同时保证 @CachePut 写入缓存的文章实体包含完整附件；
 * 2. update 场景：ArticleRestService.update 无事务，文章已 detached，
 *    附件一律通过 Repository 直接保存（附件实体不配 cascade PERSIST，避免
 *    "detached entity passed to persist"）；
 * 3. 缓存防御：Redis 反序列化后的 attachments 可能为 null，差量更新前必须判空。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleAttachmentService {

    private final ArticleAttachmentRepository articleAttachmentRepository;

    private final UploadRestService uploadRestService;

    private final UidUtils uidUtils;

    // ==================== 文章侧 ====================

    /**
     * 根据上传文件 uid 列表构建文章附件集合（不落库，由文章级联持久化）
     */
    public Set<ArticleAttachmentEntity> buildForArticle(ArticleEntity article, Set<String> uploadUids) {
        Set<ArticleAttachmentEntity> attachments = new HashSet<>();
        if (uploadUids == null || article == null) {
            return attachments;
        }
        for (String uploadUid : uploadUids) {
            Optional<UploadEntity> upload = findUpload(uploadUid);
            if (upload.isPresent()) {
                ArticleAttachmentEntity attachment = new ArticleAttachmentEntity();
                attachment.setUid(uidUtils.getUid());
                attachment.setOrgUid(article.getOrgUid());
                attachment.setArticle(article);
                attachment.setUpload(upload.get());
                //
                attachments.add(attachment);
            }
        }
        return attachments;
    }

    /**
     * 差量更新文章附件：新增缺失的关联，软删除被移除的关联
     */
    public void updateForArticle(ArticleEntity article, Set<String> uploadUids) {
        if (article == null || uploadUids == null) {
            return;
        }
        Set<String> existingUploadUids = existingUploadUids(article.getAttachments());

        for (String uploadUid : uploadUids) {
            if (existingUploadUids.contains(uploadUid)) {
                continue;
            }
            Optional<UploadEntity> upload = findUpload(uploadUid);
            if (upload.isPresent()) {
                ArticleAttachmentEntity attachment = new ArticleAttachmentEntity();
                attachment.setUid(uidUtils.getUid());
                attachment.setArticle(article);
                attachment.setUpload(upload.get());
                articleAttachmentRepository.save(attachment);
                //
                getAttachments(article).add(attachment);
            }
        }

        softDeleteRemoved(article.getAttachments(), uploadUids);
    }

    // ==================== 私有辅助 ====================

    private Optional<UploadEntity> findUpload(String uploadUid) {
        if (!StringUtils.hasText(uploadUid)) {
            return Optional.empty();
        }
        return uploadRestService.findByUid(uploadUid);
    }

    /** 现有有效附件的 uploadUid 集合（触发附件集合与 EAGER upload 加载，保证缓存数据完整） */
    private Set<String> existingUploadUids(Set<ArticleAttachmentEntity> attachments) {
        if (attachments == null) {
            return new HashSet<>();
        }
        return attachments.stream()
                .filter(attachment -> !attachment.isDeleted() && attachment.getUpload() != null)
                .map(attachment -> attachment.getUpload().getUid())
                .collect(Collectors.toSet());
    }

    /** 软删除不在目标列表中的附件关联 */
    private void softDeleteRemoved(Set<ArticleAttachmentEntity> attachments, Set<String> uploadUids) {
        if (attachments == null) {
            return;
        }
        attachments.stream()
                .filter(attachment -> attachment.getUpload() != null
                        && !uploadUids.contains(attachment.getUpload().getUid()))
                .forEach(attachment -> {
                    attachment.setDeleted(true);
                    articleAttachmentRepository.save(attachment);
                });
    }

    /** 缓存反序列化后 attachments 可能为 null（未初始化集合序列化为 null），统一兜底为可变集合 */
    private Set<ArticleAttachmentEntity> getAttachments(ArticleEntity article) {
        if (article.getAttachments() == null) {
            article.setAttachments(new HashSet<>());
        }
        return article.getAttachments();
    }

}
