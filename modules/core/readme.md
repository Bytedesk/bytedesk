<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:24:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-15 16:26:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# core

## socket

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
- [mqtt-v3.1.1](https://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html)
- [netty](https://netty.io/wiki/user-guide-for-4.x.html)
- [netty mqtt](https://netty.io/4.1/api/io/netty/handler/codec/mqtt/package-summary.html)
- [netty mqtt github](https://github.com/netty/netty/tree/4.1/codec-mqtt/src/main/java/io/netty/handler/codec/mqtt)
- [spring stomp](https://docs.spring.io/spring-framework/reference/web/websocket/stomp/overview.html)
