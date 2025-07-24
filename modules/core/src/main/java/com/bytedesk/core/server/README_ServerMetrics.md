# 服务器指标记录系统

## 概述

为了记录 CPU 和内存使用率的变化曲线，我们实现了一个新的服务器指标记录系统。该系统包含以下组件：

### 核心组件

1. **ServerMetricsEntity** - 指标数据实体
2. **ServerMetricsRepository** - 数据访问层
3. **ServerMetricsService** - 业务逻辑层
4. **ServerMetricsController** - REST API 接口
5. **ServerMetricsCleanupTask** - 数据清理任务

## 设计思路

### 数据分离策略

- **ServerEntity**: 存储服务器基本信息（名称、IP、类型等）和最新状态
- **ServerMetricsEntity**: 存储历史指标数据，专注于变化曲线分析，避免重复存储

### 优势

1. **保留历史数据**: 每次指标更新都会创建新记录，形成完整的时间序列
2. **避免重复存储**: 只存储变化曲线相关的数据，减少存储空间
3. **便于趋势分析**: 可以轻松查询特定时间段内的指标变化
4. **数据管理**: 提供自动清理机制，避免数据无限增长
5. **性能优化**: 通过索引优化查询性能

## API 接口

### 1. 获取指标历史数据

```http
GET /api/v1/server-metrics/history/{serverUid}?startTime=2025-07-24T00:00:00&endTime=2025-07-24T23:59:59
```

### 2. 获取最新指标

```http
GET /api/v1/server-metrics/latest/{serverUid}
```

### 3. 获取平均指标

```http
GET /api/v1/server-metrics/average/{serverUid}?startTime=2025-07-24T00:00:00&endTime=2025-07-24T23:59:59
```

### 4. 获取峰值指标

```http
GET /api/v1/server-metrics/peak/{serverUid}?startTime=2025-07-24T00:00:00&endTime=2025-07-24T23:59:59
```

### 5. 查找高使用率指标

```http
GET /api/v1/server-metrics/high-usage?cpuThreshold=80&memoryThreshold=80&diskThreshold=85&startTime=2025-07-24T00:00:00&endTime=2025-07-24T23:59:59
```

### 6. 获取当前服务器指标历史

```http
GET /api/v1/server-metrics/current/history?startTime=2025-07-24T00:00:00&endTime=2025-07-24T23:59:59
```

## 使用示例

### 查询最近24小时的指标数据

```java
// 获取当前服务器
ServerEntity currentServer = serverRestService.getCurrentServerMetrics();
String serverName = currentServer.getServerName();
ServerEntity existingServer = serverRestService.findByServerName(serverName);

if (existingServer != null) {
    // 查询最近24小时的数据
    LocalDateTime endTime = LocalDateTime.now();
    LocalDateTime startTime = endTime.minusHours(24);
    
    List<ServerMetricsEntity> metrics = serverMetricsService.getMetricsHistory(
        existingServer.getUid(), startTime, endTime);
    
    // 处理数据...
    for (ServerMetricsEntity metric : metrics) {
        // 可以通过 metric.getServerUid() 关联获取服务器详细信息
        // ServerEntity serverInfo = serverRestService.findByUid(metric.getServerUid()).orElse(null);
        
        System.out.println("CPU: " + metric.getCpuUsage() + "%");
        System.out.println("Memory: " + metric.getMemoryUsage() + "%");
        System.out.println("Disk: " + metric.getDiskUsage() + "%");
        System.out.println("Time: " + metric.getTimestamp());
    }
}
```

### 生成趋势图表数据

```java
// 获取最近7天的平均指标
LocalDateTime endTime = LocalDateTime.now();
LocalDateTime startTime = endTime.minusDays(7);

ServerMetricsService.ServerMetricsAverage average = serverMetricsService.getAverageMetrics(
    serverUid, startTime, endTime);

// 获取峰值指标
ServerMetricsService.ServerMetricsPeak peak = serverMetricsService.getPeakMetrics(
    serverUid, startTime, endTime);
```

## 数据清理

系统会自动清理30天前的指标数据，避免数据库无限增长。清理任务每天凌晨2点执行。

### 手动清理

```http
DELETE /api/v1/server-metrics/cleanup?retentionDays=30
```

## 数据库表结构

### bytedesk_core_server_metrics

| 字段 | 类型 | 说明 |
|------|------|------|
| uid | VARCHAR | 主键 |
| serverUid | VARCHAR | 服务器UID（关联ServerEntity） |
| timestamp | DATETIME | 记录时间 |
| cpuUsage | DOUBLE | CPU使用率（主要变化曲线数据） |
| memoryUsage | DOUBLE | 内存使用率（主要变化曲线数据） |
| diskUsage | DOUBLE | 磁盘使用率（主要变化曲线数据） |
| usedMemoryMb | BIGINT | 已用内存(MB)（用于计算内存使用量变化） |
| usedDiskGb | BIGINT | 已用磁盘空间(GB)（用于计算磁盘使用量变化） |
| uptimeSeconds | BIGINT | 运行时间(秒)（用于监控服务器运行状态） |
| collectionInterval | INT | 采集间隔(分钟) |

## 索引优化

- `idx_server_metrics_server_uid`: 按服务器UID查询
- `idx_server_metrics_timestamp`: 按时间查询
- `idx_server_metrics_server_timestamp`: 按服务器UID和时间联合查询

## 注意事项

1. **数据量控制**: 默认保留30天数据，可根据需要调整
2. **查询性能**: 大量数据查询时建议使用时间范围限制
3. **存储空间**: 定期监控数据库大小，必要时调整保留策略
4. **权限控制**: 指标数据仅超级管理员可访问
5. **数据关联**: 通过 serverUid 字段关联 ServerEntity 获取服务器详细信息

## 扩展功能

### 1. 告警功能

可以基于历史数据设置告警规则：

```java
// 检查是否有连续高使用率
List<ServerMetricsEntity> highUsageMetrics = serverMetricsService.findHighUsageMetrics(
    90.0, 90.0, 95.0, startTime, endTime);

if (highUsageMetrics.size() >= 3) {
    // 发送告警
    sendAlert("服务器连续3次高使用率告警");
}
```

### 2. 趋势预测

基于历史数据可以进行简单的趋势预测：

```java
// 计算使用率变化趋势
List<ServerMetricsEntity> recentMetrics = serverMetricsService.getMetricsHistory(
    serverUid, startTime, endTime);

// 分析趋势...
```

### 3. 容量规划

通过分析峰值和平均值，可以进行容量规划：

```java
ServerMetricsService.ServerMetricsPeak peak = serverMetricsService.getPeakMetrics(
    serverUid, startTime, endTime);

// 基于峰值进行扩容建议
if (peak.getPeakCpuUsage() > 80) {
    // 建议增加CPU资源
}
``` 