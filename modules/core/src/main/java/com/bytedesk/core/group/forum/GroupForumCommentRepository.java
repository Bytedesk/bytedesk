package com.bytedesk.core.group.forum;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupForumCommentRepository extends JpaRepository<GroupForumCommentEntity, Long> {

    Optional<GroupForumCommentEntity> findByUid(String uid);

    Optional<GroupForumCommentEntity> findByMessageUid(String messageUid);

    /** 根评论分页（parentCommentUid 为空） */
    Page<GroupForumCommentEntity> findByOrgUidAndPostUidAndParentCommentUidIsNullAndDeletedFalseOrderByCreatedAtAsc(
            String orgUid, String postUid, Pageable pageable);

    /** 指定父评论的回复分页（楼中楼） */
    Page<GroupForumCommentEntity> findByOrgUidAndParentCommentUidAndDeletedFalseOrderByCreatedAtAsc(
            String orgUid, String parentCommentUid, Pageable pageable);

    /** 帖子全部评论（含已删除占位，详情组装用） */
    List<GroupForumCommentEntity> findByOrgUidAndPostUidOrderByCreatedAtAsc(String orgUid, String postUid);
}
