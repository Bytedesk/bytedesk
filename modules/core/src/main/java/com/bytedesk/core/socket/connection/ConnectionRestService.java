/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 07:04:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.connection;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.constant.RedisConsts;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import static com.bytedesk.core.socket.connection.ConnectionStatusEnum.CONNECTED;
import static com.bytedesk.core.socket.connection.ConnectionStatusEnum.DISCONNECTED;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ConnectionRestService extends BaseRestServiceWithExport<ConnectionEntity, ConnectionRequest, ConnectionResponse, ConnectionExcel> {

    private final ConnectionRepository connectionRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final PresenceTtlResolver presenceTtlResolver;
    private final ConnectionMetrics connectionMetrics;

    private final StringRedisTemplate stringRedisTemplate;

    private final CacheManager cacheManager;

    // 最小数据库写入间隔（毫秒）
    private static final long MIN_INTERVAL_MS = 5000L;
    
    @Override
    protected Specification<ConnectionEntity> createSpecification(ConnectionRequest request) {
        return ConnectionSpecification.search(request, authService);
    }

    @Override
    protected Page<ConnectionEntity> executePageQuery(Specification<ConnectionEntity> spec, Pageable pageable) {
        return connectionRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "connection", key = "#uid", unless="#result==null")
    @Override
    public Optional<ConnectionEntity> findByUid(String uid) {
        return connectionRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return connectionRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ConnectionResponse create(ConnectionRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        ConnectionEntity entity = modelMapper.map(request, ConnectionEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ConnectionEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create connection failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ConnectionResponse update(ConnectionRequest request) {
        Optional<ConnectionEntity> optional = connectionRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ConnectionEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            ConnectionEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update connection failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Connection not found");
        }
    }

    @Override
    protected ConnectionEntity doSave(ConnectionEntity entity) {
        return connectionRepository.saveAndFlush(entity);
    }

    @Override
    public ConnectionEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ConnectionEntity entity) {
        try {
            Optional<ConnectionEntity> latest = connectionRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ConnectionEntity latestEntity = latest.get();
                // 合并最新状态后重试保存
                latestEntity
                        .setStatus(entity.getStatus())
                        .setConnectedAt(entity.getConnectedAt())
                        .setLastHeartbeatAt(entity.getLastHeartbeatAt())
                        .setDisconnectedAt(entity.getDisconnectedAt())
                        .setTtlSeconds(entity.getTtlSeconds())
                        .setProtocol(entity.getProtocol())
                        .setChannel(entity.getChannel())
                        .setIp(entity.getIp())
                        .setUserAgent(entity.getUserAgent())
                        .setDeviceUid(entity.getDeviceUid());
                return connectionRepository.saveAndFlush(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
        }
        throw new RuntimeException("无法处理乐观锁冲突: " + e.getMessage(), e);
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        Optional<ConnectionEntity> optional = connectionRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            evictPresenceCaches(optional.get().getUserUid());
            // connectionRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Connection not found");
        }
    }

    @Override
    public void delete(ConnectionRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ConnectionResponse convertToResponse(ConnectionEntity entity) {
        return modelMapper.map(entity, ConnectionResponse.class);
    }

    @Override
    public ConnectionExcel convertToExcel(ConnectionEntity entity) {
        return modelMapper.map(entity, ConnectionExcel.class);
    }

    /* ================= Presence APIs (multi-client) ================= */

    /**
     * Mark (or create) a connection as connected.
     * clientId format recommendation: userUid/client/deviceUid
     */
    @Transactional
    public void markConnected(String userUid, String orgUid, String clientId, String deviceUid, String protocol, String channel, String ip, String userAgent, Integer ttlSeconds) {
        long now = System.currentTimeMillis();
        ConnectionEntity entity = fetchConnectionForUpdate(clientId, () -> ConnectionEntity.builder()
            .uid(uidUtils.getUid())
            .userUid(userUid)
            .orgUid(orgUid)
            .clientId(clientId)
            .deviceUid(deviceUid)
            .protocol(protocol)
            .channel(channel)
            .ip(ip)
            .userAgent(userAgent)
            .build());
        applyConnectedState(entity, protocol, ttlSeconds, now);
        save(entity);
        evictPresenceCaches(userUid);
    }

    /** Update heartbeat for a client connection */
    @Transactional
    public void heartbeat(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            return;
        }
        connectionMetrics.incHeartbeatCall();
        long now = System.currentTimeMillis();
        long threshold = now - MIN_INTERVAL_MS;
        // 先写入Redis缓存（仅记录最新心跳时间，减少数据库频繁写）
        tryWriteHeartbeatToCache(clientId, now);
        // 直接使用轻量条件更新，避免读取+save 整实体开销
        int updated = connectionRepository.updateHeartbeatIfOlder(clientId, now, threshold);
        if (updated > 0) {
            cacheLastDbWrite(clientId, now);
            connectionMetrics.incDbWrite();
        } else {
            // 未更新，可能记录不存在或刚刚已写入；尝试兜底创建（只在不存在时）
            if (!ensureConnectionExistsFromHeartbeat(clientId, now)) {
                // 间隔不足，跳过数据库写
                connectionMetrics.incSkipped();
            }
        }
    }

    private void tryWriteHeartbeatToCache(String clientId, long ts) {
        try {
            if (stringRedisTemplate != null) {
                stringRedisTemplate.opsForHash().put(RedisConsts.REDIS_HEARTBEAT_HASH_KEY, clientId, String.valueOf(ts));
            }
        } catch (Exception ignore) {}
    }

    private void cacheLastDbWrite(String clientId, long ts) {
        try {
            if (stringRedisTemplate != null) {
                stringRedisTemplate.opsForHash().put(RedisConsts.REDIS_LAST_DB_WRITE_HASH_KEY, clientId, String.valueOf(ts));
            }
        } catch (Exception ignore) {}
    }

    private ConnectionEntity fetchConnectionForUpdate(String clientId, Supplier<ConnectionEntity> creator) {
        return connectionRepository.findByClientIdForUpdate(clientId).orElseGet(creator);
    }

    private void applyConnectedState(ConnectionEntity entity, String protocol, Integer ttlSeconds, long now) {
        int resolvedTtl = presenceTtlResolver.resolve(protocol, ttlSeconds);
        entity.setStatus(CONNECTED.name())
              .setConnectedAt(entity.getConnectedAt() == null ? now : entity.getConnectedAt())
              .setLastHeartbeatAt(now)
              .setDisconnectedAt(null)
              .setTtlSeconds(resolvedTtl);
    }

    private boolean ensureConnectionExistsFromHeartbeat(String clientId, long now) {
        if (!clientId.contains("/")) {
            return false;
        }
        try {
            String[] parts = clientId.split("/");
            String userUid = parts.length > 0 ? parts[0] : null;
            String deviceUid = parts.length > 2 ? parts[2] : null;
            if (userUid == null) {
                return false;
            }
            ConnectionEntity entity = fetchConnectionForUpdate(clientId, () -> ConnectionEntity.builder()
                    .uid(uidUtils.getUid())
                    .userUid(userUid)
                    .clientId(clientId)
                    .deviceUid(deviceUid)
                    .protocol(ConnectionProtocalEnum.MQTT.name())
                    .build());
            if (entity.getId() != null) {
                return false;
            }
            applyConnectedState(entity, entity.getProtocol(), null, now);
            save(entity);
            cacheLastDbWrite(clientId, now);
            connectionMetrics.incCreated();
            return true;
        } catch (Exception ex) {
            log.warn("ensureConnectionExistsFromHeartbeat failed clientId {}: {}", clientId, ex.getMessage());
            return false;
        }
    }

    /**
     * 批量刷新心跳缓存到数据库，返回成功更新条数。
     * 该方法供定时任务调用。
     */
    @Transactional
    public int flushHeartbeatCacheBatch(java.util.Map<String, Long> heartbeats) {
        if (heartbeats == null || heartbeats.isEmpty()) return 0;
        int updatedCount = 0;
        long now = System.currentTimeMillis();
        long threshold = now - MIN_INTERVAL_MS;
        for (java.util.Map.Entry<String, Long> e : heartbeats.entrySet()) {
            String clientId = e.getKey();
            Long hbTs = e.getValue();
            if (clientId == null || hbTs == null) continue;
            int u = connectionRepository.updateHeartbeatIfOlder(clientId, hbTs, threshold);
            if (u > 0) {
                updatedCount += u;
                cacheLastDbWrite(clientId, hbTs);
            }
        }
        return updatedCount;
    }

    /** Mark a client connection as disconnected */
    @Transactional
    public void markDisconnected(String clientId) {
        connectionRepository.findByClientIdForUpdate(clientId).ifPresent(entity -> {
            if (!DISCONNECTED.name().equals(entity.getStatus())) {
                entity.setStatus(DISCONNECTED.name())
                      .setDisconnectedAt(System.currentTimeMillis());
                save(entity);
                evictPresenceCaches(entity.getUserUid());
            }
        });
    }

    /** Cleanup expired (stale) connections by TTL */
    @Transactional
    public int expireStaleSessions() {
        long start = System.currentTimeMillis();
        long now = start;
        List<ConnectionEntity> all = connectionRepository.findAll();
        int scanned = 0;
        int changed = 0;
        Set<String> changedUsers = new HashSet<>();
        for (ConnectionEntity c : all) {
            scanned++;
            if (c.isDeleted()) continue;
            if (CONNECTED.name().equals(c.getStatus())) {
                Long last = c.getLastHeartbeatAt();
                if (last != null && c.getTtlSeconds() != null && last + c.getTtlSeconds() * 1000L < now) {
                    c.setStatus(DISCONNECTED.name())
                     .setDisconnectedAt(now);
                    save(c);
                    changed++;
                    if (StringUtils.hasText(c.getUserUid())) {
                        changedUsers.add(c.getUserUid());
                    }
                }
            }
        }
        for (String userUid : changedUsers) {
            evictPresenceCaches(userUid);
        }
        long cost = System.currentTimeMillis() - start;
        log.info("expireStaleSessions scanned={}, expired={}, costMs={}", scanned, changed, cost);
        return changed;
    }

    /** Determine online (has >=1 active non-expired connection) */
    @Transactional(readOnly = true)
    public boolean isUserOnline(String userUid) {
        List<ConnectionEntity> list = connectionRepository.findByUserUidAndDeletedFalse(userUid);
        if (list.isEmpty()) return false;
        long now = System.currentTimeMillis();
        for (ConnectionEntity c : list) {
            if (!CONNECTED.name().equals(c.getStatus())) continue;
            Long last = c.getLastHeartbeatAt();
            if (last == null) continue;
            if (c.getTtlSeconds() == null) continue;
            if (last + c.getTtlSeconds() * 1000L >= now) {
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public int countActiveConnections(String userUid) {
        List<ConnectionEntity> list = connectionRepository.findByUserUidAndDeletedFalse(userUid);
        if (list.isEmpty()) return 0;
        long now = System.currentTimeMillis();
        int count = 0;
        for (ConnectionEntity c : list) {
            if (!CONNECTED.name().equals(c.getStatus())) continue;
            Long last = c.getLastHeartbeatAt();
            if (last == null) continue;
            Integer ttl = c.getTtlSeconds();
            if (ttl == null) continue;
            if (last + ttl * 1000L >= now) {
                count++;
            }
        }
        return count;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "presence", key = "#userUid", unless = "#result == null")
    public PresenceResponse getPresence(String userUid) {
        int active = countActiveConnections(userUid);
        return PresenceResponse.builder().online(active > 0).activeCount(active).build();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "activeConnections", key = "#userUid", unless = "#result == null")
    public List<ConnectionResponse> listActiveConnections(String userUid) {
        List<ConnectionEntity> list = connectionRepository.findByUserUidAndDeletedFalse(userUid);
        long now = System.currentTimeMillis();
        List<ConnectionResponse> result = new ArrayList<>();
        for (ConnectionEntity c : list) {
            if (!CONNECTED.name().equals(c.getStatus())) continue;
            Long last = c.getLastHeartbeatAt();
            Integer ttl = c.getTtlSeconds();
            if (last == null || ttl == null) continue;
            if (last + ttl * 1000L >= now) {
                result.add(convertToResponse(c));
            }
        }
        return result;
    }

    private void evictPresenceCaches(String userUid) {
        if (!StringUtils.hasText(userUid) || cacheManager == null) {
            return;
        }
        Cache presence = cacheManager.getCache("presence");
        if (presence != null) {
            presence.evict(userUid);
        }
        Cache activeConnections = cacheManager.getCache("activeConnections");
        if (activeConnections != null) {
            activeConnections.evict(userUid);
        }
    }
    
}
