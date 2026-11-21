package com.bytedesk.remote.invitation;

import java.util.Optional;
import java.time.ZonedDateTime;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RemoteInvitationRepository extends JpaRepository<RemoteInvitationEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from RemoteInvitationEntity i where i.uid = :uid and i.deleted = false")
    Optional<RemoteInvitationEntity> lockByUid(@Param("uid") String uid);

    void deleteByExpiresAtBefore(ZonedDateTime cutoff);
}
