/*
 * Scheduled task to flush cached heartbeats from Redis to DB in batches.
 */
package com.bytedesk.core.socket.connection;

import java.util.HashMap;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.constant.RedisConsts;

import org.springframework.data.redis.core.StringRedisTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionHeartbeatFlushTask {

    private final StringRedisTemplate stringRedisTemplate;
    private final ConnectionRestService connectionRestService;

    private static final String REDIS_HEARTBEAT_HASH_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX + "core:conn:hb";
    private static final String REDIS_LAST_DB_WRITE_HASH_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX + "core:conn:hb:lastdb";

    // 每 10 秒批量刷新一次
    @Scheduled(fixedDelay = 10_000)
    @Transactional
    public void flush() {
        if (stringRedisTemplate == null) {
            return;
        }
        try {
            long start = System.nanoTime();
            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(REDIS_HEARTBEAT_HASH_KEY);
            if (entries == null || entries.isEmpty()) {
                return;
            }
            Map<String, Long> heartbeats = new HashMap<>();
            for (Map.Entry<Object, Object> e : entries.entrySet()) {
                String clientId = String.valueOf(e.getKey());
                Object v = e.getValue();
                try {
                    long ts = Long.parseLong(String.valueOf(v));
                    heartbeats.put(clientId, ts);
                } catch (NumberFormatException ignore) {
                }
            }
            if (heartbeats.isEmpty()) return;

            int updated = connectionRestService.flushHeartbeatCacheBatch(heartbeats);
            long elapsed = System.nanoTime() - start;
            log.debug("flushHeartbeatCacheBatch updated={} size={} elapsedMs={}", updated, heartbeats.size(), elapsed/1_000_000);

            // 清理已持久化的数据，避免哈希无限增长：
            // 原则：若 lastdb >= hbTs，则可以安全移除该 clientId 的缓存心跳
            for (Map.Entry<String, Long> e : heartbeats.entrySet()) {
                String clientId = e.getKey();
                Long hbTs = e.getValue();
                Object lastDbStr = stringRedisTemplate.opsForHash().get(REDIS_LAST_DB_WRITE_HASH_KEY, clientId);
                if (lastDbStr != null) {
                    try {
                        long lastDb = Long.parseLong(String.valueOf(lastDbStr));
                        if (lastDb >= hbTs) {
                            stringRedisTemplate.opsForHash().delete(REDIS_HEARTBEAT_HASH_KEY, clientId);
                        }
                    } catch (NumberFormatException ignore) {}
                }
            }
        } catch (Exception e) {
            log.warn("flushHeartbeatCacheBatch error: {}", e.getMessage());
        }
    }
}
