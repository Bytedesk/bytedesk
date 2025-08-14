# JMS Artemis 配置说明

## 概述

本配置支持两种 Artemis 模式：
- **Embedded 模式**：Spring Boot 自动启动内嵌 Artemis broker
- **Native 模式**：连接外部 Artemis broker

## 配置方式

### 1. Embedded 模式（开发测试推荐）

```properties
# 启用 embedded 模式
spring.artemis.mode=embedded

# embedded 模式详细配置
spring.artemis.embedded.enabled=true
spring.artemis.embedded.persistent=false
spring.artemis.embedded.data-directory=./artemis-data
spring.artemis.embedded.configuration=classpath:artemis-config.xml

# 注释掉 native 模式配置
# spring.artemis.broker-url=tcp://127.0.0.1:16161
# spring.artemis.user=admin
# spring.artemis.password=admin
```

**优点：**
- 无需额外安装 Artemis
- 配置简单
- 适合开发和测试环境

**缺点：**
- 性能相对较低
- 不适合生产环境

### 2. Native 模式（生产环境推荐）

```properties
# 启用 native 模式
spring.artemis.mode=native

# native 模式配置
spring.artemis.broker-url=tcp://127.0.0.1:16161
spring.artemis.user=admin
spring.artemis.password=admin

# 注释掉 embedded 模式配置
# spring.artemis.embedded.enabled=true
# spring.artemis.embedded.persistent=false
# spring.artemis.embedded.data-directory=./artemis-data
# spring.artemis.embedded.configuration=classpath:artemis-config.xml
```

**优点：**
- 性能更高
- 支持集群
- 适合生产环境

**缺点：**
- 需要额外安装和配置 Artemis
- 配置相对复杂

## 切换模式

要切换模式，请按以下步骤操作：

1. 修改 `spring.artemis.mode` 属性
2. 注释掉当前模式的配置
3. 取消注释目标模式的配置
4. 重启应用

## 注意事项

1. **ConnectionFactory 创建**：
   - Embedded 模式：Spring Boot 自动创建
   - Native 模式：手动创建并配置

2. **监听器工厂和 JmsTemplate**：
   - 两种模式都使用相同的配置
   - 自动使用对应的 ConnectionFactory

3. **健康检查**：
   - 两种模式都支持健康检查
   - 可通过 `/actuator/health` 查看状态

## 故障排除

### 连接失败

1. 检查 Artemis broker 是否运行
2. 验证连接参数（URL、用户名、密码）
3. 查看应用日志中的错误信息

### 性能问题

1. 调整连接池配置
2. 优化并发消费者数量
3. 配置适当的超时时间

## 相关链接

- [Spring Boot JMS 文档](https://docs.spring.io/spring-boot/reference/messaging/jms.html)
- [Apache Artemis 文档](https://activemq.apache.org/components/artemis/documentation/latest/index.html)
- [Spring JMS 指南](https://spring.io/guides/gs/messaging-jms/)
