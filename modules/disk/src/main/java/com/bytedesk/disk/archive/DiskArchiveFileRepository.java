package com.bytedesk.disk.archive;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface DiskArchiveFileRepository extends JpaRepository<DiskArchiveFileEntity, Long>,
        JpaSpecificationExecutor<DiskArchiveFileEntity> {
    Optional<DiskArchiveFileEntity> findByUidAndOrgUid(String uid, String orgUid);

    Optional<DiskArchiveFileEntity> findByOrgUidAndUserUidAndUploadRequestId(String orgUid, String userUid,
            String requestId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from DiskArchiveFileEntity f where f.uid = :uid and f.orgUid = :orgUid")
    Optional<DiskArchiveFileEntity> lockByUidAndOrgUid(@Param("uid") String uid, @Param("orgUid") String orgUid);

    List<DiskArchiveFileEntity> findByStorageClearedFalseAndUploadStateInAndCreatedAtBefore(
            List<String> states, ZonedDateTime before, Pageable pageable);
}
