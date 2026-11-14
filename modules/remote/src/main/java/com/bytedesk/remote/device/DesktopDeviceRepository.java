package com.bytedesk.remote.device;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesktopDeviceRepository extends JpaRepository<DesktopDeviceEntity, Long> {
    Optional<DesktopDeviceEntity> findByUid(String uid);
}
