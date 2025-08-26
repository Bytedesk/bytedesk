# MCP Server 配置管理

## 概述

本模块提供了完整的 MCP (Model Context Protocol) Server 配置管理功能，用于存储和管理 MCP 服务器的连接信息、能力配置和运行时状态。

## 主要组件

### 1. McpServerEntity

存储 MCP Server 配置的实体类，包含以下主要字段：

#### 基本信息

- `name`: 服务器名称
- `description`: 服务器描述
- `serverType`: 服务器类型 (THREAD, CUSTOMER, TICKET, KNOWLEDGE, FILESYSTEM, DATABASE, SEARCH, CUSTOM)
- `serverVersion`: 服务器版本

#### 连接配置

- `serverUrl`: 连接 URI/URL
- `host`: 服务器主机地址
- `port`: 服务器端口
- `protocol`: 连接协议 (http, https, websocket 等)

#### 认证配置

- `authToken`: 认证令牌
- `authType`: 认证类型 (bearer, basic, api_key 等)
- `authHeaders`: 额外认证头信息 (JSON 格式)

#### 连接参数

- `connectionTimeout`: 连接超时时间 (毫秒)
- `readTimeout`: 读取超时时间 (毫秒)
- `maxRetries`: 最大重试次数

#### 服务器能力

- `capabilities`: 服务器能力 (JSON 格式)
- `availableTools`: 可用工具/函数 (JSON 格式)
- `availableResources`: 可用资源 (JSON 格式)
- `availablePrompts`: 可用提示 (JSON 格式)

#### 配置与环境

- `configJson`: 服务器配置 (JSON 格式)
- `environmentVars`: 环境变量 (JSON 格式)

#### 状态管理

- `status`: 服务器状态 (ACTIVE, INACTIVE, ERROR, CONNECTING 等)
- `enabled`: 是否启用
- `autoStart`: 是否自动启动

#### 健康检查

- `healthCheckUrl`: 健康检查 URL
- `healthCheckInterval`: 健康检查间隔 (秒)
- `lastHealthCheck`: 最后健康检查时间
- `lastConnected`: 最后连接时间
- `lastError`: 最后错误信息

#### 其他属性

- `priority`: 优先级 (用于服务器选择)
- `tags`: 标签 (用于分类和过滤)
- `metadata`: 元数据 (JSON 格式)
- `usageStats`: 使用统计 (JSON 格式)

### 2. McpServerRequest

处理客户端请求的数据传输对象，包含与 Entity 相对应的字段，用于创建和更新 MCP Server 配置。

### 3. McpServerResponse

返回给客户端的响应对象，包含所有必要的服务器配置信息，隐藏敏感信息如认证令牌。

### 4. McpServerExcel

支持 Excel 导入/导出功能的数据对象，包含主要配置字段，便于批量管理服务器配置。

### 5. McpServerTypeEnum

定义支持的 MCP Server 类型：

- `THREAD`: 对话管理服务器
- `CUSTOMER`: 客户服务服务器
- `TICKET`: 工单管理服务器
- `KNOWLEDGE`: 知识库服务器
- `FILESYSTEM`: 文件系统服务器
- `DATABASE`: 数据库服务器
- `SEARCH`: 网络搜索服务器
- `CUSTOM`: 自定义服务器

## 数据库表结构

表名：`bytedesk_ai_mcp_server`

主要字段映射：

- `server_type`: 服务器类型
- `server_version`: 服务器版本
- `server_url`: 服务器 URL
- `auth_token`: 认证令牌
- `auth_type`: 认证类型
- `auth_headers`: 认证头信息
- `connection_timeout`: 连接超时
- `read_timeout`: 读取超时
- `max_retries`: 最大重试次数
- `is_enabled`: 是否启用
- `auto_start`: 是否自动启动
- `health_check_url`: 健康检查 URL
- `health_check_interval`: 健康检查间隔
- `last_health_check`: 最后健康检查时间
- `last_connected`: 最后连接时间
- `last_error`: 最后错误信息
- `usage_stats`: 使用统计

## 使用示例

### 创建 MCP Server 配置

```java
McpServerRequest request = McpServerRequest.builder()
    .name("Knowledge Base Server")
    .description("MCP server for knowledge base access")
    .serverType(McpServerTypeEnum.KNOWLEDGE.name())
    .serverUrl("http://localhost:8080/mcp")
    .host("localhost")
    .port(8080)
    .protocol("http")
    .authType("bearer")
    .connectionTimeout(30000)
    .readTimeout(60000)
    .enabled(true)
    .autoStart(true)
    .build();

McpServerResponse response = mcpServerRestService.create(request);
```

### 查询 MCP Server

```java
McpServerRequest queryRequest = McpServerRequest.builder()
    .serverType(McpServerTypeEnum.KNOWLEDGE.name())
    .enabled(true)
    .build();

Page<McpServerResponse> servers = mcpServerRestService.queryByOrg(queryRequest);
```

## API 端点

- `GET /api/v1/mcp/server/query/org` - 查询组织的 MCP 服务器
- `GET /api/v1/mcp/server/query/user` - 查询用户的 MCP 服务器
- `GET /api/v1/mcp/server/query` - 根据 UID 查询特定服务器
- `POST /api/v1/mcp/server/create` - 创建新的 MCP 服务器
- `POST /api/v1/mcp/server/update` - 更新 MCP 服务器配置
- `POST /api/v1/mcp/server/delete` - 删除 MCP 服务器
- `GET /api/v1/mcp/server/export` - 导出服务器配置到 Excel

## 注意事项

1. 认证令牌等敏感信息不会在 Response 中返回
2. 服务器状态会根据健康检查结果自动更新
3. 支持根据优先级进行服务器选择
4. 所有配置更改都会触发相应的事件，便于其他系统集成
5. 支持标签分类和元数据扩展，提供灵活的管理方式

## 扩展性

该设计支持：

- 添加新的服务器类型
- 扩展认证方式
- 自定义健康检查策略
- 集成外部监控系统
- 动态配置更新
