# socket

```bash
# proxy
export http_proxy=http://127.0.0.1:10818
export https_proxy=http://127.0.0.1:10818
# 
# gradle dependencies
# run gradle with hot reload
# 1. open one terminal, run：
gradle build --continuous
# 2. open second terminal, run:
gradle bootRun
# 
# 生成proto
gradle build 
# 或
gradle generateProto
```

## links

- [mqtt](https://mqtt.org/mqtt-specification/)
- [netty](https://netty.io/wiki/user-guide-for-4.x.html)
- [netty mqtt](https://netty.io/4.1/api/io/netty/handler/codec/mqtt/package-summary.html)
- [netty mqtt github](https://github.com/netty/netty/tree/4.1/codec-mqtt/src/main/java/io/netty/handler/codec/mqtt)
- [spring stomp](https://docs.spring.io/spring-framework/reference/web/websocket/stomp/overview.html)
