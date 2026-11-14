package com.bytedesk.remote.session;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface DesktopSessionRepository extends JpaRepository<DesktopSessionEntity, Long> {
    Optional<DesktopSessionEntity> findByActiveThreadUid(String threadUid);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from DesktopSessionEntity s where s.uid = :uid and s.deleted = false")
    Optional<DesktopSessionEntity> lockByUid(@Param("uid") String uid);

    List<DesktopSessionEntity> findByStatusIn(List<DesktopSessionStatusEnum> statuses);
}
