package com.bytedesk.meet.transcript;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MeetTranscriptSegmentRepository extends JpaRepository<MeetTranscriptSegmentEntity, Long>,
        JpaSpecificationExecutor<MeetTranscriptSegmentEntity> {

    Page<MeetTranscriptSegmentEntity> findByTranscriptUidAndTranscriptRevisionOrderBySeqAsc(
            String transcriptUid, Integer revision, Pageable pageable);

    Optional<MeetTranscriptSegmentEntity> findByUid(String uid);

    Optional<MeetTranscriptSegmentEntity> findFirstByTranscriptUidAndTranscriptRevisionOrderBySeqDesc(
            String transcriptUid, Integer revision);

    long countByTranscriptUidAndTranscriptRevision(String transcriptUid, Integer revision);

    void deleteByTranscriptUidAndTranscriptRevision(String transcriptUid, Integer revision);
}
