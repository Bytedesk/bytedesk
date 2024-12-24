package com.bytedesk.core.ip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class IpAccessService {
    
    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final int BLOCK_HOURS = 24;
    
    @Autowired
    private IpAccessRepository ipAccessRepository;
    
    @Autowired
    private IpBlacklistRepository blacklistRepository;
    
    @Autowired
    private IpWhitelistRepository whitelistRepository;
    
    public boolean isIpBlocked(String ip) {
        // 检查是否在白名单中
        if (whitelistRepository.existsByIp(ip)) {
            return false;
        }
        
        // 检查是否在黑名单中且未过期
        IpBlacklistEntity blacklist = blacklistRepository.findByIp(ip);
        if (blacklist != null && blacklist.getExpireTime().isAfter(LocalDateTime.now())) {
            return true;
        }
        
        return false;
    }
    
    public void recordAccess(String ip, String endpoint) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMinuteAgo = now.minusMinutes(1);
        
        // 获取最近一分钟的访问记录
        IpAccessEntity access = ipAccessRepository.findByIpAndEndpointAndAccessTimeAfter(ip, endpoint, oneMinuteAgo);
        
        if (access == null) {
            access = new IpAccessEntity();
            access.setIp(ip);
            access.setEndpoint(endpoint);
            access.setAccessTime(now);
            access.setAccessCount(1);
        } else {
            access.setAccessCount(access.getAccessCount() + 1);
        }
        access.setLastAccessTime(now);
        
        ipAccessRepository.save(access);
        
        // 检查是否需要加入黑名单
        if (access.getAccessCount() > MAX_REQUESTS_PER_MINUTE) {
            addToBlacklist(ip);
        }
    }
    
    private void addToBlacklist(String ip) {
        IpBlacklistEntity blacklist = new IpBlacklistEntity();
        blacklist.setIp(ip);
        blacklist.setBlockedTime(LocalDateTime.now());
        blacklist.setExpireTime(LocalDateTime.now().plusHours(BLOCK_HOURS));
        blacklist.setReason("Exceeded maximum request rate");
        
        blacklistRepository.save(blacklist);
    }
} 