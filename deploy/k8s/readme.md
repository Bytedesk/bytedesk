<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-12 10:21:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-03 21:49:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# Kubernetes 部署微语系统

## 文件说明

```bash
.
├── namespace.yaml                    # 创建微语系统专用命名空间
├── configmap.yaml                   # 应用配置映射
├── secret.yaml                      # 敏感信息密钥
├── mysql-deployment.yaml            # MySQL 数据库部署
├── mysql-service.yaml               # MySQL 服务
├── redis-deployment.yaml            # Redis 缓存部署
├── redis-service.yaml               # Redis 服务
├── elasticsearch-deployment.yaml    # Elasticsearch 向量数据库部署
├── elasticsearch-service.yaml       # Elasticsearch 服务
├── artemis-deployment.yaml          # ActiveMQ Artemis 消息队列部署
├── artemis-service.yaml             # Artemis 服务
├── minio-deployment.yaml            # MinIO 对象存储部署
├── minio-service.yaml               # MinIO 服务
├── zipkin-deployment.yaml           # Zipkin 分布式追踪部署
├── zipkin-service.yaml              # Zipkin 服务
├── bytedesk-deployment.yaml         # 微语主应用部署
├── bytedesk-service.yaml            # 微语服务
├── ingress.yaml                     # Ingress 路由配置
├── pvc-mysql.yaml                   # MySQL 持久化存储
├── pvc-redis.yaml                   # Redis 持久化存储
├── pvc-elasticsearch.yaml           # Elasticsearch 持久化存储
├── pvc-artemis.yaml                 # Artemis 持久化存储
├── pvc-minio.yaml                   # MinIO 持久化存储
├── pvc-zipkin.yaml                  # Zipkin 持久化存储
├── pvc-uploads.yaml                 # 文件上传持久化存储
└── ollama-deployment.yaml           # Ollama AI 模型服务（可选）
```

## 前置要求

### 1. Kubernetes 集群
- Kubernetes 1.20+
- kubectl 命令行工具
- 至少 4GB 可用内存
- 至少 20GB 可用存储空间

### 2. 存储类
确保集群中有可用的 StorageClass，用于动态创建持久化卷：

```bash
# 查看可用的存储类
kubectl get storageclass

# 如果没有默认存储类，需要设置一个
kubectl patch storageclass <storage-class-name> -p '{"metadata": {"annotations":{"storageclass.kubernetes.io/is-default-class":"true"}}}'
```

### 3. Ingress Controller
如果使用 Ingress 进行外部访问，需要安装 Ingress Controller：

```bash
# 安装 NGINX Ingress Controller
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml

# 或者使用 Helm 安装
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm install ingress-nginx ingress-nginx/ingress-nginx
```

## 部署步骤

### 1. 克隆项目并进入 K8s 目录

```bash
# 克隆项目
git clone https://github.com/Bytedesk/bytedesk.git

# 进入 K8s 部署目录
cd bytedesk/deploy/k8s
```

### 2. 创建命名空间

```bash
# 创建微语系统专用命名空间
kubectl apply -f namespace.yaml
```

### 3. 配置敏感信息

```bash
# 编辑 secret.yaml 文件，配置数据库密码、API密钥等敏感信息
# 注意：请修改默认密码和密钥
vim secret.yaml

# 应用密钥配置
kubectl apply -f secret.yaml
```

### 4. 部署中间件服务

```bash
# 部署持久化存储
kubectl apply -f pvc-mysql.yaml
kubectl apply -f pvc-redis.yaml
kubectl apply -f pvc-elasticsearch.yaml
kubectl apply -f pvc-artemis.yaml
kubectl apply -f pvc-minio.yaml
kubectl apply -f pvc-zipkin.yaml
kubectl apply -f pvc-uploads.yaml

# 部署 MySQL
kubectl apply -f mysql-deployment.yaml
kubectl apply -f mysql-service.yaml

# 部署 Redis
kubectl apply -f redis-deployment.yaml
kubectl apply -f redis-service.yaml

# 部署 Elasticsearch
kubectl apply -f elasticsearch-deployment.yaml
kubectl apply -f elasticsearch-service.yaml

# 部署 ActiveMQ Artemis
kubectl apply -f artemis-deployment.yaml
kubectl apply -f artemis-service.yaml

# 部署 MinIO
kubectl apply -f minio-deployment.yaml
kubectl apply -f minio-service.yaml

# 部署 Zipkin
kubectl apply -f zipkin-deployment.yaml
kubectl apply -f zipkin-service.yaml
```

### 5. 等待中间件服务就绪

```bash
# 查看所有 Pod 状态
kubectl get pods -n bytedesk

# 等待所有中间件服务 Running 状态
kubectl wait --for=condition=ready pod -l app=mysql -n bytedesk --timeout=300s
kubectl wait --for=condition=ready pod -l app=redis -n bytedesk --timeout=300s
kubectl wait --for=condition=ready pod -l app=elasticsearch -n bytedesk --timeout=300s
kubectl wait --for=condition=ready pod -l app=artemis -n bytedesk --timeout=300s
kubectl wait --for=condition=ready pod -l app=minio -n bytedesk --timeout=300s
```

### 6. 部署微语主应用

```bash
# 应用配置映射
kubectl apply -f configmap.yaml

# 部署微语应用
kubectl apply -f bytedesk-deployment.yaml
kubectl apply -f bytedesk-service.yaml
```

### 7. 配置外部访问

```bash
# 部署 Ingress（可选）
kubectl apply -f ingress.yaml

# 或者使用 NodePort 服务直接访问
kubectl get svc -n bytedesk
```

### 8. 可选：部署 Ollama AI 服务

如果需要使用本地 AI 模型，可以部署 Ollama：

```bash
# 部署 Ollama
kubectl apply -f ollama-deployment.yaml

# 拉取 AI 模型
kubectl exec -it deployment/ollama -n bytedesk -- ollama pull qwen3:0.6b
kubectl exec -it deployment/ollama -n bytedesk -- ollama pull bge-m3:latest
kubectl exec -it deployment/ollama -n bytedesk -- ollama pull linux6200/bge-reranker-v2-m3:latest
```

## 配置说明

### 环境变量配置

主要配置项在 `configmap.yaml` 中，包括：

- **数据库配置**：MySQL 连接信息
- **Redis 配置**：缓存和会话存储
- **Elasticsearch 配置**：向量数据库
- **AI 模型配置**：支持多种 AI 提供商
- **文件上传配置**：本地存储或 MinIO
- **自定义配置**：品牌、功能开关等

### 资源限制

建议的资源分配：

- **微语主应用**：CPU 1-2核，内存 2-4GB
- **MySQL**：CPU 1核，内存 1-2GB
- **Redis**：CPU 0.5核，内存 1GB
- **Elasticsearch**：CPU 1-2核，内存 2-4GB
- **MinIO**：CPU 0.5核，内存 1GB

### 存储配置

- **MySQL 数据**：10-50GB
- **Redis 数据**：1-5GB
- **Elasticsearch 数据**：10-50GB
- **文件上传**：根据业务需求，建议 50GB+
- **MinIO 数据**：根据业务需求

## 访问地址

部署完成后，可以通过以下方式访问：

### 微语系统
- **Web 界面**：http://your-domain 或 http://your-node-ip:30090
- **API 文档**：http://your-domain/swagger-ui/index.html
- **Knife4j 文档**：http://your-domain/doc.html

### 管理界面
- **MinIO Console**：http://your-domain:30091
- **ActiveMQ Artemis Console**：http://your-domain:30181
- **Zipkin**：http://your-domain:30411

### 默认管理员账户
- **邮箱**：admin@email.com
- **密码**：admin
- **手机号**：13345678000
- **验证码**：123456

## 监控和日志

### 查看应用状态

```bash
# 查看所有资源状态
kubectl get all -n bytedesk

# 查看 Pod 状态
kubectl get pods -n bytedesk

# 查看服务状态
kubectl get svc -n bytedesk

# 查看持久化卷
kubectl get pvc -n bytedesk
```

### 查看日志

```bash
# 查看微语应用日志
kubectl logs -f deployment/bytedesk -n bytedesk

# 查看 MySQL 日志
kubectl logs -f deployment/mysql -n bytedesk

# 查看 Redis 日志
kubectl logs -f deployment/redis -n bytedesk

# 查看 Elasticsearch 日志
kubectl logs -f deployment/elasticsearch -n bytedesk
```

### 进入容器调试

```bash
# 进入微语应用容器
kubectl exec -it deployment/bytedesk -n bytedesk -- /bin/bash

# 进入 MySQL 容器
kubectl exec -it deployment/mysql -n bytedesk -- mysql -u root -p

# 进入 Redis 容器
kubectl exec -it deployment/redis -n bytedesk -- redis-cli -a <password>
```

## 升级和回滚

### 升级应用

```bash
# 更新镜像版本
kubectl set image deployment/bytedesk bytedesk=registry.cn-hangzhou.aliyuncs.com/bytedesk/bytedesk:latest -n bytedesk

# 查看升级状态
kubectl rollout status deployment/bytedesk -n bytedesk
```

### 回滚应用

```bash
# 查看部署历史
kubectl rollout history deployment/bytedesk -n bytedesk

# 回滚到上一个版本
kubectl rollout undo deployment/bytedesk -n bytedesk

# 回滚到指定版本
kubectl rollout undo deployment/bytedesk --to-revision=2 -n bytedesk
```

## 备份和恢复

### 数据库备份

```bash
# 备份 MySQL 数据
kubectl exec deployment/mysql -n bytedesk -- mysqldump -u root -p<password> bytedesk > backup.sql

# 备份 Redis 数据
kubectl exec deployment/redis -n bytedesk -- redis-cli -a <password> BGSAVE
```

### 数据恢复

```bash
# 恢复 MySQL 数据
kubectl exec -i deployment/mysql -n bytedesk -- mysql -u root -p<password> bytedesk < backup.sql
```

## 故障排除

### 常见问题

1. **Pod 启动失败**
   ```bash
   # 查看 Pod 详细状态
   kubectl describe pod <pod-name> -n bytedesk
   
   # 查看 Pod 日志
   kubectl logs <pod-name> -n bytedesk
   ```

2. **服务无法访问**
   ```bash
   # 检查服务状态
   kubectl get svc -n bytedesk
   
   # 检查 Endpoints
   kubectl get endpoints -n bytedesk
   ```

3. **存储问题**
   ```bash
   # 检查 PVC 状态
   kubectl get pvc -n bytedesk
   
   # 检查 PV 状态
   kubectl get pv
   ```

4. **网络问题**
   ```bash
   # 检查网络策略
   kubectl get networkpolicy -n bytedesk
   
   # 测试服务连通性
   kubectl run test-pod --image=busybox -n bytedesk --rm -it --restart=Never -- nslookup mysql-service
   ```

### 性能优化

1. **资源调优**
   - 根据实际负载调整 CPU 和内存限制
   - 配置合适的副本数

2. **存储优化**
   - 使用 SSD 存储类提高 I/O 性能
   - 配置合适的存储大小

3. **网络优化**
   - 使用 Service Mesh 进行流量管理
   - 配置合适的网络策略

## 清理资源

```bash
# 删除所有微语相关资源
kubectl delete namespace bytedesk

# 或者逐个删除
kubectl delete -f . -n bytedesk

# 删除持久化存储（谨慎操作，会删除所有数据）
kubectl delete pvc --all -n bytedesk
```

## 许可证说明

请遵守 Business Source License 1.1 许可证条款：
- 不得销售、转售或将微语系统作为服务托管
- 违反条款将自动终止您的许可证权利
- 详细条款：https://github.com/Bytedesk/bytedesk/blob/main/LICENSE

## 技术支持

- **官方网站**：https://www.weiyuai.cn
- **文档地址**：https://www.weiyuai.cn/docs
- **GitHub**：https://github.com/Bytedesk/bytedesk
- **联系方式**：270580156@qq.com
