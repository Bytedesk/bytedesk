package com.bytedesk.core.ip.black;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IpBlacklistRepository extends JpaRepository<IpBlacklistEntity, Long> {
    
    IpBlacklistEntity findByIp(String ip);
} 