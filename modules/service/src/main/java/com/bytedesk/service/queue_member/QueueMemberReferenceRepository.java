package com.bytedesk.service.queue_member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueMemberReferenceRepository extends JpaRepository<QueueMemberReference, Long> {
    
    List<QueueMemberReference> findByQueueUid(String queueUid);
    
    List<QueueMemberReference> findByMemberUid(String memberUid);
    
    Optional<QueueMemberReference> findByQueueUidAndThreadUid(String queueUid, String threadUid);
}
