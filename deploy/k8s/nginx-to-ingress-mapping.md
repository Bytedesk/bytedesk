# NGINX 配置到 Kubernetes Ingress 映射指南

## 配置对比表

| NGINX 配置 | Kubernetes Ingress 配置 | 说明 |
|-------------|-------------------------|------|
| `upstream weiyuai` | `Service: bytedesk-service` | 负载均衡后端服务 |
| `ip_hash` | `affinity: "ip"` | 基于 IP 的会话亲和性 |
| `location /` | `path: /` | 根路径路由 |
| `location /websocket` | `path: /websocket` | WebSocket 路由 |
| `location /stomp` | `path: /stomp` | STOMP 路由 |
| `proxy_pass` | `backend.service` | 后端服务代理 |
| `proxy_set_header` | 自动处理 | 请求头自动设置 |
| `client_max_body_size` | `proxy-body-size` | 请求体大小限制 |
| `proxy_cache` | `enable-caching` | 缓存配置 |
| `ssl_certificate` | `tls.secretName` | SSL 证书配置 |

## 详细配置映射

### 1. 负载均衡配置

#### **NGINX 配置**

```nginx
upstream weiyuai {
    ip_hash;
    server 127.0.0.1:9003 weight=2 max_fails=10 fail_timeout=60s;
}

upstream weiyuaiwss {
    ip_hash;
    server 127.0.0.1:9885 weight=2 max_fails=10 fail_timeout=60s;
}
```

#### **Kubernetes Ingress 配置**

```yaml
annotations:
  nginx.ingress.kubernetes.io/affinity: "ip"
  nginx.ingress.kubernetes.io/session-cookie-name: "weiyuai-session"
  nginx.ingress.kubernetes.io/session-cookie-expires: "3600"

spec:
  rules:
  - host: api.weiyuai.cn
    http:
      paths:
      - path: /
        backend:
          service:
            name: bytedesk-service
            port:
              number: 9003
      - path: /websocket
        backend:
          service:
            name: bytedesk-service
            port:
              number: 9885
```

### 2. WebSocket 代理配置

#### **NGINX 配置**

```nginx
location /websocket {
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
    proxy_pass http://weiyuaiwss/websocket;
    
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
}
```

#### **Kubernetes Ingress 配置**

```yaml
annotations:
  nginx.ingress.kubernetes.io/proxy-connect-timeout: "3600"
  nginx.ingress.kubernetes.io/proxy-send-timeout: "3600"
  nginx.ingress.kubernetes.io/proxy-read-timeout: "3600"

spec:
  rules:
  - host: api.weiyuai.cn
    http:
      paths:
      - path: /websocket
        pathType: Prefix
        backend:
          service:
            name: bytedesk-service
            port:
              number: 9885
```

### 3. SSL/TLS 配置

#### **NGINX 配置**

```nginx
server {
    listen 443 ssl;
    ssl_certificate /etc/letsencrypt/live/weiyuai.cn/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/weiyuai.cn/privkey.pem;
    server_name api.weiyuai.cn;
}
```

#### **Kubernetes Ingress 配置**

```yaml
spec:
  tls:
  - hosts:
    - api.weiyuai.cn
    secretName: weiyuai-tls
  rules:
  - host: api.weiyuai.cn
    http:
      paths:
      - path: /
        backend:
          service:
            name: bytedesk-service
            port:
              number: 9003
```

### 4. 缓存配置

#### **NGINX 配置**

```nginx
proxy_cache_path /var/www/html/nginx/cache/webserver levels=1:2 keys_zone=webserver:20m max_size=1g;
proxy_cache_valid 404 10m;
```

#### **Kubernetes Ingress 配置**

```yaml
annotations:
  nginx.ingress.kubernetes.io/enable-caching: "true"
  nginx.ingress.kubernetes.io/cache-zone: "weiyuai_cache"
  nginx.ingress.kubernetes.io/cache-key: "$scheme$request_method$host$request_uri"
  nginx.ingress.kubernetes.io/cache-valid: "200 302 10m, 404 10m"
```

### 5. 安全头配置

#### **NGINX 配置**

```nginx
add_header X-Via $server_addr;
add_header X-Cache $upstream_cache_status;
```

#### **Kubernetes Ingress 配置**

```yaml
annotations:
  nginx.ingress.kubernetes.io/server-snippet: |
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    add_header X-Via $server_addr;
    add_header X-Cache $upstream_cache_status;
    server_tokens off;
```

## 部署步骤

### 1. 创建 TLS Secret

```bash
# 如果有现有的证书文件
kubectl create secret tls weiyuai-tls \
  --cert=path/to/fullchain.pem \
  --key=path/to/privkey.pem \
  -n bytedesk

# 或者使用 cert-manager 自动获取证书
kubectl apply -f - <<EOF
apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
  name: weiyuai-cert
  namespace: bytedesk
spec:
  secretName: weiyuai-tls
  issuerRef:
    name: letsencrypt-prod
    kind: ClusterIssuer
  dnsNames:
  - api.weiyuai.cn
EOF
```

### 2. 应用 Ingress 配置

```bash
# 应用 Ingress 配置
kubectl apply -f ingress-weiyuai.yaml

# 查看 Ingress 状态
kubectl get ingress -n bytedesk
kubectl describe ingress weiyuai-ingress -n bytedesk
```

### 3. 验证配置

```bash
# 测试 HTTP 重定向
curl -I http://api.weiyuai.cn/

# 测试 HTTPS 访问
curl -I https://api.weiyuai.cn/

# 测试 WebSocket 连接
curl -H "Host: api.weiyuai.cn" \
     -H "Upgrade: websocket" \
     -H "Connection: Upgrade" \
     http://<ingress-ip>/websocket/
```

## 优势对比

### Kubernetes Ingress 的优势

1. **自动化管理**
   - 自动服务发现
   - 自动负载均衡
   - 自动证书管理

2. **声明式配置**
   - 版本控制友好
   - 易于回滚
   - 配置即代码

3. **高可用性**
   - 多副本部署
   - 自动故障转移
   - 健康检查

4. **扩展性**
   - 水平扩展
   - 自动扩缩容
   - 多集群支持

### 传统 NGINX 的优势

1. **性能优化**
   - 更精细的调优
   - 自定义模块支持
   - 更低的延迟

2. **功能丰富**
   - 更多 NGINX 特性
   - 自定义配置
   - 第三方模块

3. **运维熟悉**
   - 团队经验丰富
   - 调试工具成熟
   - 文档完善

## 迁移建议

### 1. 渐进式迁移

- 先在测试环境部署 Kubernetes
- 逐步迁移服务
- 保持双活运行

### 2. 配置验证

- 对比功能特性
- 性能测试
- 兼容性检查

### 3. 监控和日志

- 设置监控告警
- 配置日志收集
- 建立运维流程

## 总结

Kubernetes Ingress-Nginx 提供了与传统 NGINX 相同的核心功能，同时增加了自动化、可扩展性和声明式配置的优势。对于微语系统这样的现代化应用，使用 Kubernetes Ingress 是更好的选择。
