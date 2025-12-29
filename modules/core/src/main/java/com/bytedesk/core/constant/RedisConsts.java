/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-23 15:20:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-16 06:53:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.constant;

public class RedisConsts {
    private RedisConsts() {}
    
    //
    public static final String BYTEDESK_REDIS_PREFIX = "bytedeskim:";
    // 
    public static final String CONNECTED_MQTT_CLIENT_IDS = BYTEDESK_REDIS_PREFIX + "connected-mqtt-client-ids";

    // 消息未读相关常量
    public static final String MESSAGE_UNREAD_PREFIX = BYTEDESK_REDIS_PREFIX + "message_unread:";
    public static final String MESSAGE_UNREAD_EXISTS_VALUE = "1";

    // 会话内消息序号缓存前缀
    public static final String THREAD_SEQUENCE_PREFIX = BYTEDESK_REDIS_PREFIX + "thread:sequence:";

    // 自动回复去重相关常量
    public static final String AUTO_REPLY_PROCESSED_PREFIX = BYTEDESK_REDIS_PREFIX + "auto_reply_processed:";
    public static final String AUTO_REPLY_PROCESSED_VALUE = "1";

    // 登录失败重试相关常量
    public static final String LOGIN_FAILED_PREFIX = BYTEDESK_REDIS_PREFIX + "login_failed:";
    public static final String LOGIN_LOCKED_PREFIX = BYTEDESK_REDIS_PREFIX + "login_locked:";
    public static final String LOGIN_LOCKED_VALUE = "1";

    // 转接超时相关常量
    public static final String TRANSFER_TIMEOUT_PREFIX = BYTEDESK_REDIS_PREFIX + "transfer:timeout:";

    // Redis监听频道相关常量
    public static final String REDIS_KEYEVENT_EXPIRED_CHANNEL = "__keyevent@0__:expired";
    public static final String REDIS_KEYEVENT_EXPIRED_PATTERN = "__keyevent@*__:expired";

    // 推送验证码发送频率控制相关常量
    public static final String PUSH_CODE_IP_PREFIX = BYTEDESK_REDIS_PREFIX + "push:code:ip:";

    // 推送记录过期处理（验证码/扫码登录）
    // ZSET: member=pushUid, score=expireAtEpochMillis
    public static final String PUSH_EXPIRE_ZSET_KEY = BYTEDESK_REDIS_PREFIX + "push:expire:zset";
    // DB 兜底过期任务的分布式锁（避免多实例重复跑）
    public static final String PUSH_EXPIRE_BACKFILL_LOCK_KEY = BYTEDESK_REDIS_PREFIX + "push:expire:backfill:lock";

    // 验证码相关常量
    public static final String KAPTCHA_PREFIX = BYTEDESK_REDIS_PREFIX + "kaptcha:";

    // Redis 缓存心跳Key
    public static final String REDIS_HEARTBEAT_HASH_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX + "core:conn:hb";
    // Redis 最近一次数据库写入时间Key
    public static final String REDIS_LAST_DB_WRITE_HASH_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX + "core:conn:hb:lastdb";

    // 活跃会话缓存 - 使用 Hash 存储活跃服务会话的关键信息
    // Key: ACTIVE_SERVICE_THREADS_KEY, Field: threadUid, Value: ActiveThreadCache JSON
    public static final String ACTIVE_SERVICE_THREADS_KEY = BYTEDESK_REDIS_PREFIX + "thread:active:service";

}
