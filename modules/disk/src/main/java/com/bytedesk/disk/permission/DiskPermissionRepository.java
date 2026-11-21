package com.bytedesk.disk.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface DiskPermissionRepository extends JpaRepository<DiskPermissionEntity, Long>, JpaSpecificationExecutor<DiskPermissionEntity> {

    Optional<DiskPermissionEntity> findByUid(String uid);

    List<DiskPermissionEntity> findByFolderUidAndDeletedFalse(String folderUid);

    List<DiskPermissionEntity> findBySpaceUidAndFolderUidInAndDeletedFalse(String spaceUid, List<String> folderUids);

    List<DiskPermissionEntity> findByTargetTypeAndTargetUidAndDeletedFalse(String targetType, String targetUid);

    Optional<DiskPermissionEntity> findByFolderUidAndTargetTypeAndTargetUidAndDeletedFalse(
            String folderUid, String targetType, String targetUid);

    void deleteByFolderUid(String folderUid);
}
