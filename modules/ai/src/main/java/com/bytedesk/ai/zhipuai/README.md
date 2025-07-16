# 智谱AI模块使用说明

本模块使用 `oapi-java-sdk` 实现了智谱AI的各种功能，包括聊天、Function Calling、角色扮演、图像生成、向量嵌入、语音合成、文件管理等。

## 配置

在 `application.yml` 中添加以下配置：

```yaml
zhipuai:
  enabled: true
  api-key: "your_zhipuai_api_key_here"
  model: "glm-4"
  temperature: 0.7
  top-p: 0.9
  max-tokens: 4096
  connection-timeout: 30
  read-timeout: 10
  write-timeout: 10
  ping-interval: 10
  max-idle-connections: 8
  keep-alive-duration: 1
```

## API 接口

### 1. 基础聊天功能

#### 同步聊天

```
GET /zhipuai/sync?message=你好
```

#### 流式聊天

```
GET /zhipuai/stream?message=你好
```

#### SSE聊天

```
GET /zhipuai/sse?message=你好
```

#### 角色扮演

```
GET /zhipuai/roleplay?message=你好&userInfo=我是导演&botInfo=你是歌手&botName=苏梦圆&userName=陆星辰
```

#### 异步聊天

```
POST /zhipuai/async
Content-Type: application/json

{
  "message": "你好"
}
```

#### 带Web搜索的聊天

```
POST /zhipuai/websearch
Content-Type: application/json

{
  "message": "查询最新科技新闻",
  "searchQuery": "最新科技新闻"
}
```

#### 语音模型聊天

```
POST /zhipuai/voice
Content-Type: application/json

{
  "message": "给我讲个冷笑话"
}
```

### 2. Function Calling 功能

#### Function Calling 同步调用

```
POST /zhipuai/function-call
Content-Type: application/json

{
  "message": "查询北京的天气",
  "functions": [
    {
      "name": "get_weather",
      "description": "获取指定城市的天气信息",
      "parameters": {
        "type": "object",
        "properties": {
          "city": {
            "type": "string",
            "description": "城市名称"
          }
        },
        "required": ["city"]
      }
    }
  ]
}
```

#### Function Calling 流式调用

```
POST /zhipuai/function-call/stream
Content-Type: application/json

{
  "message": "查询北京的天气",
  "functions": [
    {
      "name": "get_weather",
      "description": "获取指定城市的天气信息",
      "parameters": {
        "type": "object",
        "properties": {
          "city": {
            "type": "string",
            "description": "城市名称"
          }
        },
        "required": ["city"]
      }
    }
  ]
}
```

#### 示例：天气查询

```
GET /zhipuai/weather?city=北京
```

#### 示例：航班查询

```
GET /zhipuai/flight?from=成都&to=北京
```

### 3. 图像生成功能

#### 图像生成

```
POST /zhipuai/image
Content-Type: application/json

{
  "prompt": "一个未来主义的云数据中心",
  "requestId": "optional-request-id"
}
```

### 4. 向量嵌入功能

#### 单文本向量嵌入

```
POST /zhipuai/embedding
Content-Type: application/json

{
  "text": "hello world"
}
```

#### 批量文本向量嵌入

```
POST /zhipuai/embeddings
Content-Type: application/json

{
  "texts": ["hello world", "你好世界", "bonjour le monde"]
}
```

### 5. 语音合成功能

#### 基础语音合成

```
POST /zhipuai/speech
Content-Type: application/json

{
  "text": "智谱，你好呀",
  "voice": "child",
  "responseFormat": "wav"
}
```

#### 自定义语音合成

```
POST /zhipuai/speech/custom
Content-Type: application/json

{
  "text": "智谱，你好呀",
  "voiceText": "这是一条测试用例",
  "voiceDataPath": "/path/to/voice/data.wav",
  "responseFormat": "wav"
}
```

### 6. 文件管理功能

#### 文件上传

```
POST /zhipuai/file/upload
Content-Type: application/json

{
  "filePath": "/path/to/file.jsonl",
  "purpose": "fine-tune"
}
```

#### 查询文件列表

```
GET /zhipuai/files
```

#### 下载文件

```
POST /zhipuai/file/download
Content-Type: application/json

{
  "fileId": "file-20240118082608327-kp8qr",
  "outputPath": "/path/to/output/file.jsonl"
}
```

### 7. 微调功能

#### 创建微调任务

```
POST /zhipuai/finetune/create
Content-Type: application/json

{
  "model": "chatglm3-6b",
  "trainingFile": "file-20240118082608327-kp8qr"
}
```

#### 查询微调任务

```
GET /zhipuai/finetune/{jobId}
```

### 8. 健康检查

```
GET /zhipuai/health
```

## 功能特性

### 1. 基础聊天

- 支持同步、流式、SSE三种调用方式
- 支持异步聊天
- 支持Web搜索增强聊天
- 支持语音模型聊天
- 可自定义模型和温度参数
- 自动错误处理和重试

### 2. Function Calling

- 支持自定义函数定义
- 支持同步和流式调用
- 提供天气查询、航班查询等示例
- 支持Web搜索工具

### 3. 角色扮演

- 支持用户和机器人角色设定
- 使用 CharGLM3 模型
- 可自定义角色信息

### 4. 图像生成

- 支持文本到图像生成
- 使用 CogView 模型
- 可自定义请求ID

### 5. 向量嵌入

- 支持单文本和批量文本嵌入
- 使用 Embedding2 模型
- 返回高维向量表示

### 6. 语音合成

- 支持基础语音合成
- 支持自定义语音合成
- 支持多种音频格式输出

### 7. 文件管理

- 支持文件上传
- 支持文件列表查询
- 支持文件下载
- 支持微调数据集管理

### 8. 模型微调

- 支持创建微调任务
- 支持查询微调任务状态
- 支持微调模型管理

### 9. 配置管理

- 支持多种网络参数配置
- 支持连接池配置
- 支持token缓存

## 使用示例

### Java代码调用

```java
@Autowired
private ZhipuaiService zhipuaiService;

// 同步聊天
String response = zhipuaiService.chatSync("你好");

// 流式聊天
Flux<String> stream = zhipuaiService.chatStream("你好");

// 角色扮演
String roleResponse = zhipuaiService.rolePlayChat(
    "你好", 
    "我是导演", 
    "你是歌手", 
    "苏梦圆", 
    "陆星辰"
);

// Function Calling
List<ChatFunction> functions = new ArrayList<>();
ChatFunction function = ChatFunction.builder()
    .name("get_weather")
    .description("获取天气信息")
    .parameters(parameters)
    .build();
functions.add(function);

String funcResponse = zhipuaiService.functionCallingChat("查询北京天气", functions);

// 图像生成
String imageUrl = zhipuaiService.generateImage("一个未来主义的云数据中心");

// 向量嵌入
List<Double> embedding = zhipuaiService.getEmbedding("hello world");

// 语音合成
File speechFile = zhipuaiService.generateSpeech("智谱，你好呀", "child", "wav");

// 文件上传
String fileId = zhipuaiService.uploadFile("/path/to/file.jsonl", "fine-tune");

// 创建微调任务
String jobId = zhipuaiService.createFineTuningJob("chatglm3-6b", fileId);
```

### cURL 示例

```bash
# 同步聊天
curl "http://localhost:8080/zhipuai/sync?message=你好"

# 流式聊天
curl "http://localhost:8080/zhipuai/stream?message=你好"

# Function Calling
curl -X POST "http://localhost:8080/zhipuai/function-call" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "查询北京的天气",
    "functions": [
      {
        "name": "get_weather",
        "description": "获取指定城市的天气信息",
        "parameters": {
          "type": "object",
          "properties": {
            "city": {
              "type": "string",
              "description": "城市名称"
            }
          },
          "required": ["city"]
        }
      }
    ]
  }'

# 图像生成
curl -X POST "http://localhost:8080/zhipuai/image" \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "一个未来主义的云数据中心"
  }'

# 向量嵌入
curl -X POST "http://localhost:8080/zhipuai/embedding" \
  -H "Content-Type: application/json" \
  -d '{
    "text": "hello world"
  }'

# 语音合成
curl -X POST "http://localhost:8080/zhipuai/speech" \
  -H "Content-Type: application/json" \
  -d '{
    "text": "智谱，你好呀",
    "voice": "child",
    "responseFormat": "wav"
  }'

# 文件上传
curl -X POST "http://localhost:8080/zhipuai/file/upload" \
  -H "Content-Type: application/json" \
  -d '{
    "filePath": "/path/to/file.jsonl",
    "purpose": "fine-tune"
  }'
```

## 注意事项

1. 需要有效的智谱AI API密钥
2. 确保网络连接正常
3. 注意API调用频率限制
4. 建议在生产环境中配置适当的超时时间
5. 流式调用需要客户端支持SSE或WebSocket
6. 文件上传需要确保文件路径正确且有读取权限
7. 语音合成会生成临时文件，注意磁盘空间
8. 微调任务需要有效的训练文件ID

## 错误处理

模块内置了完善的错误处理机制：

- API调用失败时返回错误信息
- 网络超时自动重试
- 客户端不可用时返回友好提示
- 详细的日志记录便于调试
- 文件操作异常处理
- 参数验证和错误提示

## 扩展功能

如需添加更多功能，可以：

1. 在 `ZhipuaiService` 中添加相应方法
2. 在 `ZhipuaiController` 中添加对应的API接口
3. 更新配置文件以支持新的参数
4. 参考 `V4Test.java` 中的示例实现更多功能
