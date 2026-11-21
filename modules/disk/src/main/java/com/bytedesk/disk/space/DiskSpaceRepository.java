package com.bytedesk.disk.space;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface DiskSpaceRepository extends JpaRepository<DiskSpaceEntity, Long>, JpaSpecificationExecutor<DiskSpaceEntity> {

    Optional<DiskSpaceEntity> findByUid(String uid);

    Optional<DiskSpaceEntity> findByOrgUidAndType(String orgUid, String type);

    Optional<DiskSpaceEntity> findByOrgUidAndUserUidAndType(String orgUid, String userUid, String type);

    List<DiskSpaceEntity> findByOrgUidAndDeletedFalse(String orgUid);
}
