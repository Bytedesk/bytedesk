/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-29
 * @Description: Push expire cache service (Redis ZSET based)
 */
package com.bytedesk.core.push;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.RedisConsts;
import com.bytedesk.core.rbac.auth.AuthTypeEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PushExpireCacheService {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 将待过期的 push uid 写入 ZSET（member=uid, score=expireAtMillis）。
     */
    public void scheduleIfPending(PushEntity push) {
        if (push == null) {
            return;
        }
        if (!StringUtils.hasText(push.getUid())) {
            return;
        }
        if (!PushStatusEnum.PENDING.name().equals(push.getStatus())) {
            remove(push.getUid());
            return;
        }
        long ttlMillis = getExpireTtlMillis(push.getType());
        long expireAt = System.currentTimeMillis() + ttlMillis;
        stringRedisTemplate.opsForZSet().add(RedisConsts.PUSH_EXPIRE_ZSET_KEY, push.getUid(), expireAt);
    }

    public void remove(String pushUid) {
        if (!StringUtils.hasText(pushUid)) {
            return;
        }
        stringRedisTemplate.opsForZSet().remove(RedisConsts.PUSH_EXPIRE_ZSET_KEY, pushUid);
    }

    /**
     * 获取一批已到期的 push uid，并从 ZSET 中移除（非严格原子，但足够幂等）。
     */
    public Set<String> pollExpiredUids(int batchSize) {
        if (batchSize <= 0) {
            return Collections.emptySet();
        }
        long now = System.currentTimeMillis();
        Set<String> uids = stringRedisTemplate.opsForZSet()
                .rangeByScore(RedisConsts.PUSH_EXPIRE_ZSET_KEY, 0, now, 0, batchSize);
        if (uids == null || uids.isEmpty()) {
            return Collections.emptySet();
        }
        stringRedisTemplate.opsForZSet().remove(RedisConsts.PUSH_EXPIRE_ZSET_KEY, uids.toArray());
        return uids;
    }

    /**
     * 获取 DB 兜底任务锁。
     */
    public boolean tryAcquireBackfillLock(long ttlSeconds) {
        try {
            Boolean ok = stringRedisTemplate.opsForValue().setIfAbsent(
                    RedisConsts.PUSH_EXPIRE_BACKFILL_LOCK_KEY,
                    "1",
                    ttlSeconds,
                    TimeUnit.SECONDS);
            return Boolean.TRUE.equals(ok);
        } catch (DataAccessException e) {
            // Redis 不可用时，返回 false 让上层选择其它兜底策略
            return false;
        }
    }

    private long getExpireTtlMillis(String type) {
        // 扫码登录有效时间 3 分钟，其余验证码 15 分钟（与历史逻辑保持一致）
        if (AuthTypeEnum.SCAN_LOGIN.name().equals(type)) {
            return TimeUnit.MINUTES.toMillis(3);
        }
        return TimeUnit.MINUTES.toMillis(15);
    }
}
