package com.bytedesk.meet.minutes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetMinutesSourceRepository extends JpaRepository<MeetMinutesSourceEntity, Long> {

    List<MeetMinutesSourceEntity> findByMinutesUidOrderByOrdinalAsc(String minutesUid);

    List<MeetMinutesSourceEntity> findByRecordingUid(String recordingUid);

    void deleteByMinutesUid(String minutesUid);
}
