package com.bytedesk.core.push.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.RedisConsts;
import com.bytedesk.core.connection.ConnectionRestService;

import lombok.extern.slf4j.Slf4j;

/**
 * 推送触达闸门：在线判定 + 节流（规划 2026-10-13-weiyu-background-apns-message-push-plan §4.3）
 *
 * <p>四类触达（聊天消息/新访客排队/转接/邀请）统一经本类判定后再推送：
 * <ul>
 * <li>「离线才推」：接收方存在 CONNECTED 未过期长连接（PC/手机前台任一）即跳过推送，
 * 消息已通过 MQTT/STOMP 实时下发，推送只会造成双提醒；</li>
 * <li>节流合并：同接收人+同 thread/workgroup 在窗口内（默认 60s）只推首条，
 * Redis SETNX+TTL 实现；转接/邀请等强通知不节流（调用方不传节流键即可）。</li>
 * </ul>
 *
 * <p>降级约定：Redis 不可用时 fail-open（不节流但照常推送），日志告警，不阻断推送主流程。
 * 边界：isUserOnline 有心跳 TTL 滞后（最长 90s），最坏情况「刚断连 90s 内不推」，
 * 由节流窗口到期后下一条消息重推兜底。
 */
@Slf4j
@Service
// @AllArgsConstructor
public class AgentPushGateService {

    private final ConnectionRestService connectionRestService;

    private final StringRedisTemplate stringRedisTemplate;

    private final PushReachProperties pushReachProperties;

    public AgentPushGateService(ConnectionRestService connectionRestService,
            StringRedisTemplate stringRedisTemplate,
            PushReachProperties pushReachProperties) {
        this.connectionRestService = connectionRestService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.pushReachProperties = pushReachProperties;
    }

    /** 接收方是否有活跃长连接（在线即不推，消息已实时下发） */
    public boolean isUserOnline(String userUid) {
        if (!StringUtils.hasText(userUid)) {
            return false;
        }
        return connectionRestService.isUserOnline(userUid);
    }

    /**
     * 是否允许推送：离线 + （可选）节流未命中。
     *
     * @param userUid     接收用户 uid
     * @param throttleKey 节流键（如 threadUid/workgroupUid）；null 表示不节流（转接/邀请等强通知）
     */
    public boolean shouldPush(String userUid, String throttleKey) {
        if (!StringUtils.hasText(userUid)) {
            return false;
        }
        if (isUserOnline(userUid)) {
            log.debug("Skip mobile push because user online, userUid={}, throttleKey={}", userUid, throttleKey);
            return false;
        }
        return acquireThrottle(userUid, throttleKey);
    }

    /**
     * 节流：同 user+key 窗口内只放行首条（Redis SETNX + TTL）。
     * Redis 异常时 fail-open 放行（不节流），避免推送链路被 Redis 可用性绑架。
     */
    private boolean acquireThrottle(String userUid, String throttleKey) {
        long windowSeconds = pushReachProperties.getThrottleSeconds();
        if (!StringUtils.hasText(throttleKey) || windowSeconds <= 0) {
            return true;
        }
        String key = RedisConsts.PUSH_REACH_THROTTLE_PREFIX + userUid + ":" + throttleKey;
        try {
            Boolean first = stringRedisTemplate.opsForValue()
                    .setIfAbsent(key, "1", Duration.ofSeconds(windowSeconds));
            if (Boolean.TRUE.equals(first)) {
                return true;
            }
            log.debug("Skip mobile push because throttled, userUid={}, key={}, windowSeconds={}",
                    userUid, key, windowSeconds);
            return false;
        } catch (Exception e) {
            log.warn("Push throttle check failed (fail-open), key={}, error={}", key, e.getMessage());
            return true;
        }
    }
}
