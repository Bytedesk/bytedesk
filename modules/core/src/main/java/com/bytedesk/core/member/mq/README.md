# Member批量导入异步MQ方案

## 概述

本方案参考FAQ的异步处理模式，通过Artemis MQ消息队列实现Member批量导入的异步处理，有效解决了之前遇到的JPA/Hibernate OptimisticLockException乐观锁冲突问题。

## 架构设计

### 核心组件

1. **MemberBatchMessage** - 批量导入消息实体
2. **MemberBatchMessageService** - 批量导入消息服务
3. **MemberBatchConsumer** - 批量导入消息消费者
4. **MemberExcelListener** - Excel监听器（已修改为异步）

### 最新更新 (2025-06-01)

1. **修复序列化问题**
   - 解决 `Body not assignable to class MemberBatchMessage` 错误
   - 使用 JSON 格式正确传输和解析消息
   - 增加消息类型标识 `_type` 属性

### 处理流程

```bash
Excel上传 → MemberExcelListener → MemberBatchMessageService → Artemis MQ → MemberBatchConsumer → Member创建
```

## 关键特性

### 1. 异步批量处理

- 将Excel数据分批发送到消息队列
- 每个Member数据作为独立消息处理
- 避免大批量同步操作的锁冲突

### 2. 错峰处理机制

- 消息发送时添加随机延迟（50-200ms）
- 避免消息同时到达造成并发冲突
- 确保系统稳定性

### 3. 重试机制

- 支持最大3次重试
- 指数退避延迟策略（2s, 4s, 8s）
- 针对OptimisticLockingFailureException特殊处理

### 4. 监控和调试

- 完整的日志记录
- 批次追踪和状态监控
- 错误处理和异常捕获

## 核心代码

### 消息实体 (MemberBatchMessage)

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberBatchMessage implements Serializable {
    private String batchUid;           // 批次唯一标识
    private String operationType;      // 操作类型
    private String memberExcelJson;    // Member Excel JSON数据
    private String orgUid;             // 组织唯一标识
    private Integer batchIndex;        // 批次索引
    private Integer batchTotal;        // 批次总数
    private Boolean isLastBatch;       // 是否最后批次
    private Integer retryCount;        // 重试次数
    private Long createTimestamp;      // 创建时间戳
}
```

### 消息服务 (MemberBatchMessageService)

```java
public void sendBatchImportMessages(List<MemberExcel> memberExcelList, String orgUid) {
    String batchUid = UidUtils.getInstance().getUid();
    int total = memberExcelList.size();
    
    // 分批发送消息，每个Member一个消息
    memberExcelList.forEach(memberExcel -> {
        // 创建消息并添加随机延迟
        // 发送到队列: QUEUE_MEMBER_BATCH_IMPORT
    });
}
```

### 消息消费者 (MemberBatchConsumer)

```java
@JmsListener(destination = JmsArtemisConstants.QUEUE_MEMBER_BATCH_IMPORT)
public void onMessage(Message message) {
    try {
        // 获取消息内容 - 从TextMessage中获取JSON字符串
        String messageBody = message.getBody(String.class);
        
        // 使用JSON转换器解析消息内容为MemberBatchMessage对象
        MemberBatchMessage batchMessage = JSON.parseObject(messageBody, MemberBatchMessage.class);
        
        // 处理批量导入消息
        boolean success = processBatchImport(batchMessage);
        
        if (success) {
            message.acknowledge();
        } else {
            handleProcessingFailure(batchMessage, message);
        }
    } catch (OptimisticLockingFailureException e) {
        // 特殊处理乐观锁异常
    }
}
```

## 消息序列化问题修复 (2025-06-01)

### 问题描述

在消费消息时，MemberBatchConsumer 遇到序列化错误:

```java
Body not assignable to class com.bytedesk.core.member.mq.MemberBatchMessage
at org.apache.activemq.artemis.jms.client.ActiveMQMessage.getBody(ActiveMQMessage.java:739)
at com.bytedesk.core.member.mq.MemberBatchConsumer.onMessage(MemberBatchConsumer.java:73)
```

原因分析：

- 我们使用了 MappingJackson2MessageConverter 将消息转换为 JSON 文本，但在消费者端直接使用 `message.getBody(MemberBatchMessage.class)` 尝试获取 Java 对象
- JMS 消息体实际上是 TextMessage 类型，而不是直接序列化的 Java 对象
- MessageConverter 配置与消息获取方式不匹配

### 修复方案

1. **修改消费者端获取消息的方法**:

```java
// 修改前 - 直接获取Java对象（会导致异常）
MemberBatchMessage batchMessage = message.getBody(MemberBatchMessage.class);

// 修改后 - 获取字符串并手动解析为对象
String messageBody = message.getBody(String.class);
MemberBatchMessage batchMessage = JSON.parseObject(messageBody, MemberBatchMessage.class);
```

<!-- 2. **在消息发送时添加类型标记**: -->

```java
jmsMessage.setStringProperty("_type", "memberBatchMessage");
```

<!-- 3. **编写测试验证序列化/反序列化流程** -->

### 最佳实践

1. **消息映射**:
   - 使用 TextMessage 格式传输 JSON 数据（而非二进制序列化）
   - 使用 FastJSON 或 Jackson 进行消息体解析

2. **类型安全**:
   - 在发送消息时添加类型标记 (`_type`)
   - 在接收消息时检查类型并使用正确的解析器

3. **错误处理**:
   - 解析失败时优雅降级并提供详细日志
   - 处理解析异常并适当确认消息（避免无限重试）

### 相关文件

```java
// 修改文件
- MemberBatchConsumer.java - 修改消息处理逻辑
- JmsArtemisConfig.java - 配置消息转换器
- MemberBatchMessageService.java - 发送消息时设置类型标记
- MemberBatchMqTest.java - 添加序列化测试
```

## 配置说明

### 队列配置

```java
// JmsArtemisConstants.java
public static final String QUEUE_MEMBER_BATCH_IMPORT = QUEUE_PREFIX + "member.batch.import";
```

### 性能参数

- **批次大小**: 100条记录/批次
- **消息延迟**: 50-200ms随机延迟
- **重试次数**: 最大3次
- **重试延迟**: 2s, 4s, 8s（指数退避）

## 使用方法

### 1. Excel上传处理

```java
// MemberEventListener.java
EasyExcel.read(inputStream, MemberExcel.class, 
    new MemberExcelListener(memberBatchMessageService, upload.getOrgUid()))
    .sheet().doRead();
```

### 2. 手动触发批量导入

```java
@Autowired
private MemberBatchMessageService memberBatchMessageService;

public void importMembers(List<MemberExcel> memberList, String orgUid) {
    memberBatchMessageService.sendBatchImportMessages(memberList, orgUid);
}
```

## 监控指标

### 1. 日志监控

- 批次处理进度
- 重试次数统计
- 错误率监控

### 2. 性能指标

- 消息处理吞吐量
- 平均处理时间
- 队列积压情况

## 故障排查

### 1. 常见问题

#### OptimisticLockException仍然出现

- 检查重试机制是否正常工作
- 确认消息延迟配置是否生效
- 验证数据库连接池配置

#### 消息积压

- 检查消费者线程池配置
- 监控数据库性能
- 确认重试逻辑是否合理

#### 数据不一致

- 检查事务配置
- 验证消息确认机制
- 确认重复消息处理逻辑

### 2. 调试模式

启用调试日志：

```properties
logging.level.com.bytedesk.core.member.mq=DEBUG
```

## 性能优化建议

### 1. 数据库优化

- 合理设置数据库连接池
- 优化Member表索引
- 考虑读写分离

### 2. 消息队列优化

- 调整消费者并发数
- 优化消息大小
- 配置合适的队列深度

### 3. 应用层优化

- 批量处理大小调优
- 延迟时间调整
- 重试策略优化

## 版本历史

- **v1.0** (2025-06-01): 初始版本，实现基本异步处理
- 支持单个Member消息处理
- 实现重试机制和错峰处理
- 解决OptimisticLockException问题

## 后续计划

1. **性能监控**: 添加Metrics监控指标
2. **批量优化**: 支持可配置的批量大小
3. **故障恢复**: 实现死信队列处理
4. **压力测试**: 大规模数据导入测试
