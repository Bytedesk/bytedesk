# ByteDesk Helm Chart

ByteDesk 微语智能客服系统的 Helm Chart，支持一键部署完整的应用栈。

## 概述

这个 Helm Chart 包含了 ByteDesk 系统的所有组件：

- **ByteDesk 主应用** - 核心客服系统
- **MySQL** - 关系型数据库
- **Redis** - 缓存和会话存储
- **Elasticsearch** - 搜索引擎
- **Apache Artemis** - 消息队列
- **MinIO** - 对象存储
- **Zipkin** - 链路追踪

## 快速开始

### 前置要求

1. **Kubernetes 集群** (1.19+)
2. **Helm** (3.0+)
3. **kubectl** 配置

### 安装 Helm

```bash
# 安装 Helm
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash

# 验证安装
helm version
```

### 部署 ByteDesk

```bash
# 进入 Helm Chart 目录
cd deploy/k8s/helm

# 使用默认配置部署
./deploy.sh

# 或者使用 Helm 命令
helm install bytedesk . -n bytedesk --create-namespace
```

### 自定义配置部署

```bash
# 使用自定义 values 文件
./deploy.sh -f values-prod.yaml -v v1.0.0

# 部署到不同命名空间
./deploy.sh -n production -r bytedesk-prod

# 试运行模式
./deploy.sh -d
```

## 配置说明

### 默认配置

Chart 提供了合理的默认配置，适合开发和测试环境：

- 所有组件都启用
- 使用默认密码（生产环境请修改）
- 资源限制适中
- 持久化存储启用

### 自定义配置

#### 1. 创建自定义 values 文件

```yaml
# values-prod.yaml
global:
  imageRegistry: "registry.cn-hangzhou.aliyuncs.com"

bytedesk:
  replicaCount: 3
  image:
    tag: "v1.0.0"
  resources:
    limits:
      cpu: 4000m
      memory: 8Gi
    requests:
      cpu: 2000m
      memory: 4Gi

mysql:
  auth:
    rootPassword: "your-secure-password"
    password: "your-secure-password"

redis:
  auth:
    password: "your-secure-password"

elasticsearch:
  auth:
    password: "your-secure-password"

# 禁用不需要的组件
zipkin:
  enabled: false
```

#### 2. 使用自定义配置部署

```bash
helm install bytedesk . -f values-prod.yaml -n production
```

### 环境变量配置

#### ByteDesk 主应用环境变量

```yaml
bytedesk:
  env:
    # 数据库配置
    SPRING_DATASOURCE_URL: "jdbc:mysql://bytedesk-mysql:3306/bytedesk"
    
    # Redis 配置
    SPRING_DATA_REDIS_HOST: "bytedesk-redis"
    SPRING_DATA_REDIS_PORT: "6379"
    
    # Elasticsearch 配置
    SPRING_ELASTICSEARCH_HOST: "bytedesk-elasticsearch"
    SPRING_ELASTICSEARCH_PORT: "9200"
    
    # Artemis 配置
    SPRING_ARTEMIS_HOST: "bytedesk-artemis"
    SPRING_ARTEMIS_PORT: "61616"
    
    # MinIO 配置
    BYTEDESK_MINIO_ENDPOINT: "http://bytedesk-minio:9000"
    BYTEDESK_MINIO_BUCKET: "bytedesk"
    
    # Zipkin 配置
    SPRING_ZIPKIN_BASE_URL: "http://bytedesk-zipkin:9411"
```

## 部署选项

### 1. 开发环境

```bash
# 最小化部署，只包含必要组件
helm install bytedesk-dev . \
  --set zipkin.enabled=false \
  --set elasticsearch.enabled=false \
  --set bytedesk.replicaCount=1 \
  -n bytedesk-dev
```

### 2. 生产环境

```bash
# 高可用部署
helm install bytedesk-prod . \
  --set bytedesk.replicaCount=3 \
  --set mysql.primary.persistence.size=50Gi \
  --set redis.master.persistence.size=20Gi \
  --set elasticsearch.master.persistence.size=100Gi \
  -f values-prod.yaml \
  -n production
```

### 3. 测试环境

```bash
# 使用外部数据库
helm install bytedesk-test . \
  --set mysql.enabled=false \
  --set redis.enabled=false \
  --set bytedesk.env.SPRING_DATASOURCE_URL="jdbc:mysql://external-mysql:3306/bytedesk" \
  --set bytedesk.env.SPRING_DATA_REDIS_HOST="external-redis" \
  -n bytedesk-test
```

## 管理操作

### 查看部署状态

```bash
# 查看所有资源
kubectl get all -n bytedesk

# 查看 Pod 状态
kubectl get pods -n bytedesk

# 查看服务
kubectl get svc -n bytedesk

# 查看持久化存储
kubectl get pvc -n bytedesk
```

### 升级应用

```bash
# 升级到新版本
helm upgrade bytedesk . \
  --set bytedesk.image.tag=v1.0.1 \
  -n bytedesk

# 使用部署脚本
./deploy.sh -u -v v1.0.1
```

### 回滚操作

```bash
# 查看历史版本
helm history bytedesk -n bytedesk

# 回滚到上一个版本
helm rollback bytedesk -n bytedesk

# 回滚到指定版本
helm rollback bytedesk 2 -n bytedesk
```

### 卸载应用

```bash
# 卸载应用（保留数据）
helm uninstall bytedesk -n bytedesk

# 完全清理（包括数据）
kubectl delete namespace bytedesk
```

## 故障排除

### 常见问题

#### 1. Pod 启动失败

```bash
# 查看 Pod 状态
kubectl get pods -n bytedesk

# 查看 Pod 详情
kubectl describe pod <pod-name> -n bytedesk

# 查看日志
kubectl logs <pod-name> -n bytedesk
```

#### 2. 存储问题

```bash
# 检查 PVC 状态
kubectl get pvc -n bytedesk

# 查看存储类
kubectl get storageclass
```

#### 3. 网络问题

```bash
# 检查服务状态
kubectl get svc -n bytedesk

# 测试服务连通性
kubectl run test-pod --image=busybox -n bytedesk --rm -it --restart=Never -- nslookup bytedesk-mysql
```

### 调试命令

```bash
# 进入 Pod 调试
kubectl exec -it <pod-name> -n bytedesk -- /bin/bash

# 端口转发
kubectl port-forward svc/bytedesk 9003:9003 -n bytedesk

# 查看事件
kubectl get events -n bytedesk --sort-by='.lastTimestamp'
```

## 安全配置

### 1. 密码管理

生产环境必须修改默认密码：

```yaml
mysql:
  auth:
    rootPassword: "your-secure-mysql-password"
    password: "your-secure-mysql-password"

redis:
  auth:
    password: "your-secure-redis-password"

elasticsearch:
  auth:
    password: "your-secure-elasticsearch-password"
```

### 2. 网络策略

```yaml
# 创建网络策略限制 Pod 间通信
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: bytedesk-network-policy
  namespace: bytedesk
spec:
  podSelector:
    matchLabels:
      app: bytedesk
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          app: bytedesk
    ports:
    - protocol: TCP
      port: 9003
  egress:
  - to:
    - podSelector:
        matchLabels:
          app: mysql
    ports:
    - protocol: TCP
      port: 3306
```

### 3. RBAC 配置

```yaml
# 创建 ServiceAccount 和 RBAC 规则
apiVersion: v1
kind: ServiceAccount
metadata:
  name: bytedesk-sa
  namespace: bytedesk

---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: bytedesk-role
  namespace: bytedesk
rules:
- apiGroups: [""]
  resources: ["pods", "services", "endpoints"]
  verbs: ["get", "list", "watch"]

---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: bytedesk-rolebinding
  namespace: bytedesk
subjects:
- kind: ServiceAccount
  name: bytedesk-sa
  namespace: bytedesk
roleRef:
  kind: Role
  name: bytedesk-role
  apiGroup: rbac.authorization.k8s.io
```

## 监控和日志

### 1. 健康检查

Chart 配置了健康检查端点：

```bash
# 检查应用健康状态
kubectl exec -it <pod-name> -n bytedesk -- curl http://localhost:9003/actuator/health
```

### 2. 日志收集

```bash
# 查看应用日志
kubectl logs -f deployment/bytedesk -n bytedesk

# 查看所有组件日志
kubectl logs -f -l app=bytedesk -n bytedesk
```

### 3. 监控指标

ByteDesk 应用暴露了 Prometheus 指标：

```bash
# 端口转发访问指标
kubectl port-forward svc/bytedesk 9003:9003 -n bytedesk

# 访问指标端点
curl http://localhost:9003/actuator/prometheus
```

## 扩展和定制

### 1. 添加新的组件

在 `values.yaml` 中添加新组件配置：

```yaml
# 添加新组件
newComponent:
  enabled: true
  image:
    repository: new-component
    tag: "latest"
  resources:
    limits:
      cpu: 500m
      memory: 1Gi
    requests:
      cpu: 250m
      memory: 512Mi
```

### 2. 自定义模板

在 `templates/` 目录下添加新的模板文件：

```yaml
# templates/new-component-deployment.yaml
{{- if .Values.newComponent.enabled }}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ include "bytedesk.fullname" . }}-new-component
  namespace: {{ .Release.Namespace }}
spec:
  # ... 部署配置
{{- end }}
```

### 3. 集成外部服务

```yaml
# 使用外部数据库
bytedesk:
  env:
    SPRING_DATASOURCE_URL: "jdbc:mysql://external-mysql.company.com:3306/bytedesk"
    SPRING_DATA_REDIS_HOST: "external-redis.company.com"
```

## 最佳实践

### 1. 资源管理

- 根据实际负载调整资源限制
- 监控资源使用情况
- 设置合理的 HPA 策略

### 2. 备份策略

- 定期备份数据库
- 备份配置文件
- 测试恢复流程

### 3. 安全加固

- 使用强密码
- 启用网络策略
- 定期更新镜像
- 扫描安全漏洞

### 4. 性能优化

- 调整 JVM 参数
- 优化数据库查询
- 配置缓存策略
- 监控性能指标

## 支持

如果遇到问题，请：

1. 查看本文档的故障排除部分
2. 检查 Kubernetes 事件和日志
3. 参考 ByteDesk 项目文档
4. 提交 Issue 到项目仓库

## 贡献

欢迎贡献代码和改进建议：

1. Fork 项目
2. 创建功能分支
3. 提交 Pull Request
4. 参与讨论

## 许可证

本项目采用 MIT 许可证。 