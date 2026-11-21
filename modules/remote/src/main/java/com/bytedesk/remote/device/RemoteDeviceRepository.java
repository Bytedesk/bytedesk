package com.bytedesk.remote.device;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemoteDeviceRepository extends JpaRepository<RemoteDeviceEntity, Long> {
    Optional<RemoteDeviceEntity> findByUid(String uid);
}
