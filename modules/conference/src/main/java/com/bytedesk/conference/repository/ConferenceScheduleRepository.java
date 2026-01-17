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

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import com.bytedesk.conference.entity.ScheduleEntity;
// import com.bytedesk.conference.entity.ScheduleEntity.RepeatType;

// /**
//  * Schedule Repository
//  *
//  * 日程数据访问接口
//  */
// @Repository
// public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long>, JpaSpecificationExecutor<ScheduleEntity> {

//     /**
//      * 根据UID查找日程
//      */
//     ScheduleEntity findByUid(String uid);

//     /**
//      * 检查UID是否存在
//      */
//     Boolean existsByUid(String uid);

//     /**
//      * 根据主持人ID查找日程列表
//      */
//     List<ScheduleEntity> findByHostUidOrderByStartTimeAsc(String hostUid);

//     /**
//      * 根据会议ID查找日程
//      */
//     ScheduleEntity findByConferenceUid(String conferenceUid);

//     /**
//      * 查找指定时间范围内的日程
//      */
//     @Query("SELECT s FROM ScheduleEntity s WHERE s.startTime >= :startTime AND s.endTime <= :endTime ORDER BY s.startTime ASC")
//     List<ScheduleEntity> findByStartTimeBetween(
//         @Param("startTime") LocalDateTime startTime,
//         @Param("endTime") LocalDateTime endTime
//     );

//     /**
//      * 查找用户指定时间范围内的日程
//      */
//     @Query("SELECT s FROM ScheduleEntity s WHERE s.hostUid = :hostUid AND s.startTime >= :startTime AND s.endTime <= :endTime ORDER BY s.startTime ASC")
//     List<ScheduleEntity> findByHostUidAndStartTimeBetween(
//         @Param("hostUid") String hostUid,
//         @Param("startTime") LocalDateTime startTime,
//         @Param("endTime") LocalDateTime endTime
//     );

//     /**
//      * 查找即将开始的日程（需要提醒的）
//      */
//     @Query("SELECT s FROM ScheduleEntity s WHERE s.reminderSent = false AND s.startTime BETWEEN :startTime AND :endTime ORDER BY s.startTime ASC")
//     List<ScheduleEntity> findUpcomingSchedules(
//         @Param("startTime") LocalDateTime startTime,
//         @Param("endTime") LocalDateTime endTime
//     );

//     /**
//      * 查找重复日程
//      */
//     List<ScheduleEntity> findByRepeatTypeNotOrderByStartTimeAsc(RepeatType repeatType);

//     /**
//      * 统计用户的日程数量
//      */
//     Long countByHostUid(String hostUid);

//     /**
//      * 删除会议的所有日程
//      */
//     void deleteByConferenceUid(String conferenceUid);
// }
