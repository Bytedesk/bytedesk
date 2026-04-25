# redis

This package manages Redis runtime configuration, cache/queue/stream integration, and Redis-backed support APIs.

## Implementation Notes

- RedisConfig, CustomRedisSerializer, JedisProperties, JedisPoolProperties, and RedisClusterSwitchProperties define runtime wiring, serialization, and connectivity configuration.
- RedisService and RedisLoginRetryService provide generic Redis operations and login-retry support backed by Redis.
- RedisEvent acts as a shared event abstraction for Redis-related integration points.
- The cache subpackage provides cache configuration and error handling, the queue subpackage provides Redis-backed queue services, and the stream subpackage provides stream configuration, listener, controller, and service support.
