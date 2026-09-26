/*
 * @Author: jackning 270580156@qq.com
 * @Description: 文章附件 Repository（参考 TicketAttachmentRepository）
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 * 联系：270580156@qq.com
 */
package com.bytedesk.kbase.article_attachment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleAttachmentRepository extends JpaRepository<ArticleAttachmentEntity, Long> {

    List<ArticleAttachmentEntity> findByArticleUidAndDeletedFalse(String articleUid);

    Optional<ArticleAttachmentEntity> findByUid(String uid);
}
