package com.bytedesk.disk.file;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface DiskFileRepository extends JpaRepository<DiskFileEntity, Long>, JpaSpecificationExecutor<DiskFileEntity> {
    Optional<DiskFileEntity> findByUid(String uid);

    Page<DiskFileEntity> findBySpaceUidAndParentUidAndDeletedFalse(String spaceUid, String parentUid, Pageable pageable);

    Page<DiskFileEntity> findBySpaceUidAndParentUidAndDeletedFalseAndTypeAndFileTypeIn(
            String spaceUid, String parentUid, String type, List<String> fileTypes, Pageable pageable);

    List<DiskFileEntity> findBySpaceUidAndParentUidAndDeletedFalseOrderByTypeAscNameAsc(String spaceUid, String parentUid);

    List<DiskFileEntity> findBySpaceUidAndParentUidAndDeletedFalse(String spaceUid, String parentUid);

    List<DiskFileEntity> findBySpaceUidAndDeletedFalseAndNameContainingIgnoreCase(String spaceUid, String keyword);

    List<DiskFileEntity> findBySpaceUidAndDeletedFalseAndNameContainingIgnoreCaseAndTypeAndFileTypeIn(
            String spaceUid, String keyword, String type, List<String> fileTypes);

    List<DiskFileEntity> findBySpaceUidAndDeletedTrue(String spaceUid);

    List<DiskFileEntity> findByParentUidAndDeletedFalse(String parentUid);

    long countBySpaceUidAndDeletedFalseAndType(String spaceUid, String type);

    long countByParentUidAndDeletedFalse(String parentUid);

    boolean existsBySpaceUidAndParentUidAndNameAndDeletedFalse(String spaceUid, String parentUid, String name);
}
