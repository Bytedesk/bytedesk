package com.bytedesk.core.push.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 四类触达推送配置（规划 §4.6）
 *
 * <p>配置前缀 bytedesk.push.reach，与既有 bytedesk.push.provider（供应商）同域不同段，互不冲突。
 * 各触达开关默认值：chat=true（现状已有推送，仅收敛口径为离线才推）；其余 false（灰度开启）。
 *
 * <pre>
 * bytedesk.push.reach.chat.enabled=true
 * bytedesk.push.reach.thread-queue.enabled=false
 * bytedesk.push.reach.transfer-invite.enabled=false
 * bytedesk.push.reach.throttle-seconds=60
 * </pre>
 */
@Data
@Component
@ConfigurationProperties(prefix = "bytedesk.push.reach")
public class PushReachProperties {

    /** 聊天消息推送（现状已有，本开关收敛控制：离线才推 + 节流） */
    private final Reach chat = new Reach();

    /** 新访客请求（排队/路由池）补推离线坐席 */
    private final Reach threadQueue = new Reach();

    /** 会话转接/邀请协助推送 */
    private final Reach transferInvite = new Reach();

    /** 节流窗口（秒），0=不节流 */
    private long throttleSeconds = 60L;

    /** 无 token 等跳过场景是否落 PushApnsEntity 记录（SKIPPED，规划 2026-10-15-apns-push-record-persistence-plan） */
    private final SkipRecord skipRecord = new SkipRecord();

    @Data
    public static class Reach {
        private boolean enabled = false;
    }

    @Data
    public static class SkipRecord {
        private boolean enabled = true;
    }

    // 默认 chat 开启：现状已有聊天推送，仅收敛「在线不推」口径，不回退存量能力
    public PushReachProperties() {
        this.chat.enabled = true;
    }
}
