# Bytedesk 插件系统

## 概述

Bytedesk 插件系统提供了一个统一的框架来管理各个功能模块，实现模块化架构和集中管理。

## 架构设计

### 核心组件

1. **BytedeskPlugin 接口** - 定义插件的基本契约
2. **AbstractBytedeskPlugin** - 提供插件的通用实现
3. **PluginRegistry** - 插件注册中心，管理所有插件
4. **PluginController** - REST API 接口，提供插件信息查询

### 插件生命周期

```
注册 -> 初始化 -> 运行 -> 销毁
```

## 已注册插件

| 插件ID | 名称 | 描述 | 优先级 | 依赖 |
|--------|------|------|--------|------|
| service | Customer Service | 在线客服系统 | 10 | core |
| ai | AI Assistant | AI智能助手 | 15 | core, kbase |
| kbase | Knowledge Base | 知识库管理 | 20 | core |
| ticket | Ticket System | 工单管理系统 | 30 | core |
| call | Call Center | 呼叫中心 | 40 | core |
| voc | Voice of Customer | 客户之声 | 50 | core |

## API 接口

### 1. 获取所有插件

```http
GET /api/v1/plugins
```

**响应示例：**
```json
{
  "total": 6,
  "enabled": 5,
  "plugins": [
    {
      "id": "service",
      "name": "Customer Service",
      "description": "在线客服系统，提供实时聊天、会话管理、客服分配、消息队列等功能",
      "version": "1.0.0",
      "enabled": true,
      "priority": 10
    }
  ]
}
```

### 2. 获取插件概览

```http
GET /api/v1/plugins/overview
```

### 3. 获取插件详情

```http
GET /api/v1/plugins/{pluginId}
```

### 4. 获取插件健康状态

```http
GET /api/v1/plugins/{pluginId}/health
```

### 5. 获取所有插件健康状态

```http
GET /api/v1/plugins/health
```

### 6. 获取插件统计信息

```http
GET /api/v1/plugins/{pluginId}/statistics
```

### 7. 获取所有插件统计信息

```http
GET /api/v1/plugins/statistics
```

## 创建自定义插件

### 步骤 1: 创建插件类

```java
package com.bytedesk.yourmodule.plugin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import com.bytedesk.core.plugin.AbstractBytedeskPlugin;

@Component
public class YourModulePlugin extends AbstractBytedeskPlugin {
    
    @Value("${bytedesk.yourmodule.enabled:true}")
    private boolean enabled;
    
    @Value("${bytedesk.yourmodule.version:1.0.0}")
    private String version;
    
    @Autowired(required = false)
    private HealthIndicator yourModuleHealthIndicator;
    
    @Override
    protected HealthIndicator getHealthIndicator() {
        return yourModuleHealthIndicator;
    }
    
    @Override
    public String getPluginId() {
        return "yourmodule";
    }
    
    @Override
    public String getPluginName() {
        return "Your Module Name";
    }
    
    @Override
    public String getDescription() {
        return "Your module description";
    }
    
    @Override
    public String getVersion() {
        return version;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public int getPriority() {
        return 100; // 设置优先级
    }
    
    @Override
    public String[] getDependencies() {
        return new String[]{"core"}; // 设置依赖
    }
}
```

### 步骤 2: 配置文件

在 `application.yml` 中添加：

```yaml
bytedesk:
  yourmodule:
    enabled: true
    version: 1.0.0
```

### 步骤 3: 创建健康检查器（可选）

```java
@Slf4j
@Component
public class YourModuleHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        try {
            // 执行健康检查逻辑
            return Health.up()
                    .withDetail("status", "Running")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
```

## 插件优先级

优先级数值越小，优先级越高。建议范围：

- **1-10**: 核心功能（如 service）
- **11-20**: 重要功能（如 ai）
- **21-30**: 常规功能（如 kbase）
- **31-50**: 辅助功能（如 ticket, call, voc）
- **51-100**: 扩展功能

## 插件依赖

插件可以声明依赖其他插件：

```java
@Override
public String[] getDependencies() {
    return new String[]{"core", "kbase"};
}
```

插件注册中心会在注册时检查依赖是否满足。

## 配置选项

### 启用/禁用插件

```yaml
bytedesk:
  kbase:
    enabled: true  # 启用知识库插件
  call:
    enabled: false # 禁用呼叫中心插件
```

### 设置插件版本

```yaml
bytedesk:
  service:
    version: 1.2.0
```

## 监控和管理

### 通过 Actuator 端点

```bash
# 查看应用健康状态（包含所有模块）
curl http://localhost:9003/actuator/health

# 查看特定模块健康状态
curl http://localhost:9003/actuator/health/kbase
curl http://localhost:9003/actuator/health/service
```

### 通过插件 API

```bash
# 查看插件概览
curl http://localhost:9003/api/v1/plugins/overview

# 查看所有插件健康状态
curl http://localhost:9003/api/v1/plugins/health

# 查看特定插件详情
curl http://localhost:9003/api/v1/plugins/kbase
```

## 最佳实践

1. **命名规范**
   - 插件ID：使用小写字母，如 `kbase`, `service`
   - 类名：使用 PascalCase + Plugin 后缀，如 `KbasePlugin`

2. **版本管理**
   - 使用语义化版本号：`major.minor.patch`
   - 在配置文件中集中管理版本

3. **健康检查**
   - 为每个插件提供健康检查器
   - 检查关键资源（数据库、缓存、外部服务等）

4. **依赖管理**
   - 明确声明插件依赖
   - 避免循环依赖

5. **错误处理**
   - 插件初始化失败不应影响其他插件
   - 提供详细的错误信息和日志

## 故障排查

### 插件未注册

**问题：** 插件列表中看不到某个插件

**排查步骤：**
1. 检查插件类是否添加了 `@Component` 注解
2. 检查插件类所在包是否被扫描
3. 查看启动日志中的插件注册信息
4. 检查插件是否被配置为禁用状态

### 依赖检查失败

**问题：** 插件因依赖问题注册失败

**排查步骤：**
1. 检查依赖的插件是否已注册
2. 查看插件注册顺序（按优先级）
3. 确认依赖关系是否正确

### 健康检查失败

**问题：** 插件健康状态为 DOWN

**排查步骤：**
1. 检查 HealthIndicator 实现
2. 查看健康检查错误日志
3. 确认相关资源（数据库、Redis 等）是否正常

## 示例代码

### 获取插件信息

```java
@Autowired
private PluginRegistry pluginRegistry;

// 获取所有插件
List<BytedeskPlugin> plugins = pluginRegistry.getAllPlugins();

// 获取特定插件
Optional<BytedeskPlugin> plugin = pluginRegistry.getPlugin("kbase");

// 检查插件是否启用
boolean isEnabled = pluginRegistry.isPluginEnabled("service");

// 获取插件健康状态
Map<String, Object> health = plugin.get().getHealthStatus();
```

## 未来扩展

- [ ] 插件热加载/卸载
- [ ] 插件配置动态更新
- [ ] 插件间通信机制
- [ ] 插件市场
- [ ] 插件权限管理

## 相关文档

- [Health Indicator 文档](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints.health)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [模块化架构设计](./ARCHITECTURE.md)

## 许可证

Business Source License 1.1 - https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
