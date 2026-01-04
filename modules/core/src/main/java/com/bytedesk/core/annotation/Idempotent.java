package com.bytedesk.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables server-side idempotency for write APIs.
 *
 * Usage: annotate controller method (usually a create/submit endpoint).
 * Client should send header Idempotency-Key (or X-Request-Id) with a stable value per logical action.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * TTL for cached result / in-flight marker.
     */
    int ttlSeconds() default 600;

    /**
     * When true, missing idempotency key will be rejected.
     * Default false for backward compatibility.
     */
    boolean requireKey() default false;
}
