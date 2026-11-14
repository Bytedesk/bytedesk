package com.bytedesk.core.group.forum;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupForumPostRepository extends JpaRepository<GroupForumPostEntity, Long> {

    Optional<GroupForumPostEntity> findByUid(String uid);

    Optional<GroupForumPostEntity> findByMessageUid(String messageUid);

    /** 帖子分页（含已删除占位，前端标记展示） */
    Page<GroupForumPostEntity> findByOrgUidAndGroupUidOrderByCreatedAtDesc(
            String orgUid, String groupUid, Pageable pageable);

    /** 批量取当页帖子的评论数（含回复） */
    @Query("SELECT c.postUid, COUNT(c) FROM GroupForumCommentEntity c "
            + "WHERE c.orgUid = :orgUid AND c.postUid IN :postUids AND c.deleted = false "
            + "GROUP BY c.postUid")
    List<Object[]> countCommentsByPostUids(@Param("orgUid") String orgUid,
            @Param("postUids") List<String> postUids);
}
