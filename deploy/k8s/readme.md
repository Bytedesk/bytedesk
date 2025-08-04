<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-12 10:21:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-04 11:56:40
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
├── deploy.sh                        # 自动化部署脚本（推荐使用）
├── namespace.yaml                   # 创建微语系统专用命名空间
├── configmap.yaml                   # 应用配置映射
├── secret.yaml                      # 敏感信息密钥
├── cleanup.sh                       # PVC 和数据清理脚本
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

## 快速部署

### 使用自动化部署脚本（推荐）

#### 1. 克隆项目并进入 K8s 目录

```bash
# 克隆项目
git clone https://github.com/Bytedesk/bytedesk.git

# 进入 K8s 部署目录
cd bytedesk/deploy/k8s
```

#### 2. 配置敏感信息

```bash
# 编辑 secret.yaml 文件，配置数据库密码、API密钥等敏感信息
# 注意：请修改默认密码和密钥
# 使用 stringData 字段，可以直接使用原文本，无需 base64 编码
vim secret.yaml
```

#### 3. 执行自动化部署脚本

```bash
# 给脚本执行权限
chmod +x deploy.sh

# 执行部署脚本
./deploy.sh
```

- **deploy.sh**：安全部署，保留现有数据
- **cleanup.sh**：独立清理脚本，用于清理所有数据

### 手动部署（可选）

如果需要手动部署，可以参考 `deploy.sh` 脚本中的步骤，或者查看各个 YAML 文件中的注释说明。

**注意**：手动部署需要按顺序执行以下步骤：

1. 创建命名空间
2. 配置密钥
3. 部署持久化存储
4. 部署中间件服务
5. 等待服务就绪
6. 部署主应用
7. 配置外部访问

**推荐使用自动化脚本**，可以避免手动操作错误并节省时间。

### 可选服务部署

#### 部署 Ollama AI 服务

如果需要使用本地 AI 模型，可以部署 Ollama：

```bash
# 部署 Ollama
kubectl apply -f ollama-deployment.yaml

# 拉取 AI 模型
kubectl exec -it deployment/ollama -n bytedesk -- ollama pull qwen3:0.6b
kubectl exec -it deployment/ollama -n bytedesk -- ollama pull bge-m3:latest
kubectl exec -it deployment/ollama -n bytedesk -- ollama pull linux6200/bge-reranker-v2-m3:latest
```

#### 配置外部访问

```bash
# 部署 Ingress（可选）
kubectl apply -f ingress.yaml

# 或者使用 NodePort 服务直接访问
kubectl get svc -n bytedesk
```

### 使用建议

1. **首次部署**：推荐使用 `deploy.sh` 脚本
2. **重新部署**：脚本会自动清理旧资源，适合重新部署
3. **生产环境**：建议先测试脚本，确认无误后再在生产环境使用
4. **自定义部署**：如需自定义部署流程，可参考脚本内容手动执行

### 脚本输出示例

```bash
$ ./deploy.sh
开始部署微语系统（修复版）...
创建命名空间...
应用密钥配置...
清理旧的 PVC...
部署持久化存储...
等待 PVC 绑定...
部署中间件...
等待中间件就绪...
尝试 1/10...
MySQL 状态: Running
Redis 状态: Running
Elasticsearch 状态: Running
Artemis 状态: Running
Zipkin 状态: Running
MinIO 状态: Running
中间件启动成功！
部署微语应用...
部署完成！
查看状态: kubectl get pods -n bytedesk
查看服务: kubectl get svc -n bytedesk
查看日志: kubectl logs -f deployment/mysql -n bytedesk
```

## 配置说明

### Secret 配置

`secret.yaml` 文件包含所有敏感信息，使用 `stringData` 字段可以直接使用原文本，无需 base64 编码：

```yaml
stringData:
  # MySQL 配置
  mysql-root-password: r8FqfdbWUaN3
  mysql-username: root
  mysql-database: bytedesk
  
  # Redis 配置
  redis-password: qfRxz3tVT8Nh
  
  # Elasticsearch 配置
  elasticsearch-password: bytedesk123
  elasticsearch-username: elastic
  
  # JWT 密钥
  jwt-secret-key: 1dfaf8d004207b628a9a6b859c429f49a9a7ead9fd8161c1e60847aeef06dbd2
  
  # MinIO 配置
  minio-root-user: minioadmin
  minio-root-password: minioadmin123
  
  # Artemis 配置
  artemis-username: admin
  artemis-password: admin
  
  # AI API Keys (请替换为实际的 API 密钥)
  zhipuai-api-key: sk-xxx
  openai-api-key: sk-xxx
  # ... 其他 AI 提供商
```

**重要提示**：

- 生产环境中请修改所有默认密码和密钥
- 使用 `stringData` 字段可以直接使用原文本，Kubernetes 会自动进行 base64 编码
- 所有 AI 提供商的 API 密钥都需要替换为实际值
- **数据安全**：deploy.sh 脚本安全部署，保留现有数据；如需清理数据请使用 cleanup.sh
- **数据备份**：重要数据建议定期备份，可使用以下命令：

  ```bash
  # 备份 MySQL 数据
  kubectl exec -n bytedesk deployment/mysql -- mysqldump -u root -p bytedesk > backup.sql
  
  # 备份 MinIO 数据
  kubectl exec -n bytedesk deployment/minio -- mc mirror /data backup/
  ```

### 环境变量配置

配置采用分层管理，避免重复：

**ConfigMap (`configmap.yaml`)**：包含所有非敏感配置
- 基础配置（时区、端口等）
- 应用配置（调试、版本等）
- 自定义配置（品牌、UI等）
- 中间件配置（数据库、Redis、Elasticsearch等）
- AI 配置（模型、参数等）

**Secret (`secret.yaml`)**：包含所有敏感信息
- 数据库密码
- API 密钥
- JWT 密钥
- 中间件认证信息

**Deployment (`bytedesk-deployment.yaml`)**：引用 ConfigMap 和 Secret
- 使用 `envFrom` 引用整个 ConfigMap
- 使用 `env` 单独引用 Secret 中的敏感信息

这种设计避免了配置重复，提高了维护性。

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

- **Web 界面**：<http://your-domain> 或 <http://your-node-ip:30090>
- **API 文档**：<http://your-domain/swagger-ui/index.html>

### 管理界面

- **MinIO Console**：<http://your-domain:30091>
- **ActiveMQ Artemis Console**：<http://your-domain:30181>
- **Zipkin**：<http://your-domain:30411>

### 默认管理员账户

- **邮箱**：<admin@email.com>
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

### deploy.sh 脚本故障排除

#### 脚本执行失败

1. **kubectl 未安装**

   ```bash
   错误: kubectl 未安装
   ```

   **解决方案**：安装 kubectl 命令行工具

   ```bash
   # 下载 kubectl
   curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
   chmod +x kubectl
   sudo mv kubectl /usr/local/bin/
   ```

2. **PVC 绑定失败**

   ```bash
   error: timed out waiting for the condition
   ```

   **解决方案**：检查存储类配置

   ```bash
   # 查看存储类
   kubectl get storageclass
   
   # 检查 PVC 状态
   kubectl get pvc -n bytedesk
   kubectl describe pvc mysql-pvc -n bytedesk
   ```

3. **中间件启动超时**

   ```bash
   警告: 中间件启动超时，继续部署主应用...
   ```

   **解决方案**：手动检查中间件状态

   ```bash
   # 查看 Pod 状态
   kubectl get pods -n bytedesk
   
   # 查看 Pod 详情
   kubectl describe pod <pod-name> -n bytedesk
   
   # 查看 Pod 日志
   kubectl logs <pod-name> -n bytedesk
   ```

4. **权限不足**

   ```bash
   Error from server (Forbidden): ...
   ```

   **解决方案**：检查 RBAC 权限

   ```bash
   # 检查当前用户权限
   kubectl auth can-i create pods --namespace bytedesk
   kubectl auth can-i create pvc --namespace bytedesk
   ```

#### 脚本优化建议

1. **增加超时时间**：如果网络较慢，可以修改脚本中的超时时间
2. **添加更多检查**：可以根据需要添加更多的健康检查
3. **日志记录**：可以添加日志文件记录部署过程

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

### 使用清理脚本（推荐）

```bash
# 运行清理脚本，会提示确认
./cleanup.sh
```

### 重新部署

```bash
# 清理后重新部署
./cleanup.sh
./deploy.sh
```

### 手动清理资源

```bash
# 删除所有微语相关资源
kubectl delete namespace bytedesk

# 或者逐个删除
kubectl delete -f . -n bytedesk

# 删除持久化存储（谨慎操作，会删除所有数据）
kubectl delete pvc --all -n bytedesk
```

### 清理特定服务

```bash
# 只删除主应用，保留数据
kubectl delete deployment bytedesk -n bytedesk
kubectl delete service bytedesk -n bytedesk

# 只删除中间件，保留数据
kubectl delete deployment mysql redis elasticsearch artemis minio zipkin -n bytedesk
kubectl delete service mysql-service redis-service elasticsearch-service artemis-service minio-service zipkin-service -n bytedesk
```

## 许可证说明

请遵守 Business Source License 1.1 许可证条款：

- 不得销售、转售或将微语系统作为服务托管
- 违反条款将自动终止您的许可证权利
- 详细条款：<https://github.com/Bytedesk/bytedesk/blob/main/LICENSE>

## 技术支持

- **官方网站**：<https://www.weiyuai.cn>
- **文档地址**：<https://www.weiyuai.cn/docs>
- **GitHub**：<https://github.com/Bytedesk/bytedesk>
- **联系方式**：<270580156@qq.com>
