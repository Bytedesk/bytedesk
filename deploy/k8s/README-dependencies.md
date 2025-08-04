# Bytedesk Kubernetes 依赖服务说明

## 新增的依赖服务

参考 `docker-compose.yaml` 文件，我们为 Kubernetes 部署添加了以下依赖服务：

### 1. Elasticsearch (向量数据库)
- **文件**: `elasticsearch-deployment.yaml`
- **镜像**: `docker.elastic.co/elasticsearch/elasticsearch:8.18.0`
- **端口**: 9200 (HTTP), 9300 (Transport)
- **用途**: 向量数据库，用于AI知识库和语义搜索
- **存储**: 10Gi PVC
- **访问**: http://elasticsearch-service:9200

### 2. Artemis (ActiveMQ 消息队列)
- **文件**: `artemis-deployment.yaml`
- **镜像**: `apache/activemq-artemis:latest`
- **端口**: 
  - 61616 (JMS)
  - 61617 (AMQP)
  - 8161 (Web Console)
  - 5672 (AMQP Alt)
  - 61613 (STOMP)
  - 1883 (MQTT)
- **用途**: 消息队列，用于异步消息处理
- **存储**: 5Gi PVC
- **Web Console**: http://artemis-service:8161/console

### 3. Zipkin (分布式追踪)
- **文件**: `zipkin-deployment.yaml`
- **镜像**: `openzipkin/zipkin:latest`
- **端口**: 9411 (HTTP)
- **用途**: 分布式追踪系统，用于监控和调试
- **存储**: 1Gi PVC
- **访问**: http://zipkin-service:9411

### 4. MinIO (对象存储)
- **文件**: `minio-deployment.yaml`
- **镜像**: `minio/minio:latest`
- **端口**: 9000 (API), 9001 (Console)
- **用途**: 对象存储服务，用于文件存储
- **存储**: 10Gi PVC
- **API**: http://minio-service:9000
- **Console**: http://minio-service:9001

## 配置更新

### Secret 配置
在 `secret.yaml` 中添加了以下密钥：
- `elasticsearch-password`: Elasticsearch 密码
- `elasticsearch-username`: Elasticsearch 用户名
- `artemis-username`: Artemis 用户名
- `artemis-password`: Artemis 密码
- `minio-root-user`: MinIO 根用户
- `minio-root-password`: MinIO 根密码

### Bytedesk 应用配置
在 `bytedesk-deployment.yaml` 中添加了以下环境变量：

#### Zipkin 配置
```yaml
- name: MANAGEMENT_ZIPKIN_TRACING_ENABLED
  value: "false"
- name: MANAGEMENT_ZIPKIN_TRACING_ENDPOINT
  value: "http://zipkin-service:9411/api/v2/spans"
```

#### MinIO 配置
```yaml
- name: BYTEDESK_MINIO_ENABLED
  value: "false"
- name: BYTEDESK_MINIO_ENDPOINT
  value: "http://minio-service:9000"
- name: BYTEDESK_MINIO_ACCESS_KEY
  valueFrom:
    secretKeyRef:
      name: bytedesk-secrets
      key: minio-root-user
- name: BYTEDESK_MINIO_SECRET_KEY
  valueFrom:
    secretKeyRef:
      name: bytedesk-secrets
      key: minio-root-password
- name: BYTEDESK_MINIO_BUCKET_NAME
  value: "bytedesk"
- name: BYTEDESK_MINIO_REGION
  value: "us-east-1"
- name: BYTEDESK_MINIO_SECURE
  value: "false"
```

## 部署顺序

部署脚本 `deploy.sh` 已更新，按以下顺序部署：

1. 创建命名空间
2. 应用密钥配置
3. 清理旧的 PVC
4. 创建新的 PVC
5. 等待 PVC 绑定
6. 部署所有中间件服务
7. 等待中间件就绪
8. 部署 Bytedesk 应用

## 服务访问

### 内部访问
- MySQL: `mysql-service:3306`
- Redis: `redis-service:6379`
- Elasticsearch: `elasticsearch-service:9200`
- Artemis: `artemis-service:61616`
- Zipkin: `zipkin-service:9411`
- MinIO: `minio-service:9000`

### 外部访问
通过 Ingress 或 NodePort 服务暴露到外部。

## 注意事项

1. **资源需求**: 新增服务会增加集群资源消耗，请确保有足够的 CPU 和内存
2. **存储**: 确保有足够的存储空间用于 PVC
3. **网络**: 确保集群内服务间网络通信正常
4. **安全**: 生产环境中请修改默认密码和密钥
5. **监控**: 建议配置监控和日志收集

## 故障排除

### 检查服务状态
```bash
kubectl get pods -n bytedesk
kubectl get svc -n bytedesk
```

### 查看服务日志
```bash
kubectl logs -f deployment/elasticsearch -n bytedesk
kubectl logs -f deployment/artemis -n bytedesk
kubectl logs -f deployment/zipkin -n bytedesk
kubectl logs -f deployment/minio -n bytedesk
```

### 检查 PVC 状态
```bash
kubectl get pvc -n bytedesk
```

### 重启服务
```bash
kubectl rollout restart deployment/elasticsearch -n bytedesk
kubectl rollout restart deployment/artemis -n bytedesk
kubectl rollout restart deployment/zipkin -n bytedesk
kubectl rollout restart deployment/minio -n bytedesk
``` 