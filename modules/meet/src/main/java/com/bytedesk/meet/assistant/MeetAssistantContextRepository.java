package com.bytedesk.meet.assistant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetAssistantContextRepository extends JpaRepository<MeetAssistantContextEntity, Long> {

    Optional<MeetAssistantContextEntity> findByUid(String uid);

    Optional<MeetAssistantContextEntity> findByThreadUid(String threadUid);

    List<MeetAssistantContextEntity> findByOrgUidAndOwnerUserUidOrderByUpdatedAtDesc(String orgUid, String ownerUserUid);
}
