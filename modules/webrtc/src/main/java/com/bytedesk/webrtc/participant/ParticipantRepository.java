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
package com.bytedesk.webrtc.participant;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long>, JpaSpecificationExecutor<ParticipantEntity> {

    Optional<ParticipantEntity> findByUid(String uid);

    Boolean existsByUid(String uid);

    Optional<ParticipantEntity> findByNameAndOrgUidAndTypeAndDeletedFalse(String name, String orgUid, String type);

    // Boolean existsByPlatform(String platform);

    // ===== 会议参与者（type=MEETING） =====

    /** 某会议室的全部参会记录 */
    List<ParticipantEntity> findByRoomUidAndDeletedFalse(String roomUid);

    /** 某会议室当前在会参与者 */
    List<ParticipantEntity> findByRoomUidAndStatusAndDeletedFalse(String roomUid, String status);

    /** 某用户在指定会议室的活跃参会记录（在会，未离会） */
    Optional<ParticipantEntity> findByRoomUidAndUserUidAndStatusAndDeletedFalse(String roomUid, String userUid, String status);

    /** 按 Janus 参与者 id 查找活跃记录（前端回传 janusParticipantId 时使用） */
    Optional<ParticipantEntity> findByRoomUidAndJanusParticipantIdAndStatusAndDeletedFalse(String roomUid, Long janusParticipantId, String status);

    /** 某用户参加过的所有会议记录 */
    List<ParticipantEntity> findByUserUidAndTypeAndDeletedFalse(String userUid, String type);
}
