package com.bytedesk.core.ip;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IpBlacklistRepository extends JpaRepository<IpBlacklistEntity, Long> {
    
    IpBlacklistEntity findByIp(String ip);
} 