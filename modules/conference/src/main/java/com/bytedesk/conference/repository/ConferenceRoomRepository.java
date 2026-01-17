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
// import org.springframework.stereotype.Repository;

// import com.bytedesk.conference.entity.RoomEntity;
// import com.bytedesk.conference.entity.RoomEntity.RoomStatus;
// import com.bytedesk.conference.entity.RoomEntity.RoomType;

// /**
//  * Room Repository
//  *
//  * 会议室数据访问接口
//  */
// @Repository
// public interface RoomRepository extends JpaRepository<RoomEntity, Long>, JpaSpecificationExecutor<RoomEntity> {

//     /**
//      * 根据UID查找会议室
//      */
//     Optional<RoomEntity> findByUid(String uid);

//     /**
//      * 检查UID是否存在
//      */
//     Boolean existsByUid(String uid);

//     /**
//      * 根据主持人ID查找会议室列表
//      */
//     List<RoomEntity> findByHostUidOrderByCreatedAtDesc(String hostUid);

//     /**
//      * 根据状态查找会议室列表
//      */
//     List<RoomEntity> findByStatusOrderByCreatedAtDesc(RoomStatus status);

//     /**
//      * 根据类型查找会议室列表
//      */
//     List<RoomEntity> findByTypeOrderByCreatedAtDesc(RoomType type);

//     /**
//      * 查找活跃的会议室
//      */
//     List<RoomEntity> findByStatusAndCurrentParticipantsLessThanOrderByCreatedAtAsc(
//         RoomStatus status,
//         Integer maxParticipants
//     );

//     /**
//      * 统计用户的会议室数量
//      */
//     Long countByHostUid(String hostUid);

//     /**
//      * 统计指定状态的会议室数量
//      */
//     Long countByStatus(RoomStatus status);
// }
