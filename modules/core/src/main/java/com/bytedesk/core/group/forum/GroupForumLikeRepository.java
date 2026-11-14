package com.bytedesk.core.group.forum;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupForumLikeRepository extends JpaRepository<GroupForumLikeEntity, Long> {

    Optional<GroupForumLikeEntity> findByPostUidAndUserUid(String postUid, String userUid);

    boolean existsByPostUidAndUserUid(String postUid, String userUid);

    /** 批量取当页帖子的点赞数 */
    @Query("SELECT l.postUid, COUNT(l) FROM GroupForumLikeEntity l "
            + "WHERE l.orgUid = :orgUid AND l.postUid IN :postUids "
            + "GROUP BY l.postUid")
    List<Object[]> countLikesByPostUids(@Param("orgUid") String orgUid,
            @Param("postUids") List<String> postUids);

    /** 批量判断当前用户对当页帖子的点赞状态 */
    @Query("SELECT l.postUid FROM GroupForumLikeEntity l "
            + "WHERE l.orgUid = :orgUid AND l.userUid = :userUid AND l.postUid IN :postUids")
    List<String> findLikedPostUids(@Param("orgUid") String orgUid, @Param("userUid") String userUid,
            @Param("postUids") List<String> postUids);
}
