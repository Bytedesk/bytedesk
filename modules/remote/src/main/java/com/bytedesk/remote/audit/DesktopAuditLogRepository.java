package com.bytedesk.remote.audit;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesktopAuditLogRepository extends JpaRepository<DesktopAuditLogEntity, Long> {
    List<DesktopAuditLogEntity> findBySessionUidOrderByCreatedAtAsc(String sessionUid);
}
