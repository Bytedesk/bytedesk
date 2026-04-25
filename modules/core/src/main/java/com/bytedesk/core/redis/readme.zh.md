# redis

该包负责 Redis 运行配置、缓存/队列/Stream 集成与基于 Redis 的支撑接口。

## 实现要点

- RedisConfig、CustomRedisSerializer、JedisProperties、JedisPoolProperties、RedisClusterSwitchProperties 定义运行装配、序列化与连接配置。
- RedisService 与 RedisLoginRetryService 提供通用 Redis 操作和基于 Redis 的登录重试支持。
- RedisEvent 作为 Redis 相关集成点的共享事件抽象。
- cache 子包提供缓存配置与错误处理，queue 子包提供基于 Redis 的队列服务，stream 子包提供 Stream 配置、监听器、控制器与服务支持。
