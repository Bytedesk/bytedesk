/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @LastEditors: bytedesk.com
 * @LastEditTime: 2025-01-16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: support@bytedesk.com
 *  联系：support@bytedesk.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.conference.repository;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import com.bytedesk.conference.entity.ParticipantEntity;
// import com.bytedesk.conference.entity.ParticipantEntity.ParticipantRole;
// import com.bytedesk.conference.entity.ParticipantEntity.ParticipantStatus;

// /**
//  * Participant Repository
//  *
//  * 参与者数据访问接口
//  */
// @Repository
// public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long>, JpaSpecificationExecutor<ParticipantEntity> {

//     /**
//      * 根据UID查找参与者
//      */
//     Optional<ParticipantEntity> findByUid(String uid);

//     /**
//      * 检查UID是否存在
//      */
//     Boolean existsByUid(String uid);

//     /**
//      * 根据会议ID查找参与者列表
//      */
//     List<ParticipantEntity> findByConferenceUidOrderByJoinTimeAsc(String conferenceUid);

//     /**
//      * 根据会议ID和用户ID查找参与者
//      */
//     Optional<ParticipantEntity> findByConferenceUidAndUserUid(String conferenceUid, String userUid);

//     /**
//      * 根据会议ID和角色查找参与者列表
//      */
//     List<ParticipantEntity> findByConferenceUidAndRole(String conferenceUid, ParticipantRole role);

//     /**
//      * 根据会议ID和状态查找参与者列表
//      */
//     List<ParticipantEntity> findByConferenceUidAndStatus(String conferenceUid, ParticipantStatus status);

//     /**
//      * 查找会议中的在线参与者
//      */
//     @Query("SELECT p FROM ParticipantEntity p WHERE p.conferenceUid = :conferenceUid AND p.status = :status")
//     List<ParticipantEntity> findOnlineParticipants(
//         @Param("conferenceUid") String conferenceUid,
//         @Param("status") ParticipantStatus status
//     );

//     /**
//      * 统计会议参与者数量
//      */
//     Long countByConferenceUid(String conferenceUid);

//     /**
//      * 统计会议在线参与者数量
//      */
//     @Query("SELECT COUNT(p) FROM ParticipantEntity p WHERE p.conferenceUid = :conferenceUid AND p.status = :status")
//     Long countOnlineParticipants(
//         @Param("conferenceUid") String conferenceUid,
//         @Param("status") ParticipantStatus status
//     );

//     /**
//      * 查找用户参与的所有会议
//      */
//     @Query("SELECT DISTINCT p.conferenceUid FROM ParticipantEntity p WHERE p.userUid = :userUid")
//     List<String> findUserConferenceUids(@Param("userUid") String userUid);

//     /**
//      * 删除会议的所有参与者
//      */
//     void deleteByConferenceUid(String conferenceUid);

//     /**
//      * 根据会议ID和用户ID删除参与者
//      */
//     void deleteByConferenceUidAndUserUid(String conferenceUid, String userUid);
// }
