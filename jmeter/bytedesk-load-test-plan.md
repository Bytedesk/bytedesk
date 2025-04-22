# 微语 压力测试方案

## 1. 测试目标

- 验证系统在高并发场景下的稳定性和可靠性
- 评估系统的最大吞吐量和性能上限
- 识别系统可能存在的性能瓶颈点
- 测试系统在不同负载条件下的响应时间
- 评估系统资源使用情况（CPU、内存、数据库连接等）
- 确定系统的水平扩展能力和垂直扩展边界

## 2. 测试环境

### 2.1 硬件环境

- 测试服务器: 与生产环境配置相近或相同的服务器
- 数据库服务器: 与生产环境配置相近的数据库服务器
- 负载生成器: 至少2-3台高性能服务器用于生成测试负载

### 2.2 软件环境

- 操作系统: Linux (推荐 Ubuntu 20.04/22.04 或 CentOS 8)
- JDK版本: JDK 17
- 应用服务器: Spring Boot 3.4.4
- 数据库: MySQL 8.0+ / PostgreSQL 14+
- 测试工具: Apache JMeter 5.5+, Gatling 3.9+
- 监控工具: Prometheus + Grafana, VisualVM, Arthas

### 2.3 测试网络环境

- 内部局域网: 千兆网络环境
- 外部测试: 模拟用户访问的公网环境

## 3. 测试范围

### 3.1 API接口测试

- 用户认证接口
- IM消息发送和接收接口
- 客服工作台操作接口
- 知识库查询接口
- AI对话接口

### 3.2 核心功能测试

- 即时消息收发
- 客服分配和路由
- AI回复生成
- 多人会话
- 文件上传下载

### 3.3 后台任务测试

- 定时任务执行
- 消息异步处理
- 数据统计与分析

## 4. 测试指标

### 4.1 关键性能指标

- 吞吐量(TPS/QPS): 系统每秒处理的事务/请求数
- 响应时间: 平均值、90/95/99百分位
- 并发用户数: 系统能够同时支持的活跃用户数
- 错误率: 在不同负载情况下的请求失败率
- 资源利用率: CPU、内存、网络I/O、磁盘I/O使用率

### 4.2 业务指标

- 消息投递成功率
- 消息延迟时间
- 客服响应时间
- AI回复生成时间
- WebSocket连接稳定性

## 5. 测试场景设计

### 5.1 基础API压测

#### 5.1.1 登录认证性能测试

- **目标**: 测试系统处理大量并发登录请求的能力
- **虚拟用户数**: 500-5000
- **加压方式**: 阶梯式（每30秒增加500用户）
- **持续时间**: 10分钟
- **测试接口**: `/api/v1/auth/login`

#### 5.1.2 消息发送性能测试

- **目标**: 测试系统处理高并发消息的能力
- **虚拟用户数**: 1000
- **每用户发送频率**: 2-10条/分钟
- **消息类型**: 文本、图片、文件混合
- **持续时间**: 30分钟
- **测试接口**: `/api/v1/message/send`

### 5.2 实时通信压测

#### 5.2.1 WebSocket连接测试

- **目标**: 测试系统维持大量长连接的能力
- **连接数**: 5000-20000
- **加压方式**: 阶梯式（每分钟增加1000连接）
- **持续时间**: 60分钟
- **测试指标**: 连接成功率、断连率、消息投递成功率

#### 5.2.2 高并发群聊测试

- **目标**: 测试群聊场景下的消息广播性能
- **群数量**: 100个
- **每群用户**: 50-200人
- **消息频率**: 5-20条/秒/群
- **持续时间**: 20分钟
- **测试指标**: 消息投递时延、CPU使用率、内存使用情况

### 5.3 客服系统压测

#### 5.3.1 客服分配性能测试

- **目标**: 测试大量客户同时接入时的分配性能
- **虚拟客户数**: 3000
- **客服坐席数**: 100
- **接入模式**: 批量接入（每秒50-100新客户）
- **持续时间**: 15分钟
- **测试指标**: 分配成功率、平均分配时间、系统资源使用情况

#### 5.3.2 客服工作台操作测试

- **目标**: 测试客服同时处理多会话的系统性能
- **客服数量**: 50
- **每客服会话数**: 10-30会话
- **操作频率**: 每秒1-3次操作（回复、转接、查询等）
- **持续时间**: 30分钟
- **测试指标**: 操作响应时间、系统稳定性

### 5.4 AI系统压测

#### 5.4.1 AI问答性能测试

- **目标**: 测试AI回复生成性能
- **并发请求数**: 50-200
- **请求内容**: 混合简单和复杂问题
- **持续时间**: 15分钟
- **测试指标**: 响应时间、系统资源占用、超时率

#### 5.4.2 知识库查询测试

- **目标**: 测试知识库的查询性能
- **并发请求数**: 100-500
- **查询类型**: 精确匹配和模糊匹配混合
- **持续时间**: 20分钟
- **测试指标**: 查询响应时间、数据库性能指标

### 5.5 混合场景测试

#### 5.5.1 全功能混合压测

- **目标**: 模拟真实生产环境下的混合负载
- **虚拟用户**: 总计10000用户，混合执行以上所有场景
- **持续时间**: 60分钟
- **测试指标**: 系统整体稳定性、响应时间、资源使用情况

## 6. 测试工具与脚本

### 6.1 JMeter测试计划

#### 6.1.1 JMeter脚本结构

```
jmeter/
  ├── scripts/
  │   ├── 01_login_test.jmx
  │   ├── 02_message_test.jmx
  │   ├── 03_websocket_test.jmx
  │   ├── 04_groupchat_test.jmx
  │   ├── 05_cs_allocation_test.jmx
  │   ├── 06_cs_operation_test.jmx
  │   ├── 07_ai_qa_test.jmx
  │   ├── 08_kb_query_test.jmx
  │   └── 09_mixed_test.jmx
  ├── data/
  │   ├── users.csv
  │   ├── messages.csv
  │   └── questions.csv
  └── results/
      ├── raw/
      └── reports/
```

#### 6.1.2 JMeter线程组配置示例

登录测试线程组配置:

```xml
<ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="Login Test" enabled="true">
  <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
  <elementProp name="ThreadGroup.main_controller" elementType="LoopController" guiclass="LoopControlPanel" testclass="LoopController" testname="Loop Controller" enabled="true">
    <boolProp name="LoopController.continue_forever">false</boolProp>
    <stringProp name="LoopController.loops">1</stringProp>
  </elementProp>
  <stringProp name="ThreadGroup.num_threads">500</stringProp>
  <stringProp name="ThreadGroup.ramp_time">30</stringProp>
  <boolProp name="ThreadGroup.scheduler">true</boolProp>
  <stringProp name="ThreadGroup.duration">600</stringProp>
  <stringProp name="ThreadGroup.delay">0</stringProp>
</ThreadGroup>
```

### 6.2 测试数据准备

#### 6.2.1 测试数据生成脚本

创建用于生成测试数据的脚本:

```bash
#!/bin/bash
# generate_test_data.sh
# 生成测试所需的用户、消息和问题数据

# 生成用户数据
echo "username,password,email" > users.csv
for i in $(seq 1 10000); do
  echo "user$i,password$i,user$i@example.com" >> users.csv
done

# 生成消息数据
echo "message_content,message_type" > messages.csv
for i in $(seq 1 1000); do
  echo "This is test message $i,text" >> messages.csv
done

# 生成AI问题数据
echo "question" > questions.csv
for i in $(seq 1 500); do
  echo "What is the answer to question $i?" >> questions.csv
done
```

## 7. 测试执行计划

### 7.1 准备阶段

1. 配置测试环境并部署应用
2. 准备测试数据
3. 配置监控系统
4. 进行小规模测试验证测试脚本

### 7.2 执行阶段

1. 分场景执行单项测试，从低负载开始，逐步增加
2. 记录每个场景的测试结果和系统表现
3. 待单项测试完成后，执行混合场景测试
4. 连续监控系统性能指标

### 7.3 执行脚本

```bash
#!/bin/bash
# run_load_tests.sh

BASE_DIR=$(pwd)
JMETER_BIN="/path/to/jmeter/bin"
SCRIPTS_DIR="$BASE_DIR/jmeter/scripts"
RESULTS_DIR="$BASE_DIR/jmeter/results/raw"
REPORT_DIR="$BASE_DIR/jmeter/results/reports"

# 创建结果目录
mkdir -p $RESULTS_DIR
mkdir -p $REPORT_DIR

# 运行所有测试脚本
for script in $SCRIPTS_DIR/*.jmx; do
  script_name=$(basename "$script" .jmx)
  echo "Running test: $script_name"
  
  # 执行JMeter测试
  $JMETER_BIN/jmeter -n -t "$script" \
    -l "$RESULTS_DIR/${script_name}_result.jtl" \
    -e -o "$REPORT_DIR/$script_name"
  
  echo "Test $script_name completed"
  
  # 等待系统恢复
  echo "Waiting 2 minutes for system to stabilize..."
  sleep 120
done

echo "All tests completed"
```

## 8. 监控配置

### 8.1 Prometheus配置

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'bytedesk'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ['bytedesk-app:9003']
```

### 8.2 Grafana仪表盘

创建以下Grafana仪表盘:

1. 系统概览仪表盘
   - JVM内存使用、GC情况
   - CPU使用率
   - 网络I/O
   - 响应时间变化

2. 数据库性能仪表盘
   - 连接池使用情况
   - 查询执行时间
   - 事务数
   - 慢查询分析

3. 应用指标仪表盘
   - HTTP请求统计
   - WebSocket连接数
   - 错误率
   - 自定义业务指标

## 9. 结果分析与报告

### 9.1 性能指标收集

- JMeter生成的聚合报告
- Prometheus指标数据
- 系统日志分析
- 数据库性能数据

### 9.2 性能测试报告

- [测试报告模板](./bytedesk-load-test-report.md)

### 9.3 结果图表示例

以下是测试结果的可视化图表类型:

1. 响应时间与并发用户关系图
2. 吞吐量与并发用户关系图
3. 错误率与并发用户关系图
4. 资源使用率趋势图
5. 数据库性能指标图
6. 各API端点响应时间对比图

## 10. 性能优化策略

### 10.1 常见瓶颈及优化方向

#### 10.1.1 应用层优化

- JVM参数调优: 根据测试结果调整堆大小、GC策略
- 线程池优化: 调整各业务线程池大小和策略
- 代码级优化: 优化高频调用路径、减少不必要的对象创建

#### 10.1.2 数据库优化

- 索引优化: 针对高频查询添加合适索引
- SQL优化: 重写复杂查询、添加适当的查询提示
- 连接池配置: 优化连接池大小和超时设置

#### 10.1.3 中间件优化

- Redis配置: 优化缓存策略和内存管理
- 消息队列: 调整队列大小和消费者数量
- WebSocket: 优化心跳机制和连接管理

#### 10.1.4 架构优化

- 服务拆分: 识别并拆分高负载服务
- 读写分离: 实施数据库读写分离
- 分库分表: 对高频访问表进行水平拆分

### 10.2 扩展策略

- 垂直扩展: 增加单机资源（CPU、内存）
- 水平扩展: 增加服务节点数量
- 混合扩展: 结合垂直和水平扩展策略

## 11. 持续性能测试

### 11.1 持续集成中的性能测试

- 集成JMeter到CI/CD流程
- 设置性能基准和警报阈值
- 自动生成性能趋势报告

### 11.2 性能监控告警

- 建立关键指标的监控告警
- 配置性能退化自动通知
- 性能数据历史趋势分析

## 12. 附录

### 12.1.1 JMeter测试参数说明

- 线程组参数详解
- 定时器使用策略
- 断言配置建议
- 结果收集器配置

### 12.1.2 系统配置参考

- 应用服务器推荐配置
- JVM参数建议
- 数据库参数建议
- 网络参数优化

---

## 编写者与审核者

- 编写者: [姓名]
- 审核者: [姓名]
- 编写日期: [日期]
- 版本: 1.0
