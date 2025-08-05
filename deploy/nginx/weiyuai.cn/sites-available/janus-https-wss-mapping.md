# Janus HTTPS/WSS 配置映射说明

## 当前配置状态

### Janus 服务端配置

#### HTTP/HTTPS 配置 (janus.transport.http.jcfg)

```
general: {
    http = true                     # HTTP 已启用
    port = 8088                     # HTTP 端口
    https = false                   # HTTPS 已禁用
    #secure_port = 8089            # HTTPS 端口 (注释掉)
}

admin: {
    admin_http = true               # Admin HTTP 已启用
    admin_port = 7088               # Admin HTTP 端口
    admin_https = false             # Admin HTTPS 已禁用
    admin_secure_port = 7889        # Admin HTTPS 端口 (已设置但未启用)
}
```

#### WebSocket 配置 (janus.transport.websockets.jcfg)

```
general: {
    ws = true                       # WebSocket 已启用
    ws_port = 8188                  # WebSocket 端口
    wss = true                      # WSS 已启用
    wss_port = 8989                 # WSS 端口
}

admin: {
    admin_ws = true                 # Admin WebSocket 已启用
    admin_ws_port = 7188            # Admin WebSocket 端口
    admin_wss = true                # Admin WSS 已启用
    admin_wss_port = 7989           # Admin WSS 端口
}
```

## Nginx 映射配置

### 当前映射关系

| Janus 服务 | Janus 端口 | Nginx 路径 | 协议 | 状态 |
|------------|------------|------------|------|------|
| HTTP API | 8088 | `/janus` | HTTP → HTTPS | ✅ 已配置 |
| Admin HTTP | 7088 | `/admin` | HTTP → HTTPS | ✅ 已配置 |
| WebSocket | 8188 | `/janus/` | WS → WSS | ✅ 已配置 |
| WSS | 8989 | `/janus-wss/` | WSS → WSS | ✅ 新增 |
| Admin WebSocket | 7188 | `/admin/` | WS → WSS | ✅ 已配置 |
| Admin WSS | 7989 | `/admin-wss/` | WSS → WSS | ✅ 新增 |

### 访问方式

#### HTTP API (推荐用于生产环境)

```javascript
// 客户端访问
const janus = new Janus({
    server: 'https://janus.weiyuai.cn/janus',
    // 其他配置...
});
```

#### WebSocket API (推荐用于实时通信)

```javascript
// 客户端访问 - 普通 WebSocket
const janus = new Janus({
    server: 'wss://janus.weiyuai.cn/janus/',
    // 其他配置...
});

// 客户端访问 - 安全 WebSocket (如果启用)
const janus = new Janus({
    server: 'wss://janus.weiyuai.cn/janus-wss/',
    // 其他配置...
});
```

#### Admin API

```javascript
// Admin HTTP API
const adminUrl = 'https://janus.weiyuai.cn/admin';

// Admin WebSocket API
const adminWsUrl = 'wss://janus.weiyuai.cn/admin/';

// Admin WSS API (如果启用)
const adminWssUrl = 'wss://janus.weiyuai.cn/admin-wss/';
```

## 配置建议

### 1. 启用 Janus HTTPS (可选)

如果要让 Janus 内部也使用 HTTPS，修改 `janus.transport.http.jcfg`：

```javascript
general: {
    https = true                    // 启用 HTTPS
    secure_port = 8089              // HTTPS 端口
    // ... 其他配置
}

admin: {
    admin_https = true              // 启用 Admin HTTPS
    admin_secure_port = 7889        // Admin HTTPS 端口
    // ... 其他配置
}
```

然后更新 Nginx 配置：

```nginx
# HTTPS API 映射
location /janus-https {
    proxy_pass https://121.36.247.120:8089;
    # ... 其他配置
}

# Admin HTTPS 映射
location /admin-https {
    proxy_pass https://121.36.247.120:7889;
    # ... 其他配置
}
```

### 2. 证书配置

Janus 配置文件中的证书路径：

```javascript
certificates: {
    cert_pem = "/root/janus/weiyuai.cn/cert.pem"
    cert_key = "/root/janus/weiyuai.cn/privkey.pem"
}
```

确保这些证书文件存在且有效。

### 3. CORS 配置

在 Janus 配置中启用 CORS：

```javascript
cors: {
    allow_origin = "https://janus.weiyuai.cn"
    enforce_cors = true
}
```

## 测试连接

### 1. 测试 HTTP API

```bash
curl -X POST https://janus.weiyuai.cn/janus \
  -H "Content-Type: application/json" \
  -d '{"janus": "info", "transaction": "test"}'
```

### 2. 测试 Admin API

```bash
curl -X GET https://janus.weiyuai.cn/admin
```

### 3. 测试 WebSocket

```javascript
// 在浏览器控制台中测试
const ws = new WebSocket('wss://janus.weiyuai.cn/janus/');
ws.onopen = () => console.log('WebSocket connected');
ws.onmessage = (event) => console.log('Message:', event.data);
```

## 安全建议

1. **使用 WSS**: 在生产环境中始终使用 WSS 而不是 WS
2. **启用 CORS**: 配置适当的 CORS 策略
3. **限制访问**: 使用 ACL 限制 Admin API 的访问
4. **证书管理**: 确保 SSL 证书有效且定期更新
5. **防火墙**: 只开放必要的端口

## 故障排除

### 常见问题

1. **WebSocket 连接失败**
   - 检查 Janus 服务是否运行
   - 验证端口是否正确开放
   - 检查防火墙设置

2. **SSL 证书错误**
   - 验证证书文件路径
   - 检查证书有效性
   - 确认证书权限

3. **CORS 错误**
   - 检查 CORS 配置
   - 验证 Origin 设置
   - 查看浏览器控制台错误

### 日志检查

```bash
# 检查 Janus 日志
tail -f /var/log/janus/janus.log

# 检查 Nginx 日志
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
```
