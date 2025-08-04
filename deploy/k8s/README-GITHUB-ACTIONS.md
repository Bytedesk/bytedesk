# GitHub Actions Kubernetes 部署配置指南

## 概述

本项目配置了两个独立的 GitHub Actions 工作流：
1. **bytedesk.yml** - 负责构建和推送 Docker 镜像
2. **deploy-k8s.yml** - 负责部署到 Kubernetes 集群

这种分离设计提供了更好的模块化和可维护性。

## 配置步骤

### 1. 准备 Kubernetes 集群配置

首先，你需要获取 Kubernetes 集群的访问配置：

```bash
# 获取当前集群配置
kubectl config view --raw

# 或者获取特定上下文的配置
kubectl config view --raw --minify --flatten --context=your-context-name
```

### 2. 在 GitHub 仓库中配置 Secrets

在 GitHub 仓库的 Settings -> Secrets and variables -> Actions 中添加以下 secrets：

#### 必需配置：
- **KUBE_CONFIG**: Kubernetes 集群配置文件内容（base64 编码）
  ```bash
  # 将配置文件内容进行 base64 编码
  kubectl config view --raw | base64 -w 0
  ```

#### 可选配置：
- **KUBE_CONTEXT**: Kubernetes 上下文名称（如果集群有多个上下文）

### 3. 配置示例

```bash
# 1. 获取集群配置并编码
kubectl config view --raw | base64 -w 0

# 2. 复制输出的内容到 GitHub Secrets 的 KUBE_CONFIG 字段

# 3. 如果有多个上下文，设置 KUBE_CONTEXT
# 例如：production-cluster
```

## 工作流说明

### 触发条件
- 当推送以 `v` 开头的标签时触发（例如：`v1.0.0`）

### 执行步骤

#### bytedesk.yml 工作流：
1. **构建阶段** (`build` job)：
   - 构建 Java 项目
   - 构建并推送 Docker 镜像到阿里云和 Docker Hub

#### deploy-k8s.yml 工作流：
1. **部署阶段** (`deploy` job)：
   - 等待主工作流成功完成
   - 配置 kubectl 工具
   - 连接到 Kubernetes 集群
   - 更新部署文件中的镜像版本
   - 部署应用到集群
   - 执行健康检查

### 工作流文件
- `.github/workflows/bytedesk.yml` - 主构建工作流
- `.github/workflows/deploy-k8s.yml` - Kubernetes 部署工作流

### 部署文件
deploy-k8s.yml 工作流会自动使用以下 Kubernetes 配置文件：
- `deploy/k8s/namespace.yaml` - 命名空间
- `deploy/k8s/secret.yaml` - 密钥配置（如果存在）
- `deploy/k8s/configmap.yaml` - 配置映射
- `deploy/k8s/bytedesk-deployment.yaml` - 主应用部署
- `deploy/k8s/bytedesk-service.yaml` - 服务配置

## 使用示例

### 1. 创建新版本标签
```bash
git tag v1.0.0
git push origin v1.0.0
```

### 2. 查看部署状态
```bash
# 在 GitHub Actions 页面查看工作流执行状态
# 或使用 kubectl 查看集群状态
kubectl get pods -n bytedesk
kubectl get svc -n bytedesk
kubectl get deployment -n bytedesk
```

### 3. 查看日志
```bash
kubectl logs -f deployment/bytedesk -n bytedesk
```

## 故障排除

### 常见问题

1. **权限错误**
   - 确保 Kubernetes 集群配置有足够的权限
   - 检查 ServiceAccount 权限

2. **镜像拉取失败**
   - 确保集群可以访问镜像仓库
   - 检查镜像标签是否正确

3. **部署超时**
   - 检查资源限制和请求
   - 查看 Pod 事件和日志

### 调试命令
```bash
# 查看 Pod 状态
kubectl get pods -n bytedesk -o wide

# 查看 Pod 事件
kubectl describe pod <pod-name> -n bytedesk

# 查看服务状态
kubectl get svc -n bytedesk

# 查看部署状态
kubectl get deployment -n bytedesk

# 查看日志
kubectl logs <pod-name> -n bytedesk
```

## 安全注意事项

1. **密钥管理**：确保敏感信息通过 Kubernetes Secrets 管理
2. **网络策略**：配置适当的网络策略限制 Pod 间通信
3. **资源限制**：设置合理的资源请求和限制
4. **镜像安全**：使用可信的镜像源，定期更新基础镜像

## 自定义配置

### 修改部署配置
如果需要修改部署配置，可以编辑 `deploy/k8s/` 目录下的 YAML 文件：

- `bytedesk-deployment.yaml` - 修改副本数、资源限制等
- `configmap.yaml` - 修改应用配置
- `secret.yaml` - 修改密钥配置（注意安全）

### 添加新的 Kubernetes 资源
可以在 `deploy/k8s/` 目录下添加新的 YAML 文件，并在工作流中添加相应的 `kubectl apply` 命令。

## 相关文档

- [Kubernetes 官方文档](https://kubernetes.io/docs/)
- [GitHub Actions 文档](https://docs.github.com/en/actions)
- [kubectl 命令参考](https://kubernetes.io/docs/reference/kubectl/) 