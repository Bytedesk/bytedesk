package com.bytedesk.remote.session;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface RemoteSessionRepository extends JpaRepository<RemoteSessionEntity, Long> {
    Optional<RemoteSessionEntity> findByActiveThreadUid(String threadUid);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from RemoteSessionEntity s where s.uid = :uid and s.deleted = false")
    Optional<RemoteSessionEntity> lockByUid(@Param("uid") String uid);

    List<RemoteSessionEntity> findByStatusIn(List<RemoteSessionStatusEnum> statuses);
}
