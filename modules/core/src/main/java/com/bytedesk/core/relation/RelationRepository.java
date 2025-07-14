/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-20 12:52:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.relation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RelationRepository extends JpaRepository<RelationEntity, Long>, JpaSpecificationExecutor<RelationEntity> {

    Optional<RelationEntity> findByUid(String uid);

    Boolean existsByUid(String uid);

    Optional<RelationEntity> findByNameAndOrgUidAndTypeAndDeletedFalse(String name, String orgUid, String type);

    // ==================== 社交关系查询方法 ====================
    
    /**
     * 根据主体用户ID和关系类型查询关系
     */
    List<RelationEntity> findBySubjectUserUidAndTypeAndDeletedFalse(String subjectUserUid, String type);
    
    /**
     * 根据客体用户ID和关系类型查询关系
     */
    List<RelationEntity> findByObjectUserUidAndTypeAndDeletedFalse(String objectUserUid, String type);
    
    /**
     * 根据客体内容ID和关系类型查询关系
     */
    List<RelationEntity> findByObjectContentUidAndTypeAndDeletedFalse(String objectContentUid, String type);
    
    /**
     * 查询用户关注的其他用户
     */
    @Query("SELECT r FROM RelationEntity r WHERE r.subjectUserUid = :userId AND r.type = 'FOLLOW' AND r.deleted = false")
    List<RelationEntity> findFollowingsByUserId(@Param("userId") String userId);
    
    /**
     * 查询关注用户的粉丝
     */
    @Query("SELECT r FROM RelationEntity r WHERE r.objectUserUid = :userId AND r.type = 'FOLLOW' AND r.deleted = false")
    List<RelationEntity> findFollowersByUserId(@Param("userId") String userId);
    
    /**
     * 查询互相关注的好友
     */
    @Query("SELECT r FROM RelationEntity r WHERE r.subjectUserUid = :userId AND r.type = 'FRIEND' AND r.deleted = false")
    List<RelationEntity> findFriendsByUserId(@Param("userId") String userId);
    
    /**
     * 查询用户点赞的内容
     */
    @Query("SELECT r FROM RelationEntity r WHERE r.subjectUserUid = :userId AND r.type = 'LIKE' AND r.deleted = false")
    List<RelationEntity> findLikesByUserId(@Param("userId") String userId);
    
    /**
     * 查询用户收藏的内容
     */
    @Query("SELECT r FROM RelationEntity r WHERE r.subjectUserUid = :userId AND r.type = 'FAVORITE' AND r.deleted = false")
    List<RelationEntity> findFavoritesByUserId(@Param("userId") String userId);
    
    /**
     * 查询内容被点赞的关系
     */
    @Query("SELECT r FROM RelationEntity r WHERE r.objectContentUid = :contentId AND r.type = 'LIKE' AND r.deleted = false")
    List<RelationEntity> findLikesByContentId(@Param("contentId") String contentId);
    
    /**
     * 查询内容被收藏的关系
     */
    @Query("SELECT r FROM RelationEntity r WHERE r.objectContentUid = :contentId AND r.type = 'FAVORITE' AND r.deleted = false")
    List<RelationEntity> findFavoritesByContentId(@Param("contentId") String contentId);
    
    /**
     * 检查两个用户之间是否存在特定类型的关系
     */
    @Query("SELECT COUNT(r) > 0 FROM RelationEntity r WHERE r.subjectUserUid = :subjectUserId AND r.objectUserUid = :objectUserId AND r.type = :type AND r.deleted = false")
    Boolean existsRelationBetweenUsers(@Param("subjectUserId") String subjectUserId, 
                                      @Param("objectUserId") String objectUserId, 
                                      @Param("type") String type);
    
    /**
     * 检查用户是否关注了另一个用户
     */
    @Query("SELECT COUNT(r) > 0 FROM RelationEntity r WHERE r.subjectUserUid = :subjectUserId AND r.objectUserUid = :objectUserId AND r.type = 'FOLLOW' AND r.deleted = false")
    Boolean isFollowing(@Param("subjectUserId") String subjectUserId, 
                       @Param("objectUserId") String objectUserId);
    
    /**
     * 检查用户是否点赞了某个内容
     */
    @Query("SELECT COUNT(r) > 0 FROM RelationEntity r WHERE r.subjectUserUid = :userId AND r.objectContentUid = :contentId AND r.type = 'LIKE' AND r.deleted = false")
    Boolean hasLiked(@Param("userId") String userId, 
                    @Param("contentId") String contentId);
    
    /**
     * 检查用户是否收藏了某个内容
     */
    @Query("SELECT COUNT(r) > 0 FROM RelationEntity r WHERE r.subjectUserUid = :userId AND r.objectContentUid = :contentId AND r.type = 'FAVORITE' AND r.deleted = false")
    Boolean hasFavorited(@Param("userId") String userId, 
                        @Param("contentId") String contentId);
    
    /**
     * 分页查询用户关注的其他用户
     */
    @Query("SELECT r FROM RelationEntity r WHERE r.subjectUserUid = :userId AND r.type = 'FOLLOW' AND r.deleted = false")
    Page<RelationEntity> findFollowingsByUserIdPageable(@Param("userId") String userId, Pageable pageable);
    
    /**
     * 分页查询关注用户的粉丝
     */
    @Query("SELECT r FROM RelationEntity r WHERE r.objectUserUid = :userId AND r.type = 'FOLLOW' AND r.deleted = false")
    Page<RelationEntity> findFollowersByUserIdPageable(@Param("userId") String userId, Pageable pageable);
    
    /**
     * 分页查询用户点赞的内容
     */
    @Query("SELECT r FROM RelationEntity r WHERE r.subjectUserUid = :userId AND r.type = 'LIKE' AND r.deleted = false")
    Page<RelationEntity> findLikesByUserIdPageable(@Param("userId") String userId, Pageable pageable);
    
    /**
     * 分页查询用户收藏的内容
     */
    @Query("SELECT r FROM RelationEntity r WHERE r.subjectUserUid = :userId AND r.type = 'FAVORITE' AND r.deleted = false")
    Page<RelationEntity> findFavoritesByUserIdPageable(@Param("userId") String userId, Pageable pageable);
    
    /**
     * 统计用户关注数量
     */
    @Query("SELECT COUNT(r) FROM RelationEntity r WHERE r.subjectUserUid = :userId AND r.type = 'FOLLOW' AND r.deleted = false")
    Long countFollowingsByUserId(@Param("userId") String userId);
    
    /**
     * 统计用户粉丝数量
     */
    @Query("SELECT COUNT(r) FROM RelationEntity r WHERE r.objectUserUid = :userId AND r.type = 'FOLLOW' AND r.deleted = false")
    Long countFollowersByUserId(@Param("userId") String userId);
    
    /**
     * 统计内容点赞数量
     */
    @Query("SELECT COUNT(r) FROM RelationEntity r WHERE r.objectContentUid = :contentId AND r.type = 'LIKE' AND r.deleted = false")
    Long countLikesByContentId(@Param("contentId") String contentId);
    
    /**
     * 统计内容收藏数量
     */
    @Query("SELECT COUNT(r) FROM RelationEntity r WHERE r.objectContentUid = :contentId AND r.type = 'FAVORITE' AND r.deleted = false")
    Long countFavoritesByContentId(@Param("contentId") String contentId);
}
