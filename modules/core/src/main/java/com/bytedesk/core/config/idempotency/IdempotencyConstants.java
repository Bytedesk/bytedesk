package com.bytedesk.core.config.idempotency;

import com.bytedesk.core.constant.RedisConsts;

public class IdempotencyConstants {

    private IdempotencyConstants() {}

    public static final String HEADER_IDEMPOTENCY_KEY = "Idempotency-Key";
    public static final String HEADER_REQUEST_ID = "X-Request-Id";

    public static final String ATTR_REDIS_KEY = "BYTEDESK_IDEMPOTENCY_REDIS_KEY";
    public static final String ATTR_STORED = "BYTEDESK_IDEMPOTENCY_STORED";
    public static final String ATTR_TTL_SECONDS = "BYTEDESK_IDEMPOTENCY_TTL_SECONDS";

    public static final String VALUE_PENDING = "P";

    public static String redisKeyPrefix() {
        return RedisConsts.BYTEDESK_REDIS_PREFIX + "idempotency:";
    }
}
