package com.bytedesk.disk.audit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DiskLogRepository extends JpaRepository<DiskLogEntity, Long> {
}
