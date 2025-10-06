# Bytedesk 插件系统 - 架构概览

## 系统架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                      Bytedesk Application                        │
├─────────────────────────────────────────────────────────────────┤
│                     Plugin Registry (Core)                       │
│                  - 插件注册管理                                    │
│                  - 生命周期管理                                    │
│                  - 依赖关系检查                                    │
└────────┬──────────┬──────────┬──────────┬──────────┬───────────┘
         │          │          │          │          │
    ┌────▼───┐ ┌───▼────┐ ┌───▼────┐ ┌───▼────┐ ┌──▼─────┐
    │ Core   │ │Service │ │  AI    │ │ Kbase  │ │Ticket  │ ...
    │Plugin  │ │Plugin  │ │Plugin  │ │Plugin  │ │Plugin  │
    └────┬───┘ └───┬────┘ └───┬────┘ └───┬────┘ └───┬────┘
         │         │          │          │          │
    ┌────▼────────────────────▼──────────▼──────────▼────────┐
    │              Health Indicators (Actuator)              │
    │    - CoreHealthIndicator                               │
    │    - ServiceHealthIndicator                            │
    │    - AiHealthIndicator                                 │
    │    - KbaseHealthIndicator                              │
    │    - TicketHealthIndicator                             │
    └────────────────────────────────────────────────────────┘
```

## 模块说明

### 核心层（Core Module）

**位置：** `modules/core/src/main/java/com/bytedesk/core/plugin/`

**组件：**
- `BytedeskPlugin.java` - 插件接口定义
- `AbstractBytedeskPlugin.java` - 插件抽象基类
- `PluginRegistry.java` - 插件注册中心
- `PluginController.java` - REST API 控制器
- `CorePlugin.java` - 核心模块插件实现

**功能：**
- 定义插件标准接口
- 提供插件注册和管理
- 实现插件生命周期管理
- 提供插件信息查询 API

### 业务模块插件

#### 1. 在线客服模块（Service）
**位置：** `modules/service/src/main/java/com/bytedesk/service/plugin/ServicePlugin.java`

**功能：** 实时聊天、会话管理、客服分配

**优先级：** 10（最高）

**依赖：** core

#### 2. AI 模块（AI）
**位置：** `modules/ai/src/main/java/com/bytedesk/ai/plugin/AiPlugin.java`

**功能：** 大模型集成、智能客服、对话生成

**优先级：** 15

**依赖：** core, kbase

#### 3. 知识库模块（Kbase）
**位置：** `modules/kbase/src/main/java/com/bytedesk/kbase/plugin/KbasePlugin.java`

**功能：** 文章管理、FAQ、向量检索

**优先级：** 20

**依赖：** core

#### 4. 工单系统模块（Ticket）
**位置：** `modules/ticket/src/main/java/com/bytedesk/ticket/plugin/TicketPlugin.java`

**功能：** 工单创建、流转、SLA 管理

**优先级：** 30

**依赖：** core

#### 5. 呼叫中心模块（Call）
**位置：** `modules/call/src/main/java/com/bytedesk/call/plugin/CallPlugin.java`

**功能：** 语音通话、FreeSWITCH 集成

**优先级：** 40

**依赖：** core

**注意：** 默认禁用，需要配置 FreeSWITCH 后启用

#### 6. 客户之声模块（VOC）
**位置：** `modules/voc/src/main/java/com/bytedesk/voc/plugin/VocPlugin.java`

**功能：** 客户反馈、满意度调查、数据分析

**优先级：** 50

**依赖：** core

## 文件结构

```
modules/
├── core/
│   └── src/main/java/com/bytedesk/core/
│       ├── plugin/
│       │   ├── BytedeskPlugin.java           # 插件接口
│       │   ├── AbstractBytedeskPlugin.java   # 抽象基类
│       │   ├── PluginRegistry.java           # 注册中心
│       │   ├── PluginController.java         # REST API
│       │   ├── CorePlugin.java               # 核心插件
│       │   ├── README.md                     # 使用文档
│       │   ├── ARCHITECTURE.md               # 架构文档
│       │   └── application-plugin-example.yml # 配置示例
│       └── config/
│           └── CoreHealthIndicator.java      # 核心健康检查
│
├── service/
│   └── src/main/java/com/bytedesk/service/
│       ├── plugin/
│       │   └── ServicePlugin.java            # 客服插件
│       └── config/
│           └── ServiceHealthIndicator.java   # 客服健康检查
│
├── ai/
│   └── src/main/java/com/bytedesk/ai/
│       ├── plugin/
│       │   └── AiPlugin.java                 # AI插件
│       └── config/
│           └── AiHealthIndicator.java        # AI健康检查
│
├── kbase/
│   └── src/main/java/com/bytedesk/kbase/
│       ├── plugin/
│       │   └── KbasePlugin.java              # 知识库插件
│       └── config/
│           └── KbaseHealthIndicator.java     # 知识库健康检查
│
├── ticket/
│   └── src/main/java/com/bytedesk/ticket/
│       ├── plugin/
│       │   └── TicketPlugin.java             # 工单插件
│       └── config/
│           └── TicketHealthIndicator.java    # 工单健康检查
│
├── call/
│   └── src/main/java/com/bytedesk/call/
│       ├── plugin/
│       │   └── CallPlugin.java               # 呼叫中心插件
│       └── config/
│           └── CallHealthIndicator.java      # 呼叫中心健康检查
│
└── voc/
    └── src/main/java/com/bytedesk/voc/
        ├── plugin/
        │   └── VocPlugin.java                # VOC插件
        └── config/
            └── VocHealthIndicator.java       # VOC健康检查
```

## 工作流程

### 1. 应用启动流程

```
1. Spring Boot 启动
   ↓
2. 扫描并加载所有 @Component 标注的插件类
   ↓
3. PluginRegistry 的 @PostConstruct 方法执行
   ↓
4. 按优先级排序插件列表
   ↓
5. 依次注册每个插件
   ├─ 检查插件依赖
   ├─ 调用插件的 initialize() 方法
   └─ 记录日志
   ↓
6. 所有插件注册完成
   ↓
7. 打印插件列表
```

### 2. 插件查询流程

```
客户端请求
   ↓
GET /api/v1/plugins
   ↓
PluginController.getAllPlugins()
   ↓
PluginRegistry.getAllPlugins()
   ↓
返回插件列表（JSON）
```

### 3. 健康检查流程

```
客户端请求
   ↓
GET /api/v1/plugins/{pluginId}/health
   ↓
PluginController.getPluginHealth()
   ↓
Plugin.getHealthStatus()
   ↓
HealthIndicator.health()
   ↓
返回健康状态（JSON）
```

## 关键特性

### 1. 自动发现
- 使用 Spring 的组件扫描自动发现插件
- 通过 `@Component` 注解标识插件类

### 2. 依赖管理
- 插件可以声明对其他插件的依赖
- 注册时自动检查依赖是否满足

### 3. 优先级控制
- 通过优先级控制插件注册顺序
- 优先级低的先注册，高的后注册

### 4. 生命周期管理
- `initialize()` - 插件初始化
- `destroy()` - 插件销毁
- 与 Spring 生命周期集成

### 5. 健康监控
- 每个插件可关联一个 HealthIndicator
- 通过 API 和 Actuator 端点查询健康状态

### 6. 动态配置
- 通过配置文件启用/禁用插件
- 无需修改代码即可控制模块

## API 端点总览

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/v1/plugins` | GET | 获取所有插件列表 |
| `/api/v1/plugins/enabled` | GET | 获取已启用的插件 |
| `/api/v1/plugins/overview` | GET | 获取插件概览 |
| `/api/v1/plugins/{id}` | GET | 获取指定插件详情 |
| `/api/v1/plugins/health` | GET | 获取所有插件健康状态 |
| `/api/v1/plugins/{id}/health` | GET | 获取指定插件健康状态 |
| `/api/v1/plugins/statistics` | GET | 获取所有插件统计信息 |
| `/api/v1/plugins/{id}/statistics` | GET | 获取指定插件统计信息 |

## Actuator 端点

| 端点 | 描述 |
|------|------|
| `/actuator/health` | 应用整体健康状态 |
| `/actuator/health/core` | 核心模块健康状态 |
| `/actuator/health/service` | 客服模块健康状态 |
| `/actuator/health/ai` | AI模块健康状态 |
| `/actuator/health/kbase` | 知识库模块健康状态 |
| `/actuator/health/ticket` | 工单模块健康状态 |
| `/actuator/health/call` | 呼叫中心模块健康状态 |
| `/actuator/health/voc` | VOC模块健康状态 |

## 配置项

```yaml
bytedesk:
  # 各模块配置
  core.enabled: true
  service.enabled: true
  ai.enabled: true
  kbase.enabled: true
  ticket.enabled: true
  call.enabled: false
  voc.enabled: true
  
  # 版本号配置
  core.version: 1.0.0
  service.version: 1.0.0
  # ... 其他模块版本
```

## 扩展性

### 添加新插件

1. 创建插件类，继承 `AbstractBytedeskPlugin`
2. 实现必要的方法
3. 添加 `@Component` 注解
4. 可选：创建对应的 HealthIndicator
5. 在配置文件中添加配置项

### 自定义插件行为

- 重写 `initialize()` 方法自定义初始化逻辑
- 重写 `getStatistics()` 方法提供自定义统计信息
- 重写 `getHealthStatus()` 方法自定义健康检查

## 监控和日志

### 启动日志示例

```
INFO  PluginRegistry - Initializing Plugin Registry...
INFO  CorePlugin - Initializing plugin: Core Module (core)
INFO  ServicePlugin - Initializing plugin: Customer Service (service)
INFO  AiPlugin - Initializing plugin: AI Assistant (ai)
INFO  KbasePlugin - Initializing plugin: Knowledge Base (kbase)
INFO  PluginRegistry - Plugin Registry initialized with 6 plugins
INFO  PluginRegistry - ==================================================
INFO  PluginRegistry - Registered Plugins (6)
INFO  PluginRegistry - ==================================================
INFO  PluginRegistry -   - Core Module (core) v1.0.0 [ENABLED] Priority: 1
INFO  PluginRegistry -   - Customer Service (service) v1.0.0 [ENABLED] Priority: 10
INFO  PluginRegistry -   - AI Assistant (ai) v1.0.0 [ENABLED] Priority: 15
INFO  PluginRegistry -   - Knowledge Base (kbase) v1.0.0 [ENABLED] Priority: 20
INFO  PluginRegistry -   - Ticket System (ticket) v1.0.0 [ENABLED] Priority: 30
INFO  PluginRegistry -   - Call Center (call) v1.0.0 [DISABLED] Priority: 40
INFO  PluginRegistry -   - Voice of Customer (voc) v1.0.0 [ENABLED] Priority: 50
INFO  PluginRegistry - ==================================================
```

## 许可证

Business Source License 1.1

## 联系方式

- Email: 270580156@qq.com
- Website: https://bytedesk.com
- GitHub: https://github.com/Bytedesk/bytedesk
