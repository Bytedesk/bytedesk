# Helm 与 GitHub Actions 集成指南

## 概述

本文档说明如何将 ByteDesk Helm Chart 与 GitHub Actions 工作流集成，实现自动化的 Kubernetes 部署。

## 集成方案

### 方案一：修改现有工作流

在现有的 `deploy-k8s.yml` 工作流中使用 Helm 进行部署：

```yaml
# .github/workflows/deploy-k8s.yml
name: Deploy to Kubernetes

on:
  workflow_run:
    workflows: ["bytedesk"]
    types:
      - completed
    branches:
      - main
      - master

jobs:
  deploy:
    runs-on: ubuntu-latest
    if: ${{ github.event.workflow_run.conclusion == 'success' }}
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      # 提取版本号
      - name: Extract version
        id: version
        run: |
          TAG_NAME="${{ github.event.workflow_run.head_branch }}"
          if [[ "$TAG_NAME" == v* ]]; then
            VERSION="${TAG_NAME#v}"
          else
            VERSION="$(date +%Y%m%d-%H%M%S)"
          fi
          echo "VERSION=$VERSION" >> $GITHUB_OUTPUT

      # 设置 Helm
      - name: Set up Helm
        uses: azure/setup-helm@v3
        with:
          version: 'latest'

      # 配置 kubectl
      - name: Configure kubectl
        run: |
          mkdir -p $HOME/.kube
          echo "${{ secrets.KUBE_CONFIG }}" | base64 -d > $HOME/.kube/config
          chmod 600 $HOME/.kube/config
          if [ -n "${{ secrets.KUBE_CONTEXT }}" ]; then
            kubectl config use-context ${{ secrets.KUBE_CONTEXT }}
          fi

      # 验证集群连接
      - name: Verify cluster connection
        run: |
          kubectl cluster-info
          kubectl get nodes

      # 使用 Helm 部署
      - name: Deploy with Helm
        run: |
          cd deploy/k8s/helm
          
          # 检查是否已存在 release
          if helm list -n bytedesk | grep -q "^bytedesk"; then
            echo "Upgrading existing release..."
            helm upgrade bytedesk . \
              --set bytedesk.image.tag=${{ steps.version.outputs.VERSION }} \
              --set global.imageRegistry=registry.cn-hangzhou.aliyuncs.com \
              -n bytedesk
          else
            echo "Installing new release..."
            helm install bytedesk . \
              --set bytedesk.image.tag=${{ steps.version.outputs.VERSION }} \
              --set global.imageRegistry=registry.cn-hangzhou.aliyuncs.com \
              -n bytedesk --create-namespace
          fi

      # 等待部署完成
      - name: Wait for deployment
        run: |
          kubectl rollout status deployment/bytedesk-bytedesk -n bytedesk --timeout=300s

      # 健康检查
      - name: Health check
        run: |
          sleep 30
          kubectl get pods -n bytedesk
          kubectl get svc -n bytedesk
          kubectl logs -n bytedesk -l app.kubernetes.io/name=bytedesk --tail=10
```

### 方案二：创建专门的 Helm 工作流

创建新的工作流文件专门用于 Helm 部署：

```yaml
# .github/workflows/helm-deploy.yml
name: Helm Deploy

on:
  workflow_run:
    workflows: ["bytedesk"]
    types:
      - completed
    branches:
      - main
      - master

jobs:
  helm-deploy:
    runs-on: ubuntu-latest
    if: ${{ github.event.workflow_run.conclusion == 'success' }}
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      # 提取版本号
      - name: Extract version
        id: version
        run: |
          TAG_NAME="${{ github.event.workflow_run.head_branch }}"
          if [[ "$TAG_NAME" == v* ]]; then
            VERSION="${TAG_NAME#v}"
          else
            VERSION="$(date +%Y%m%d-%H%M%S)"
          fi
          echo "VERSION=$VERSION" >> $GITHUB_OUTPUT

      # 设置 Helm
      - name: Set up Helm
        uses: azure/setup-helm@v3
        with:
          version: 'latest'

      # 配置 kubectl
      - name: Configure kubectl
        run: |
          mkdir -p $HOME/.kube
          echo "${{ secrets.KUBE_CONFIG }}" | base64 -d > $HOME/.kube/config
          chmod 600 $HOME/.kube/config
          if [ -n "${{ secrets.KUBE_CONTEXT }}" ]; then
            kubectl config use-context ${{ secrets.KUBE_CONTEXT }}
          fi

      # 验证集群连接
      - name: Verify cluster connection
        run: |
          kubectl cluster-info
          kubectl get nodes

      # 使用 Helm 部署
      - name: Deploy with Helm
        run: |
          cd deploy/k8s/helm
          
          # 使用 Helm 部署脚本
          ./deploy.sh -v ${{ steps.version.outputs.VERSION }} -u

      # 验证部署
      - name: Verify deployment
        run: |
          kubectl get pods -n bytedesk
          kubectl get svc -n bytedesk
          kubectl get pvc -n bytedesk
```

## 环境特定部署

### 开发环境部署

```yaml
# 在 GitHub Actions 中使用开发环境配置
- name: Deploy to Development
  run: |
    cd deploy/k8s/helm
    helm upgrade --install bytedesk-dev . \
      -f values-dev.yaml \
      --set bytedesk.image.tag=${{ steps.version.outputs.VERSION }} \
      -n bytedesk-dev --create-namespace
```

### 生产环境部署

```yaml
# 在 GitHub Actions 中使用生产环境配置
- name: Deploy to Production
  run: |
    cd deploy/k8s/helm
    helm upgrade --install bytedesk-prod . \
      -f values-prod.yaml \
      --set bytedesk.image.tag=${{ steps.version.outputs.VERSION }} \
      -n production --create-namespace
```

## 多环境部署策略

### 分支部署策略

```yaml
# 根据分支自动选择环境
- name: Deploy based on branch
  run: |
    cd deploy/k8s/helm
    
    if [[ "${{ github.ref }}" == "refs/heads/develop" ]]; then
      # 开发环境
      helm upgrade --install bytedesk-dev . \
        -f values-dev.yaml \
        --set bytedesk.image.tag=${{ steps.version.outputs.VERSION }} \
        -n bytedesk-dev --create-namespace
    elif [[ "${{ github.ref }}" == "refs/heads/main" ]]; then
      # 生产环境
      helm upgrade --install bytedesk-prod . \
        -f values-prod.yaml \
        --set bytedesk.image.tag=${{ steps.version.outputs.VERSION }} \
        -n production --create-namespace
    fi
```

### 标签部署策略

```yaml
# 根据标签选择环境
- name: Deploy based on tag
  run: |
    cd deploy/k8s/helm
    
    TAG_NAME="${{ github.ref_name }}"
    
    if [[ "$TAG_NAME" == *"-dev" ]]; then
      # 开发环境
      helm upgrade --install bytedesk-dev . \
        -f values-dev.yaml \
        --set bytedesk.image.tag=${{ steps.version.outputs.VERSION }} \
        -n bytedesk-dev --create-namespace
    elif [[ "$TAG_NAME" == *"-prod" ]]; then
      # 生产环境
      helm upgrade --install bytedesk-prod . \
        -f values-prod.yaml \
        --set bytedesk.image.tag=${{ steps.version.outputs.VERSION }} \
        -n production --create-namespace
    fi
```

## 安全配置

### 使用 Kubernetes Secrets

```yaml
# 在 GitHub Actions 中创建 Secrets
- name: Create Kubernetes Secrets
  run: |
    kubectl create secret generic bytedesk-secrets \
      --from-literal=mysql-root-password="${{ secrets.MYSQL_ROOT_PASSWORD }}" \
      --from-literal=mysql-password="${{ secrets.MYSQL_PASSWORD }}" \
      --from-literal=redis-password="${{ secrets.REDIS_PASSWORD }}" \
      --from-literal=elasticsearch-password="${{ secrets.ELASTICSEARCH_PASSWORD }}" \
      --from-literal=artemis-password="${{ secrets.ARTEMIS_PASSWORD }}" \
      --from-literal=minio-password="${{ secrets.MINIO_PASSWORD }}" \
      -n bytedesk --dry-run=client -o yaml | kubectl apply -f -
```

### 使用外部 Secrets 管理

```yaml
# 使用 External Secrets Operator
- name: Deploy with External Secrets
  run: |
    cd deploy/k8s/helm
    helm upgrade --install bytedesk . \
      --set bytedesk.image.tag=${{ steps.version.outputs.VERSION }} \
      --set externalSecrets.enabled=true \
      --set externalSecrets.secretStore=aws-secrets-manager \
      -n bytedesk --create-namespace
```

## 回滚策略

### 自动回滚

```yaml
# 部署失败时自动回滚
- name: Deploy with rollback
  run: |
    cd deploy/k8s/helm
    
    # 部署
    if ! helm upgrade --install bytedesk . \
      --set bytedesk.image.tag=${{ steps.version.outputs.VERSION }} \
      -n bytedesk --create-namespace; then
      
      echo "Deployment failed, rolling back..."
      helm rollback bytedesk -n bytedesk
      exit 1
    fi
```

### 手动回滚

```yaml
# 提供手动回滚选项
- name: Manual rollback
  if: failure()
  run: |
    echo "Deployment failed. To rollback, run:"
    echo "helm rollback bytedesk -n bytedesk"
```

## 监控和通知

### 部署状态通知

```yaml
# 部署完成后发送通知
- name: Notify deployment status
  if: always()
  run: |
    if [ "${{ job.status }}" == "success" ]; then
      echo "✅ Deployment successful"
      # 发送成功通知
    else
      echo "❌ Deployment failed"
      # 发送失败通知
    fi
```

### 健康检查通知

```yaml
# 部署后健康检查
- name: Health check and notify
  run: |
    # 等待应用启动
    sleep 60
    
    # 检查健康状态
    if kubectl exec -it deployment/bytedesk-bytedesk -n bytedesk -- \
       curl -f http://localhost:9003/actuator/health; then
      echo "✅ Application is healthy"
    else
      echo "❌ Application health check failed"
      exit 1
    fi
```

## 最佳实践

### 1. 使用 Helm 依赖管理

```yaml
# Chart.yaml 中添加依赖
dependencies:
  - name: mysql
    version: 9.x.x
    repository: https://charts.bitnami.com/bitnami
  - name: redis
    version: 17.x.x
    repository: https://charts.bitnami.com/bitnami
```

### 2. 使用 Helm 测试

```yaml
# 部署后运行测试
- name: Run Helm tests
  run: |
    helm test bytedesk -n bytedesk
```

### 3. 使用 Helm 插件

```yaml
# 安装有用的 Helm 插件
- name: Install Helm plugins
  run: |
    helm plugin install https://github.com/databus23/helm-diff
    helm plugin install https://github.com/helm/helm-unittest
```

### 4. 使用 Helm 模板验证

```yaml
# 验证 Helm 模板
- name: Validate Helm templates
  run: |
    cd deploy/k8s/helm
    helm template bytedesk . --dry-run --debug
```

## 故障排除

### 常见问题

1. **Helm 版本不兼容**
   ```bash
   # 检查 Helm 版本
   helm version
   ```

2. **模板渲染错误**
   ```bash
   # 调试模板
   helm template bytedesk . --debug
   ```

3. **资源冲突**
   ```bash
   # 检查现有资源
   kubectl get all -n bytedesk
   ```

4. **权限问题**
   ```bash
   # 检查 RBAC
   kubectl auth can-i create deployments -n bytedesk
   ```

### 调试命令

```bash
# 查看 Helm 历史
helm history bytedesk -n bytedesk

# 查看 Helm 状态
helm status bytedesk -n bytedesk

# 查看生成的 YAML
helm get manifest bytedesk -n bytedesk

# 查看 values
helm get values bytedesk -n bytedesk
```

## 总结

通过 Helm 与 GitHub Actions 的集成，可以实现：

1. **自动化部署** - 代码推送后自动部署到 Kubernetes
2. **环境管理** - 支持多环境部署策略
3. **版本控制** - 使用 Git 标签管理版本
4. **回滚能力** - 快速回滚到之前的版本
5. **安全配置** - 使用 Kubernetes Secrets 管理敏感信息
6. **监控通知** - 部署状态实时通知

这种集成方式提供了完整的 CI/CD 流程，大大简化了 Kubernetes 应用的部署和管理。 