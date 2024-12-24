package com.bytedesk.core.ip.white;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IpWhitelistRepository extends JpaRepository<IpWhitelistEntity, Long> {
    
    boolean existsByIp(String ip);
} 