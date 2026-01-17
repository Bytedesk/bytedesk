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

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import com.bytedesk.conference.entity.ConferenceEntity;
// import com.bytedesk.conference.entity.ConferenceEntity.ConferenceStatus;
// import com.bytedesk.conference.entity.ConferenceEntity.ConferenceType;

// /**
//  * Conference Repository
//  *
//  * 会议数据访问接口
//  */
// @Repository
// public interface ConferenceRepository extends JpaRepository<ConferenceEntity, Long>, JpaSpecificationExecutor<ConferenceEntity> {

//     /**
//      * 根据UID查找会议
//      */
//     Optional<ConferenceEntity> findByUid(String uid);

//     /**
//      * 检查UID是否存在
//      */
//     Boolean existsByUid(String uid);

//     /**
//      * 根据房间ID查找会议
//      */
//     Optional<ConferenceEntity> findByRoomId(String roomId);

//     /**
//      * 根据主持人ID查找会议列表
//      */
//     List<ConferenceEntity> findByHostUidOrderByCreatedAtDesc(String hostUid);

//     /**
//      * 根据状态查找会议列表
//      */
//     List<ConferenceEntity> findByStatusOrderByStartTimeDesc(ConferenceStatus status);

//     /**
//      * 根据类型查找会议列表
//      */
//     List<ConferenceEntity> findByTypeOrderByStartTimeDesc(ConferenceType type);

//     /**
//      * 查找用户的会议历史（作为主持人或参与者）
//      */
//     @Query("SELECT c FROM ConferenceEntity c WHERE c.hostUid = :userUid OR c.uid IN (SELECT p.conferenceUid FROM ParticipantEntity p WHERE p.userUid = :userUid) ORDER BY c.createdAt DESC")
//     List<ConferenceEntity> findUserConferences(@Param("userUid") String userUid);

//     /**
//      * 查找进行中的会议
//      */
//     List<ConferenceEntity> findByStatusAndStartTimeBeforeAndEndTimeAfter(
//         ConferenceStatus status,
//         LocalDateTime startTime,
//         LocalDateTime endTime
//     );

//     /**
//      * 查找即将开始的会议（指定时间范围内）
//      */
//     @Query("SELECT c FROM ConferenceEntity c WHERE c.status = :status AND c.startTime BETWEEN :startTime AND :endTime ORDER BY c.startTime ASC")
//     List<ConferenceEntity> findUpcomingConferences(
//         @Param("status") ConferenceStatus status,
//         @Param("startTime") LocalDateTime startTime,
//         @Param("endTime") LocalDateTime endTime
//     );

//     /**
//      * 查找已结束的会议（指定时间范围内）
//      */
//     @Query("SELECT c FROM ConferenceEntity c WHERE c.status = :status AND c.endTime BETWEEN :startTime AND :endTime ORDER BY c.endTime DESC")
//     List<ConferenceEntity> findEndedConferences(
//         @Param("status") ConferenceStatus status,
//         @Param("startTime") LocalDateTime startTime,
//         @Param("endTime") LocalDateTime endTime
//     );

//     /**
//      * 统计用户创建的会议数量
//      */
//     Long countByHostUid(String hostUid);

//     /**
//      * 统计指定状态的会议数量
//      */
//     Long countByStatus(ConferenceStatus status);
// }
